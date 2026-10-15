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
import com.example.ui.theme.StitchOutline
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchPrimaryContainer
import com.example.ui.theme.StitchSecondaryContainer
import com.example.ui.theme.StitchSurfaceContainer
import com.example.ui.theme.StitchSurfaceHigh
import com.example.ui.theme.StitchSurfaceLow
import com.example.ui.theme.StitchTertiary
import com.example.ui.theme.StitchTextMuted
import com.example.ui.theme.StitchTextPrimary
import com.example.ui.theme.StitchTextSecondary
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
        containerColor = StitchSurfaceLow,
        icon = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(ConflictRedBg)
                    .border(1.5.dp, ConflictRedBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Conflict Warning",
                    tint = ConflictRed,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = if (conflict is ConflictResult.SameCourse) "Course Already Selected" else "Schedule Conflict Detected",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.01).sp
                ),
                color = StitchTextPrimary
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
                            "You are attempting to select Section ${candidate.section} of ${candidate.courseCode}, but Section ${conflict.existing.section} is already in your schedule."
                        is ConflictResult.TimeOverlap ->
                            "The schedule for ${candidate.courseCode} (Sec ${candidate.section}) clashes with another course currently in your schedule."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = StitchTextSecondary
                )

                // Conflict Detail Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StitchSurfaceContainer)
                        .border(1.dp, ConflictRedBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Candidate Info
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "New Selection Candidate",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp),
                            color = StitchPrimary
                        )
                        val candParsed = ScheduleHelper.parse(candidate.timeSlot)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = candidate.courseCode,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                                color = StitchTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(StitchSurfaceHigh)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Sec ${candidate.section}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                                    color = StitchTextPrimary
                                )
                            }
                        }
                        Text(
                            text = candParsed.formattedTime,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = StitchTextSecondary
                        )
                    }

                    val existing = when (conflict) {
                        is ConflictResult.SameCourse -> conflict.existing
                        is ConflictResult.TimeOverlap -> conflict.existing
                    }
                    val existParsed = ScheduleHelper.parse(existing.timeSlot)

                    // Clashes With Info
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "Clashes with Currently Enrolled",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp),
                            color = ConflictRed
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = existing.courseCode,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                                color = StitchTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(StitchSurfaceHigh)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Sec ${existing.section}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                                    color = StitchTextPrimary
                                )
                            }
                        }
                        Text(
                            text = existParsed.formattedTime,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = StitchTextSecondary
                        )
                    }

                    if (conflict is ConflictResult.TimeOverlap) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Overlap on:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp),
                                color = ConflictRed
                            )
                            Text(
                                text = "${conflict.overlappingDays.joinToString(", ") { it.fullName }} · ${conflict.clashTimeDescription}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
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
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (conflict is ConflictResult.SameCourse) StitchPrimaryContainer else ConflictRedBorder,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("conflict_resolve_button")
            ) {
                Text(
                    text = if (conflict is ConflictResult.SameCourse) "Switch Section" else "Replace Clashing Course",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("conflict_cancel_button")
            ) {
                Text("Keep Current", color = StitchTextSecondary)
            }
        }
    )
}
