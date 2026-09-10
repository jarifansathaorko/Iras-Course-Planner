import re
with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# Increase available space for day columns
content = content.replace("val timeColWidth = 46.dp", "val timeColWidth = 40.dp")
content = content.replace("val dayColWidth = (availableWidth - timeColWidth - 16.dp)", "val dayColWidth = (availableWidth - timeColWidth - 8.dp)")
content = content.replace("modifier = Modifier\n                        .padding(8.dp)", "modifier = Modifier\n                        .padding(4.dp)")

# Redesign the Course Block
old_block = """                                    Box(
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
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(1.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = parsed.timeRange12Hr.replace(" AM", "").replace(" PM", ""),
                                                color = Color.White,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = course.courseCode,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                color = Color.White,
                                                fontSize = 9.5.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(1.dp))
                                            Text(
                                                text = "Sec ${course.section}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 7.5.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }"""

new_block = """                                    Box(
                                        modifier = Modifier
                                            .offset(x = leftOffsetDp.dp, y = topOffsetDp.dp)
                                            .width(blockWidthDp.dp)
                                            .height(blockHeightDp.dp)
                                            .padding(horizontal = 1.dp, vertical = 1.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(blockColor)
                                            .clickable {
                                                selectedCourseForInfo = course
                                                onCourseClick?.invoke(course)
                                            }
                                    ) {
                                        Text(
                                            text = parsed.timeRange12Hr.replace(" AM", "").replace(" PM", ""),
                                            color = Color.White,
                                            fontSize = 6.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .padding(top = 2.dp)
                                        )
                                        
                                        Column(
                                            modifier = Modifier.align(Alignment.Center).padding(top = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = course.courseCode,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                color = Color.White,
                                                fontSize = 9.5.sp,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "Sec ${course.section}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 7.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }"""
content = content.replace(old_block, new_block)

# Also let's fix the amPm label size which I changed to 9.sp in last turn
# and make sure it has space
content = content.replace("fontSize = 9.sp,\n                                        color = MaterialTheme.colorScheme.outline\n                                    )", "fontSize = 8.sp,\n                                        color = MaterialTheme.colorScheme.outline\n                                    )")


with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
