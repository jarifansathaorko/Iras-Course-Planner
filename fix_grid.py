import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# 1. Update hourSlotHeightDp to 56.dp
content = content.replace("val hourSlotHeightDp = 46.dp", "val hourSlotHeightDp = 56.dp")

# 2. Fix the Time Text alignment. The reference image centers time labels on the grid lines (like standard calendars)
# Current code puts them inside the block. We'll adjust the padding/alignment for time labels slightly.
target_time_label = """                                    Box(
                                        modifier = Modifier
                                            .width(timeColWidth)
                                            .height(hourSlotHeightDp)
                                            .padding(end = 4.dp),
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        val amPm = if (hour >= 12) "PM" else "AM"
                                        val h12 = if (hour > 12) hour - 12 else hour
                                        Text(
                                            text = "$h12 $amPm",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }"""

replacement_time_label = """                                    Box(
                                        modifier = Modifier
                                            .width(timeColWidth)
                                            .height(hourSlotHeightDp)
                                            .padding(end = 4.dp),
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        val amPm = if (hour >= 12) "PM" else "AM"
                                        val h12 = if (hour > 12) hour - 12 else hour
                                        Text(
                                            text = "$h12 $amPm",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.offset(y = (-6).dp) // Shift up to align with the line
                                        )
                                    }"""
content = content.replace(target_time_label, replacement_time_label)

# 3. Update the Course Block rendering
target_block = """                                    Box(
                                        modifier = Modifier
                                            .offset(x = leftOffsetDp.dp, y = topOffsetDp.dp)
                                            .width(blockWidthDp.dp)
                                            .height(blockHeightDp.dp)
                                            .padding(horizontal = 2.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(blockColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .clickable {
                                                selectedCourseForInfo = course
                                                onCourseClick?.invoke(course)
                                            }
                                            .padding(4.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "${course.courseCode}-${course.section}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                lineHeight = 11.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = parsed.timeRange12Hr.replace(" - ", "\\n"),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.95f),
                                                fontSize = 8.sp,
                                                lineHeight = 9.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }"""

replacement_block = """                                    Box(
                                        modifier = Modifier
                                            .offset(x = leftOffsetDp.dp, y = topOffsetDp.dp)
                                            .width(blockWidthDp.dp)
                                            .height(blockHeightDp.dp)
                                            .padding(horizontal = 2.dp, vertical = 1.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(blockColor)
                                            .clickable {
                                                selectedCourseForInfo = course
                                                onCourseClick?.invoke(course)
                                            }
                                    ) {
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            // Top dark translucent band for time
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Black.copy(alpha = 0.15f))
                                                    .padding(horizontal = 4.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = parsed.timeRange12Hr.replace(" AM", "").replace(" PM", ""),
                                                    color = Color.White.copy(alpha = 0.95f),
                                                    fontSize = 8.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            
                                            // Course info
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Text(
                                                    text = "${course.courseCode}-${course.section}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    lineHeight = 12.sp,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${course.credit} Cr",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White.copy(alpha = 0.95f),
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }"""

content = content.replace(target_block, replacement_block)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)

