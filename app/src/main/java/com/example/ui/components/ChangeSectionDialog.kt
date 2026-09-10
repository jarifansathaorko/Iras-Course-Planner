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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CourseEntity
import com.example.domain.ConflictResult
import com.example.domain.ScheduleHelper
import com.example.ui.theme.ConflictRed
import com.example.ui.theme.ConflictRedBg
import com.example.ui.theme.SelectedBlueBg
import com.example.ui.theme.SelectedBlueBorder
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg

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
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Change Section",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${currentCourse.courseCode}: ${currentCourse.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select an available section below. Conflict detection checks each slot against your remaining schedule.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            isCurrent -> SelectedBlueBg
                            isFull -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            conflict != null -> ConflictRedBg
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val borderColor = when {
                            isCurrent -> SelectedBlueBorder
                            isFull -> MaterialTheme.colorScheme.outlineVariant
                            conflict != null -> ConflictRed.copy(alpha = 0.4f)
                            else -> MaterialTheme.colorScheme.outlineVariant
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
                                // Section Chip
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Sec ${sectionCandidate.section}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = parsed.formattedTime,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = sectionCandidate.faculty,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (isFull && !isCurrent) {
                                        Text(
                                            text = "Section Full (${sectionCandidate.enrolled}/${sectionCandidate.capacity})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
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
                                                modifier = Modifier.width(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            val conflictMsg = when (conflict) {
                                                is ConflictResult.TimeOverlap -> "Clashes with ${conflict.existing.courseCode}"
                                                is ConflictResult.SameCourse -> "Already selected"
                                            }
                                            Text(
                                                text = conflictMsg,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ConflictRed
                                            )
                                        }
                                    } else if (!isCurrent) {
                                        Text(
                                            text = "Available • No Conflict",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SuccessGreen
                                        )
                                    }
                                }

                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Current",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else if (isFull) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Full",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Switch",
                                        tint = if (conflict != null) ConflictRed else MaterialTheme.colorScheme.primary
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
                Text("Cancel")
            }
        }
    )
}
