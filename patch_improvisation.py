import re
with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# 1. Height Adjustment (Make blocks taller for better readability)
content = content.replace("val hourSlotHeightDp = 48.dp", "val hourSlotHeightDp = 58.dp")

# 2. Add Zebra striping and Time Col Divider
old_guidelines = """                            // 2. Horizontal Hour Guidelines & Labels
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

new_guidelines = """                            // 2. Horizontal Hour Guidelines & Labels
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
content = content.replace(old_guidelines, new_guidelines)

# 3. Add shadow and border to course blocks
old_box = """                                    Box(
                                        modifier = Modifier
                                            .offset(x = leftOffsetDp.dp, y = topOffsetDp.dp)
                                            .width(blockWidthDp.dp)
                                            .height(blockHeightDp.dp)
                                            .padding(horizontal = 2.dp, vertical = 1.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(blockColor)
                                            .clickable { selectedCourseForInfo = course }
                                    ) {"""

new_box = """                                    Box(
                                        modifier = Modifier
                                            .offset(x = leftOffsetDp.dp, y = topOffsetDp.dp)
                                            .width(blockWidthDp.dp)
                                            .height(blockHeightDp.dp)
                                            .padding(horizontal = 2.dp, vertical = 1.dp)
                                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(6.dp))
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(blockColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                            .clickable { selectedCourseForInfo = course }
                                    ) {"""
content = content.replace(old_box, new_box)

# 4. Improve Fonts Inside Course Blocks
# Currently they are 7.5.sp, 9.sp, 8.sp.
# We will change to 8.5.sp, 11.sp, 9.sp since we increased height.
content = content.replace("fontSize = 7.5.sp,", "fontSize = 8.sp,")
content = content.replace("fontSize = 9.sp,", "fontSize = 11.sp,")
content = content.replace("fontSize = 8.sp,", "fontSize = 9.sp,")

# Wait, check if shadow import exists, we can add it safely.
# if import androidx.compose.ui.draw.shadow not found, add it.
if "import androidx.compose.ui.draw.shadow" not in content:
    content = content.replace("import androidx.compose.ui.draw.clip", "import androidx.compose.ui.draw.clip\nimport androidx.compose.ui.draw.shadow")

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
