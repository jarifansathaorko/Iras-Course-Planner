import re
with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# 1. Height Adjustment
content = content.replace("val hourSlotHeightDp = 58.dp", "val hourSlotHeightDp = 52.dp")

# 2. Remove Zebra Striping & keep simple horizontal lines
old_guidelines = """                            // 2. Horizontal Hour Guidelines & Labels
                            for (hour in startHour..endHour) {
                                val hMin = hour * 60
                                val yOffset = mapMinutesToY(hMin).dp
                                
                                // Zebra striping (alternating row colors)
                                if (hour % 2 == 0 && hour < endHour) {
                                    Box(
                                        modifier = Modifier
                                            .offset(x = timeColWidth, y = yOffset)
                                            .width(totalGridWidth - timeColWidth)
                                            .height(hourSlotHeightDp)
                                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.05f))
                                    )
                                }

                                // Horizontal guideline
                                Box(
                                    modifier = Modifier
                                        .offset(x = timeColWidth, y = yOffset)
                                        .width(totalGridWidth - timeColWidth)
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                                )"""

new_guidelines = """                            // 2. Horizontal Hour Guidelines & Labels
                            for (hour in startHour..endHour) {
                                val hMin = hour * 60
                                val yOffset = mapMinutesToY(hMin).dp

                                // Horizontal guideline
                                Box(
                                    modifier = Modifier
                                        .offset(x = timeColWidth, y = yOffset)
                                        .width(totalGridWidth - timeColWidth)
                                        .height(0.5.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                                )"""
content = content.replace(old_guidelines, new_guidelines)

# 3. Change AM/PM labels format (e.g., from "8 AM" to matching the screenshot, wait, image shows "8 AM", "9 AM", so "$h12 $amPm" is correct)
# I will make the font size 9.sp and perfectly centered.
old_label = """                                // Time Label
                                Box(
                                    modifier = Modifier
                                        .width(timeColWidth)
                                        .offset(y = yOffset)
                                        .padding(end = 4.dp),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    val amPm = if (hour >= 12) "PM" else "AM"
                                    val h12 = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
                                    Text(
                                        text = "$h12 $amPm",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.offset(y = (-6).dp)
                                    )
                                }"""

new_label = """                                // Time Label
                                Box(
                                    modifier = Modifier
                                        .width(timeColWidth)
                                        .offset(y = yOffset - 7.dp)
                                        .padding(end = 6.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    val amPm = if (hour >= 12) "PM" else "AM"
                                    val h12 = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
                                    Text(
                                        text = "$h12 $amPm",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }"""
content = content.replace(old_label, new_label)

# 4. Box content sizes
old_box = """                                    ) {
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            // Top dark translucent band for time
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Black.copy(alpha = 0.2f))
                                                    .padding(horizontal = 2.dp, vertical = 3.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = parsed.timeRange12Hr.replace(" AM", "").replace(" PM", ""),
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            
                                            // Course info
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 2.dp, vertical = 2.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = course.courseCode,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(1.dp))
                                                Text(
                                                    text = "Sec ${course.section}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    fontSize = 9.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }"""

new_box = """                                    ) {
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            // Top dark translucent band for time
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Black.copy(alpha = 0.2f))
                                                    .padding(horizontal = 1.dp, vertical = 2.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = parsed.timeRange12Hr.replace(" AM", "").replace(" PM", ""),
                                                    color = Color.White,
                                                    fontSize = 7.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            
                                            // Course info
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 0.dp, vertical = 2.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = course.courseCode,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(1.dp))
                                                Text(
                                                    text = "Sec ${course.section}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    fontSize = 8.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }"""
content = content.replace(old_box, new_box)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
