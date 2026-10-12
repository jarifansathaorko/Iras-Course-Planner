package com.example.data

import android.content.Context
import com.example.data.entity.CourseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.absoluteValue

object UpdateManager {
    
    // Simulate checking for a version
    suspend fun checkForUpdate(currentVersion: Int): Int {
        // In real Firebase: This would check Firestore document for the latest version.
        // For now, returning a dummy higher version to trigger the UI if needed.
        return currentVersion + 1 
    }

    suspend fun downloadAndParseCsv(urlString: String): List<CourseEntity> = withContext(Dispatchers.IO) {
        val courses = mutableListOf<CourseEntity>()
        var currentUrl = urlString.trim()
        var redirectedConn: HttpURLConnection? = null
        var redirectCount = 0
        val maxRedirects = 5

        try {
            while (redirectCount <= maxRedirects) {
                val conn = URL(currentUrl).openConnection() as HttpURLConnection
                conn.apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "CoursePlanner-Android-App")
                    instanceFollowRedirects = true
                    connectTimeout = 15000
                    readTimeout = 30000
                }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER ||
                    responseCode == 307 || responseCode == 308
                ) {
                    val location = conn.getHeaderField("Location")
                    conn.disconnect()
                    if (location.isNullOrBlank()) {
                        break
                    }
                    currentUrl = location
                    redirectCount++
                } else {
                    redirectedConn = conn
                    break
                }
            }

            val finalConn = redirectedConn ?: return@withContext emptyList()
            if (finalConn.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext emptyList()
            }

            val reader = BufferedReader(InputStreamReader(finalConn.inputStream, Charsets.UTF_8))
            var isFirstLine = true

            reader.forEachLine { rawLine ->
                val line = rawLine.removePrefix("\uFEFF").trim()
                if (line.isBlank()) return@forEachLine

                val delimiter = if (line.contains('\t')) "\t" else ","
                val tokens = line.split("$delimiter(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
                    .map { it.removeSurrounding("\"").trim() }

                if (tokens.isEmpty()) return@forEachLine

                val firstCol = tokens[0]
                if (isFirstLine || firstCol.equals("Course", ignoreCase = true) || firstCol.equals("Course Code", ignoreCase = true)) {
                    isFirstLine = false
                    return@forEachLine
                }

                if (tokens.size >= 4 && firstCol.isNotBlank()) {
                    val courseCode = firstCol
                    val title = tokens.getOrElse(1) { courseCode }.ifBlank { courseCode }
                    val section = tokens.getOrElse(2) { "1" }.toIntOrNull() ?: 1
                    val timeSlot = tokens.getOrElse(3) { "" }.trim()
                    val enrolled = tokens.getOrElse(4) { "0" }.toIntOrNull() ?: 0
                    val capacity = tokens.getOrElse(5) { "0" }.toIntOrNull() ?: 0
                    val credit = tokens.getOrElse(6) { "3" }.toIntOrNull() ?: 3
                    val faculty = tokens.getOrElse(7) { "TBA" }.ifBlank { "TBA" }

                    if (timeSlot.isNotBlank()) {
                        val id = "$courseCode-$section".hashCode().absoluteValue
                        courses.add(
                            CourseEntity(
                                id = id,
                                courseCode = courseCode,
                                title = title,
                                section = section,
                                timeSlot = timeSlot,
                                enrolled = enrolled,
                                capacity = capacity,
                                credit = credit,
                                faculty = faculty
                            )
                        )
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            redirectedConn?.disconnect()
        }

        return@withContext courses
    }
}
