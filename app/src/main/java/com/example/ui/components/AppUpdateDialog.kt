package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DownloadState
import com.example.data.GitHubRelease
import com.example.data.UpdateCheckState
import com.example.ui.theme.ConflictRed
import com.example.ui.theme.ConflictRedBg
import com.example.ui.theme.ConflictRedBorder
import com.example.ui.theme.StitchOutline
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchPrimaryContainer
import com.example.ui.theme.StitchSecondary
import com.example.ui.theme.StitchSurfaceContainer
import com.example.ui.theme.StitchSurfaceHigh
import com.example.ui.theme.StitchSurfaceLow
import com.example.ui.theme.StitchTertiary
import com.example.ui.theme.StitchTextMuted
import com.example.ui.theme.StitchTextPrimary
import com.example.ui.theme.StitchTextSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg
import com.example.ui.theme.SuccessGreenBorder
import java.io.File
import java.util.Locale

@Composable
fun AppUpdateDialog(
    showDialog: Boolean,
    currentVersion: String,
    repoName: String,
    checkState: UpdateCheckState,
    downloadState: DownloadState,
    onDismiss: () -> Unit,
    onCheckUpdate: (String) -> Unit,
    onSaveRepo: (String) -> Unit,
    onDownloadAndInstall: (GitHubRelease) -> Unit,
    onInstallApk: (File) -> Unit
) {
    if (!showDialog) return

    var isEditingRepo by remember { mutableStateOf(false) }
    var repoInput by remember(repoName) { mutableStateOf(repoName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("app_update_dialog"),
        containerColor = StitchSurfaceLow,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = StitchPrimaryContainer.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "App Update",
                            tint = StitchPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "App Updates",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = (-0.01).sp
                        ),
                        color = StitchTextPrimary
                    )
                    Text(
                        text = "GitHub Manual Updater",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = StitchTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info Banner (Current Version & GitHub Repo)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = StitchSurfaceContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Installed Version:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = StitchTextSecondary
                            )
                            Surface(
                                color = StitchPrimaryContainer.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "v$currentVersion",
                                    fontWeight = FontWeight.Bold,
                                    color = StitchPrimary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // GitHub Repo row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Repository:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = StitchTextSecondary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = repoName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = StitchTextPrimary
                                )
                                IconButton(
                                    onClick = { isEditingRepo = !isEditingRepo },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Repository",
                                        modifier = Modifier.size(15.dp),
                                        tint = StitchPrimary
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(visible = isEditingRepo) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                OutlinedTextField(
                                    value = repoInput,
                                    onValueChange = { repoInput = it },
                                    label = { Text("GitHub Repo (owner/repo)") },
                                    placeholder = { Text("owner/repo") },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = StitchTextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = StitchPrimary,
                                        unfocusedBorderColor = StitchOutline,
                                        focusedLabelColor = StitchPrimary,
                                        unfocusedLabelColor = StitchTextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            isEditingRepo = false
                                            repoInput = repoName
                                        }
                                    ) {
                                        Text("Cancel", color = StitchTextSecondary)
                                    }
                                    Button(
                                        onClick = {
                                            onSaveRepo(repoInput.trim())
                                            isEditingRepo = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = StitchPrimaryContainer,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Save")
                                    }
                                }
                            }
                        }
                    }
                }

                // Dynamic Status Section based on Check & Download state
                when (checkState) {
                    is UpdateCheckState.Idle -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    StitchSurfaceContainer,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, StitchOutline, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Ready to check for latest updates from GitHub.",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp),
                                    color = StitchTextSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { onCheckUpdate(repoName) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StitchPrimaryContainer,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("btn_check_github_now")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Check for Updates")
                                }
                            }
                        }
                    }

                    is UpdateCheckState.Checking -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(34.dp),
                                    color = StitchPrimary,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Connecting to GitHub Releases...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp),
                                    color = StitchTextSecondary
                                )
                            }
                        }
                    }

                    is UpdateCheckState.UpToDate -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = SuccessGreenBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreenBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "You're up to date!",
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        fontSize = 14.5.sp
                                    )
                                    Text(
                                        text = "Version ${checkState.latestVersion} is the latest release.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = StitchTextSecondary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedButton(
                                onClick = { onCheckUpdate(repoName) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = StitchPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Check Again")
                            }
                        }
                    }

                    is UpdateCheckState.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = ConflictRedBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ConflictRedBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = ConflictRed,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Update Check Failed",
                                        fontWeight = FontWeight.Bold,
                                        color = ConflictRed,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = checkState.message,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = StitchTextSecondary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { onCheckUpdate(repoName) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StitchPrimaryContainer,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Retry Check")
                            }
                        }
                    }

                    is UpdateCheckState.UpdateAvailable -> {
                        val release = checkState.release
                        val formattedSize = if (release.apkSize > 0) {
                            String.format(Locale.US, "%.1f MB", release.apkSize / (1024f * 1024f))
                        } else null

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = StitchSurfaceContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StitchPrimaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "🎉 New Update Available!",
                                            fontWeight = FontWeight.Bold,
                                            color = StitchPrimary,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = release.name.ifBlank { release.tagName },
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                            color = StitchTextSecondary
                                        )
                                    }
                                    if (formattedSize != null) {
                                        Surface(
                                            color = StitchSurfaceHigh,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = formattedSize,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                fontWeight = FontWeight.SemiBold,
                                                color = StitchSecondary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                if (release.body.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Release Notes:",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                                        color = StitchTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = StitchSurfaceLow,
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 140.dp)
                                    ) {
                                        Text(
                                            text = release.body,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                            color = StitchTextSecondary,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .verticalScroll(rememberScrollState())
                                        )
                                    }
                                }

                                // Download States UI
                                Spacer(modifier = Modifier.height(14.dp))
                                when (downloadState) {
                                    is DownloadState.Idle -> {
                                        if (release.apkDownloadUrl != null) {
                                            Button(
                                                onClick = { onDownloadAndInstall(release) },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("btn_download_install_apk"),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = StitchPrimaryContainer,
                                                    contentColor = Color.White
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CloudDownload,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Download & Install Update")
                                            }
                                        } else {
                                            Text(
                                                text = "No .apk asset attached to this GitHub release.",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                                color = ConflictRed
                                            )
                                        }
                                    }

                                    is DownloadState.Downloading -> {
                                        val percent = (downloadState.progress * 100).toInt()
                                        val downloadedMb = downloadState.downloadedBytes / (1024f * 1024f)
                                        val totalMb = downloadState.totalBytes / (1024f * 1024f)

                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Downloading update...",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                                    fontWeight = FontWeight.Medium,
                                                    color = StitchTextPrimary
                                                )
                                                Text(
                                                    text = "$percent%",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                                    fontWeight = FontWeight.Bold,
                                                    color = StitchPrimary
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            LinearProgressIndicator(
                                                progress = { downloadState.progress },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(4.dp)),
                                                color = StitchPrimary,
                                                trackColor = StitchSurfaceHigh
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = String.format(Locale.US, "%.1f MB / %.1f MB", downloadedMb, totalMb),
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                                color = StitchTextMuted
                                            )
                                        }
                                    }

                                    is DownloadState.Completed -> {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "✓ Download finished! Ready to install.",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = { onInstallApk(downloadState.file) },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = StitchTertiary,
                                                    contentColor = Color(0xFF003824)
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.SystemUpdate,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Install Update Now", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    is DownloadState.Error -> {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Download failed: ${downloadState.message}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                                color = ConflictRed
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedButton(
                                                onClick = { onDownloadAndInstall(release) },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = StitchPrimary
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline)
                                            ) {
                                                Text("Retry Download")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_close_app_update_dialog")
            ) {
                Text("Close", color = StitchTextSecondary)
            }
        }
    )
}
