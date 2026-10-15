package com.example.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchSurfaceLow
import com.example.ui.theme.StitchTextPrimary
import com.example.ui.theme.StitchTextSecondary

@Composable
fun UpdateDataDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = StitchSurfaceLow,
            title = {
                Text(text = "Updated Data Available", color = StitchTextPrimary)
            },
            text = {
                Text(
                    text = "A new course catalog update has been detected. Would you like to download and install the latest course schedules now? (Requires Internet)",
                    color = StitchTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Update Now", color = StitchPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Later", color = StitchTextSecondary)
                }
            }
        )
    }
}
