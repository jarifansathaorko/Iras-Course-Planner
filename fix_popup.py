import re

with open("app/src/main/java/com/example/ui/components/ConflictAlertDialog.kt", "r") as f:
    content = f.read()

target = """                // Conflict Detail Box
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
                }"""

replacement = """                // Conflict Detail Boxes
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Candidate Course
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Candidate",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            val candParsed = ScheduleHelper.parse(candidate.timeSlot)
                            Text(
                                text = "${candidate.courseCode} (Sec ${candidate.section})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = candParsed.formattedTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Existing/Conflicting Course
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f))
                            .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Clashes with",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                            val existing = when (conflict) {
                                is ConflictResult.SameCourse -> conflict.existing
                                is ConflictResult.TimeOverlap -> conflict.existing
                            }
                            val existParsed = ScheduleHelper.parse(existing.timeSlot)
                            Text(
                                text = "${existing.courseCode} (Sec ${existing.section})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = existParsed.formattedTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            if (conflict is ConflictResult.TimeOverlap) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Overlap on ${conflict.overlappingDays.joinToString(", ") { it.shortName }}: ${conflict.clashTimeDescription}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/components/ConflictAlertDialog.kt", "w") as f:
    f.write(content)

