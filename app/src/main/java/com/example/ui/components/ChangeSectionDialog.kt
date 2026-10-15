package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.data.entity.CourseEntity
import com.example.domain.ConflictResult
import com.example.domain.ScheduleHelper
import com.example.ui.theme.ConflictRed
import com.example.ui.theme.ConflictRedBg
import com.example.ui.theme.ConflictRedBorder
import com.example.ui.theme.StitchOutline
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchPrimaryContainer
import com.example.ui.theme.StitchSecondary
import com.example.ui.theme.StitchSecondaryContainer
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

@Composable
fun ChangeSectionDialog(
    currentCourse: CourseEntity,
    allCoursesOfCode: List<CourseEntity>,
    selectedCourses: List<CourseEntity>,
    onDismiss: () -> Unit,
    onSelectNewSection: (CourseEntity) -> Unit
) {
    val remainingSelection = selectedCourses.filter { it.id != currentCourse.id }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("change_section_dialog"),
        containerColor = StitchSurfaceLow,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Change Section",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.01).sp
                        ),
                        color = StitchTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${currentCourse.courseCode} • Currently: Sec ${currentCourse.section}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                        color = StitchPrimary
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = StitchTextSecondary)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select an alternative section below. Time slots are verified in real-time against your other enrolled courses.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = StitchTextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allCoursesOfCode, key = { it.id }) { sectionCandidate ->
                        val isCurrent = sectionCandidate.id == currentCourse.id
                        val isFull = sectionCandidate.capacity > 0 && sectionCandidate.enrolled >= sectionCandidate.capacity
                        val conflict = if (isCurrent) null else ScheduleHelper.findConflict(sectionCandidate, remainingSelection)
                        val parsed = ScheduleHelper.parse(sectionCandidate.timeSlot)

                        val backgroundColor = when {
                            isCurrent -> StitchPrimaryContainer.copy(alpha = 0.15f)
                            isFull -> StitchSurfaceContainer.copy(alpha = 0.6f)
                            conflict != null -> ConflictRedBg
                            else -> StitchSurfaceContainer
                        }

                        val borderColor = when {
                            isCurrent -> StitchPrimaryContainer
                            isFull -> StitchOutline.copy(alpha = 0.4f)
                            conflict != null -> ConflictRedBorder
                            else -> StitchOutline
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = backgroundColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("section_candidate_${sectionCandidate.section}")
                                .clickable(enabled = !isCurrent && !isFull) {
                                    onSelectNewSection(sectionCandidate)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Section Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isCurrent) StitchPrimaryContainer else StitchSurfaceHigh)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Sec ${sectionCandidate.section}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                        color = if (isCurrent) Color.White else StitchTextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = parsed.formattedTime,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                                        color = StitchTextPrimary
                                    )
                                    Text(
                                        text = if (sectionCandidate.faculty.isNotBlank()) sectionCandidate.faculty else "TBA",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = StitchTextSecondary
                                    )

                                    if (isFull && !isCurrent) {
                                        Text(
                                            text = "Section Full (${sectionCandidate.enrolled}/${sectionCandidate.capacity})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp),
                                            color = ConflictRed
                                        )
                                    } else if (conflict != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = ConflictRed,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            val conflictMsg = when (conflict) {
                                                is ConflictResult.TimeOverlap -> "Clashes with ${conflict.existing.courseCode}"
                                                is ConflictResult.SameCourse -> "Already selected"
                                            }
                                            Text(
                                                text = conflictMsg,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                                color = ConflictRed
                                            )
                                        }
                                    } else if (!isCurrent) {
                                        Text(
                                            text = "Available • No Conflict",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                            color = StitchTertiary
                                        )
                                    }
                                }

                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(StitchPrimaryContainer.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Current",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                            color = StitchPrimary
                                        )
                                    }
                                } else if (isFull) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Full",
                                        tint = StitchTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Switch",
                                        tint = if (conflict != null) ConflictRed else StitchPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = StitchTextSecondary)
            }
        }
    )
}
