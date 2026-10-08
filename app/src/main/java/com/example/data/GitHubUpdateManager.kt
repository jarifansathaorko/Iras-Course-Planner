package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class GitHubRelease(
    val tagName: String,
    val name: String,
    val body: String,
    val publishedAt: String,
    val apkDownloadUrl: String?,
    val apkFileName: String?,
    val apkSize: Long
)

sealed interface UpdateCheckState {
    data object Idle : UpdateCheckState
    data object Checking : UpdateCheckState
    data class UpdateAvailable(
        val currentVersion: String,
        val release: GitHubRelease
    ) : UpdateCheckState
    data class UpToDate(
        val currentVersion: String,
        val latestVersion: String
    ) : UpdateCheckState
    data class Error(val message: String) : UpdateCheckState
}

sealed interface DownloadState {
    data object Idle : DownloadState
    data class Downloading(
        val progress: Float,
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : DownloadState
    data class Completed(val file: File) : DownloadState
    data class Error(val message: String) : DownloadState
}

object GitHubUpdateManager {

    private const val PREFS_NAME = "github_update_prefs"
    private const val KEY_REPO = "github_repo"
    const val DEFAULT_REPO = "jarifansathaorko/CoursePlanner"

    fun getRepository(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_REPO, DEFAULT_REPO)?.trim() ?: DEFAULT_REPO
    }

    fun setRepository(context: Context, repo: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_REPO, repo.trim()).apply()
    }

    suspend fun checkForUpdates(
        context: Context,
        repoOwnerAndName: String = getRepository(context)
    ): UpdateCheckState = withContext(Dispatchers.IO) {
        val targetRepo = repoOwnerAndName.trim().removePrefix("https://github.com/").removeSuffix("/")
        if (!targetRepo.contains("/")) {
            return@withContext UpdateCheckState.Error("Invalid repository format. Please use 'owner/repo' (e.g. $DEFAULT_REPO)")
        }

        try {
            val url = URL("https://api.github.com/repos/$targetRepo/releases/latest")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "CoursePlanner-Android-App")
                connectTimeout = 10000
                readTimeout = 15000
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return@withContext UpdateCheckState.Error("No releases found for GitHub repo: $targetRepo (or repository is private)")
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext UpdateCheckState.Error("GitHub API returned error code: $responseCode")
            }

            val responseText = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(responseText)

            val tagName = json.optString("tag_name", "").trim()
            val releaseName = json.optString("name", tagName)
            val body = json.optString("body", "No release notes provided.")
            val publishedAt = json.optString("published_at", "")

            var apkUrl: String? = null
            var apkName: String? = null
            var apkSize = 0L

            val assets = json.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        apkUrl = asset.optString("browser_download_url")
                        apkName = name
                        apkSize = asset.optLong("size", 0L)
                        break
                    }
                }
            }

            val currentVersion = BuildConfig.VERSION_NAME
            val release = GitHubRelease(
                tagName = tagName,
                name = releaseName,
                body = body,
                publishedAt = publishedAt,
                apkDownloadUrl = apkUrl,
                apkFileName = apkName,
                apkSize = apkSize
            )

            if (isNewerVersion(tagName, currentVersion)) {
                UpdateCheckState.UpdateAvailable(
                    currentVersion = currentVersion,
                    release = release
                )
            } else {
                UpdateCheckState.UpToDate(
                    currentVersion = currentVersion,
                    latestVersion = tagName
                )
            }
        } catch (e: Exception) {
            UpdateCheckState.Error(e.localizedMessage ?: "Failed to check for updates from GitHub")
        }
    }

    /**
     * Compare semantic versions.
     * Returns true if remote is strictly newer than local.
     */
    fun isNewerVersion(remoteTag: String, currentVersion: String): Boolean {
        val cleanRemote = cleanVersion(remoteTag)
        val cleanCurrent = cleanVersion(currentVersion)

        val remoteParts = cleanRemote.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = cleanCurrent.split(".").mapNotNull { it.toIntOrNull() }

        val maxLength = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until maxLength) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }

    private fun cleanVersion(v: String): String {
        return v.trim()
            .removePrefix("v")
            .removePrefix("V")
            .split("-")[0] // ignore build suffixes like -beta
    }

    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        fileName: String,
        onProgress: (progress: Float, downloadedBytes: Long, totalBytes: Long) -> Unit
    ): File = withContext(Dispatchers.IO) {
        val updatesDir = File(context.cacheDir, "updates")
        if (!updatesDir.exists()) {
            updatesDir.mkdirs()
        }

        val destinationFile = File(updatesDir, fileName.ifBlank { "CoursePlanner-update.apk" })
        if (destinationFile.exists()) {
            destinationFile.delete()
        }

        val url = URL(downloadUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "GET"
            instanceFollowRedirects = true
            connectTimeout = 15000
            readTimeout = 30000
        }

        // Handle redirects manually if needed (GitHub asset downloads usually redirect to AWS S3)
        var redirectedConn: HttpURLConnection = connection
        var redirectCode = redirectedConn.responseCode
        var redirectCount = 0
        while ((redirectCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                redirectCode == HttpURLConnection.HTTP_MOVED_PERM ||
                redirectCode == HttpURLConnection.HTTP_SEE_OTHER ||
                redirectCode == 307 || redirectCode == 308) && redirectCount < 5) {
            val newUrl = redirectedConn.getHeaderField("Location")
            redirectedConn.disconnect()
            redirectedConn = URL(newUrl).openConnection() as HttpURLConnection
            redirectedConn.connectTimeout = 15000
            redirectedConn.readTimeout = 30000
            redirectCode = redirectedConn.responseCode
            redirectCount++
        }

        if (redirectedConn.responseCode != HttpURLConnection.HTTP_OK) {
            throw IllegalStateException("Failed to download update: HTTP ${redirectedConn.responseCode}")
        }

        val totalLength = redirectedConn.contentLengthLong
        var totalRead = 0L

        redirectedConn.inputStream.use { input ->
            FileOutputStream(destinationFile).use { output ->
                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    totalRead += bytesRead
                    val progress = if (totalLength > 0) totalRead.toFloat() / totalLength else 0f
                    onProgress(progress, totalRead, totalLength)
                }
                output.flush()
            }
        }

        destinationFile
    }

    fun triggerPackageInstall(context: Context, apkFile: File): Boolean {
        if (!apkFile.exists()) return false

        // On Android 8.0+ (API 26), verify if app can request package installs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(settingsIntent)
                return false
            }
        }

        val authority = "${context.packageName}.fileprovider"
        val contentUri = FileProvider.getUriForFile(context, authority, apkFile)

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(installIntent)
        return true
    }
}
