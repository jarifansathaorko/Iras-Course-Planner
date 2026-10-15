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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CourseEntity
import com.example.data.entity.PlanMetaEntity
import com.example.ui.theme.StitchOutline
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchPrimaryContainer
import com.example.ui.theme.StitchSecondary
import com.example.ui.theme.StitchSurfaceContainer
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.StitchSurfaceHigh
import com.example.ui.theme.StitchSurfaceLow
import com.example.ui.theme.StitchTertiary
import com.example.ui.theme.StitchTextMuted
import com.example.ui.theme.StitchTextPrimary
import com.example.ui.theme.StitchTextSecondary

@Composable
fun ConfirmScheduleDialog(
    selectedCourses: List<CourseEntity>,
    allPlanMeta: List<PlanMetaEntity>,
    plan1Count: Int,
    plan2Count: Int,
    plan3Count: Int,
    plan4Count: Int,
    onDismiss: () -> Unit,
    onConfirm: (planNumber: Int, planName: String) -> Unit
) {
    var chosenPlanNumber by remember { mutableIntStateOf(1) }
    var planName by remember(chosenPlanNumber) {
        val existingMeta = allPlanMeta.find { it.planNumber == chosenPlanNumber }
        mutableStateOf(existingMeta?.planName ?: "Plan $chosenPlanNumber")
    }

    val totalCredits = selectedCourses.sumOf { it.credit }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("confirm_schedule_dialog"),
        containerColor = StitchSurfaceLow,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Save Schedule to Plans",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.01).sp
                        ),
                        color = StitchTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${selectedCourses.size} Courses • $totalCredits Credits",
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select destination plan slot to store your active routine:",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = StitchTextSecondary
                )

                val planCounts = mapOf(1 to plan1Count, 2 to plan2Count, 3 to plan3Count, 4 to plan4Count)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (p in 1..4) {
                        val isSelected = chosenPlanNumber == p
                        val meta = allPlanMeta.find { it.planNumber == p }
                        val count = planCounts[p] ?: 0

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) StitchPrimaryContainer.copy(alpha = 0.15f) else StitchSurfaceContainer,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) StitchPrimaryContainer else StitchOutline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_plan_radio_$p")
                                .clickable {
                                    chosenPlanNumber = p
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { chosenPlanNumber = p },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = StitchPrimary,
                                        unselectedColor = StitchOutline
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = meta?.planName ?: "Plan $p",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        ),
                                        color = StitchTextPrimary
                                    )
                                    Text(
                                        text = if (count > 0) "$count courses (will overwrite)" else "Empty slot",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = if (count > 0) StitchSecondary else StitchTextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // Custom Plan Name input
                OutlinedTextField(
                    value = planName,
                    onValueChange = { planName = it },
                    label = { Text("Plan Label / Name", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StitchSurfaceContainer,
                        unfocusedContainerColor = StitchSurfaceContainer,
                        focusedBorderColor = StitchPrimaryContainer,
                        unfocusedBorderColor = StitchOutline,
                        focusedTextColor = StitchTextPrimary,
                        unfocusedTextColor = StitchTextPrimary,
                        focusedLabelColor = StitchPrimary,
                        unfocusedLabelColor = StitchTextMuted
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("plan_name_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(chosenPlanNumber, planName.trim().ifEmpty { "Plan $chosenPlanNumber" }) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StitchPrimaryContainer,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("save_plan_confirm_button")
            ) {
                Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Confirm & Save", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = StitchTextSecondary)
            }
        }
    )
}
