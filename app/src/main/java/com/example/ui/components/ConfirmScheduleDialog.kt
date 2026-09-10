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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.CourseEntity
import com.example.data.entity.PlanMetaEntity
import com.example.ui.theme.SelectedBlueBg
import com.example.ui.theme.SelectedBlueBorder

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
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Save Schedule to Plans",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${selectedCourses.size} Courses • $totalCredits Credits",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Select which plan slot to save your current course schedule to:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val planCounts = mapOf(1 to plan1Count, 2 to plan2Count, 3 to plan3Count, 4 to plan4Count)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (p in 1..4) {
                        val isSelected = chosenPlanNumber == p
                        val meta = allPlanMeta.find { it.planNumber == p }
                        val count = planCounts[p] ?: 0

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
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
                                    onClick = { chosenPlanNumber = p }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = meta?.planName ?: "Plan $p",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (count > 0) "$count courses saved (will overwrite)" else "Empty slot",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (count > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline
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
                    label = { Text("Plan Label") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("plan_name_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(chosenPlanNumber, planName.trim().ifEmpty { "Plan $chosenPlanNumber" }) },
                modifier = Modifier.testTag("save_plan_confirm_button")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Confirm & Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
