import re

with open("app/src/main/java/com/example/ui/components/ConflictAlertDialog.kt", "r") as f:
    content = f.read()

target_pattern = r"// Conflict Detail Box\s+Box\([\s\S]*?\}\s+\}\s+\}"

replacement = """// Conflict Detail Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, ConflictRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Candidate Info
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Candidate",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        val candParsed = ScheduleHelper.parse(candidate.timeSlot)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = candidate.courseCode,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sec ${candidate.section}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = candParsed.formattedTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    val existing = when (conflict) {
                        is ConflictResult.SameCourse -> conflict.existing
                        is ConflictResult.TimeOverlap -> conflict.existing
                    }
                    val existParsed = ScheduleHelper.parse(existing.timeSlot)
                    
                    // Clashes With Info
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Clashes with",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ConflictRed
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = existing.courseCode,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sec ${existing.section}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = existParsed.formattedTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (conflict is ConflictResult.TimeOverlap) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Overlap on:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ConflictRed
                            )
                            Text(
                                text = "${conflict.overlappingDays.joinToString(", ") { it.fullName }} · ${conflict.clashTimeDescription}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ConflictRed
                            )
                        }
                    }
                }"""

new_content = re.sub(target_pattern, replacement, content, count=1)

if new_content != content:
    with open("app/src/main/java/com/example/ui/components/ConflictAlertDialog.kt", "w") as f:
        f.write(new_content)
    print("Successfully replaced.")
else:
    print("Target not found.")

