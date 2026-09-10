package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ConflictResult
import com.example.domain.ScheduleHelper
import com.example.ui.theme.ConflictRed
import com.example.ui.theme.ConflictRedBg
import com.example.ui.theme.ConflictRedBorder
import com.example.ui.viewmodel.ConflictAlert

@Composable
fun ConflictAlertDialog(
    conflictAlert: ConflictAlert,
    onDismiss: () -> Unit,
    onReplaceConflicting: () -> Unit
) {
    val candidate = conflictAlert.candidate
    val conflict = conflictAlert.conflict

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("conflict_alert_dialog"),
        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(ConflictRedBg)
                    .border(1.5.dp, ConflictRedBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Conflict Warning",
                    tint = ConflictRed,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        title = {
            Text(
                text = if (conflict is ConflictResult.SameCourse) "Course Already Selected" else "Schedule Conflict Detected",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = when (conflict) {
                        is ConflictResult.SameCourse ->
                            "You are attempting to select Section ${candidate.section} of ${candidate.courseCode}, but you already have Section ${conflict.existing.section} in your schedule."
                        is ConflictResult.TimeOverlap ->
                            "The schedule for ${candidate.courseCode} (Sec ${candidate.section}) clashes with another course currently in your schedule."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Conflict Detail Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ConflictRedBg)
                        .border(1.dp, ConflictRedBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Candidate:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ConflictRed
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val candParsed = ScheduleHelper.parse(candidate.timeSlot)
                            Text(
                                text = "${candidate.courseCode} Sec ${candidate.section} • ${candParsed.formattedTime}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        val existing = when (conflict) {
                            is ConflictResult.SameCourse -> conflict.existing
                            is ConflictResult.TimeOverlap -> conflict.existing
                        }
                        val existParsed = ScheduleHelper.parse(existing.timeSlot)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Clashes with:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ConflictRed
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${existing.courseCode} Sec ${existing.section} • ${existParsed.formattedTime}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (conflict is ConflictResult.TimeOverlap) {
                            Text(
                                text = "Overlap on: ${conflict.overlappingDays.joinToString(", ") { it.fullName }} (${conflict.clashTimeDescription})",
                                style = MaterialTheme.typography.bodySmall,
                                color = ConflictRed
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onReplaceConflicting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (conflict is ConflictResult.SameCourse) MaterialTheme.colorScheme.primary else ConflictRed
                ),
                modifier = Modifier.testTag("conflict_resolve_button")
            ) {
                Text(
                    text = if (conflict is ConflictResult.SameCourse) "Switch Section" else "Replace Clashing Course",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("conflict_cancel_button")
            ) {
                Text("Keep Current")
            }
        }
    )
}
