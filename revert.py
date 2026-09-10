import re
with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# 1. Math replacements: replace mapMinutesToY with simple calculation
# Find the start of mergedIntervals and remove down to totalHeightDp
start_idx = content.find("val freeTimeBlockHeightDp")
if start_idx != -1:
    end_idx = content.find("val totalHeightDp = mapMinutesToY(endDayMinutes).dp") + len("val totalHeightDp = mapMinutesToY(endDayMinutes).dp")
    replacement = """    val totalHeightDp = ((endDayMinutes - startDayMinutes) / 60f * hourSlotHeightDp.value).dp
    val mapMinutesToY = { minutes: Int ->
        ((minutes - startDayMinutes) / 60f) * hourSlotHeightDp.value
    }"""
    content = content[:start_idx] + replacement + content[end_idx:]

# 2. Revert horizontal hour guidelines and remove Free Time blocks
old_grid = """                            // 2. Horizontal Hour Guidelines & Labels
                            for (hour in startHour..endHour) {
                                val hMin = hour * 60
                                val inGap = freeGaps.any { gap -> hMin > gap.first && hMin < gap.last }
                                if (!inGap) {
                                    val yOffset = mapMinutesToY(hMin).dp
                                    // Horizontal guideline
                                    Box(
                                        modifier = Modifier
                                            .offset(x = timeColWidth, y = yOffset)
                                            .width(totalGridWidth - timeColWidth)
                                            .height(0.5.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                                    )
                                    // Time Label
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
                                    }
                                }
                            }

                            // 3. Free Time Blocks
                            for (gap in freeGaps) {
                                val topOffsetDp = mapMinutesToY(gap.first).dp
                                Box(
                                    modifier = Modifier
                                        .offset(x = timeColWidth + 1.dp, y = topOffsetDp)
                                        .width(totalGridWidth - timeColWidth - 2.dp)
                                        .height(freeTimeBlockHeightDp.dp)
                                        .padding(vertical = 1.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val startParsed = ScheduleHelper.formatMinutesTo12Hr(gap.first)
                                    val endParsed = ScheduleHelper.formatMinutesTo12Hr(gap.last)
                                    Text(
                                        text = "FREE TIME • $startParsed - $endParsed",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }"""

new_grid = """                            // 2. Horizontal Hour Guidelines & Labels
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
                                )
                                // Time Label
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
                                }
                            }"""
content = content.replace(old_grid, new_grid)

# Also revert height 80 to 48
content = content.replace("val hourSlotHeightDp = 80.dp", "val hourSlotHeightDp = 48.dp")

# also restore the heightIn constraint for the scrollable area
content = content.replace("""                    // Scrollable Time Grid & Course Blocks (Vertical Only)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(verticalScrollState)
                    ) {""", """                    // Scrollable Time Grid & Course Blocks (Vertical Only)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 480.dp) // Limits max height, but wraps compact if smaller
                            .verticalScroll(verticalScrollState)
                    ) {""")

# And change the Card modifier back from weight(1f) to normal
content = content.replace("""        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {""", """        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {""")

# Revert fonts back inside the course block to match exactly what they had in image:
font_replace = """                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Black.copy(alpha = 0.2f))
                                                    .padding(horizontal = 2.dp, vertical = 3.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = parsed.timeRange12Hr.replace(" AM", "").replace(" PM", ""),
                                                    color = Color.White,
                                                    fontSize = 8.sp,
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
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(1.dp))
                                                Text(
                                                    text = "Sec ${course.section}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    fontSize = 9.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    textAlign = TextAlign.Center
                                                )
                                            }"""

font_new = """                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Black.copy(alpha = 0.2f))
                                                    .padding(horizontal = 4.dp, vertical = 3.dp),
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
                                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = course.courseCode,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(1.dp))
                                                Text(
                                                    text = "Sec ${course.section}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }"""
content = content.replace(font_replace, font_new)

# Column maxHeight
content = content.replace("""                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(totalGridWidth)
                        .fillMaxHeight()
                ) {""", """                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(totalGridWidth)
                ) {""")


with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
