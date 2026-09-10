import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# Add data class TimeGap
if "data class TimeGap" not in content:
    content = content.replace("private val BLOCK_COLORS = listOf(", "data class TimeGap(val startMin: Int, val endMin: Int)\n\nprivate val BLOCK_COLORS = listOf(")

# Replace totalHeightDp and mapMinutesToY
old_block = """    val hourSlotHeightDp = 50.dp
        val totalHeightDp = ((((endDayMinutes - startDayMinutes) / 60f) + 1) * hourSlotHeightDp.value).dp
    val mapMinutesToY = { minutes: Int ->
        ((minutes - startDayMinutes) / 60f) * hourSlotHeightDp.value
    }"""

new_block = """    val hourSlotHeightDp = 50.dp

    val gaps = remember(parsedCourses) {
        val intervals = parsedCourses.map { it.startMinutes to it.endMinutes }.sortedBy { it.first }
        val activeBlocks = mutableListOf<Pair<Int, Int>>()
        for (interval in intervals) {
            if (activeBlocks.isEmpty()) {
                activeBlocks.add(interval)
            } else {
                val last = activeBlocks.last()
                if (interval.first <= last.second) {
                    activeBlocks[activeBlocks.lastIndex] = last.copy(second = maxOf(last.second, interval.second))
                } else {
                    activeBlocks.add(interval)
                }
            }
        }
        val foundGaps = mutableListOf<TimeGap>()
        val GAP_THRESHOLD = 90
        for (i in 0 until activeBlocks.size - 1) {
            val gapStart = activeBlocks[i].second
            val gapEnd = activeBlocks[i+1].first
            if (gapEnd - gapStart >= GAP_THRESHOLD) {
                foundGaps.add(TimeGap(gapStart, gapEnd))
            }
        }
        foundGaps
    }

    val GAP_BANNER_HEIGHT_DP = 42f

    val mapMinutesToY = { minutes: Int ->
        var y = ((minutes - startDayMinutes) / 60f) * hourSlotHeightDp.value
        var shift = 0f
        for (gap in gaps) {
            if (minutes > gap.startMin) {
                val collapsedDuration = minOf(minutes, gap.endMin) - gap.startMin
                shift -= (collapsedDuration / 60f) * hourSlotHeightDp.value
                if (minutes >= gap.endMin) {
                    shift += GAP_BANNER_HEIGHT_DP
                } else {
                    shift += (collapsedDuration.toFloat() / (gap.endMin - gap.startMin)) * GAP_BANNER_HEIGHT_DP
                }
            }
        }
        y + shift
    }

    val totalHeightDp = remember(gaps, startDayMinutes, endDayMinutes) {
        var totalHeightFloat = (((endDayMinutes - startDayMinutes) / 60f) + 1) * hourSlotHeightDp.value
        for (gap in gaps) {
            val duration = gap.endMin - gap.startMin
            totalHeightFloat -= (duration / 60f) * hourSlotHeightDp.value
            totalHeightFloat += GAP_BANNER_HEIGHT_DP
        }
        totalHeightFloat.dp
    }"""

content = content.replace(old_block, new_block)

# Replace horizontal hour guidelines logic to skip hours inside gaps
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
                                
                                // Skip guidelines that fall inside a collapsed gap
                                val inGap = gaps.any { gap -> hMin > gap.startMin && hMin < gap.endMin }
                                if (inGap) continue
                                
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

# Add rendering for Free Time banners BEFORE rendering Course Class Blocks
old_render_courses = """                        // Render Course Class Blocks on Top
                        for ((cIndex, course) in courses.withIndex()) {"""

new_render_courses = """                        // Render Free Time Banners
                        for (gap in gaps) {
                            val bannerTopY = mapMinutesToY(gap.startMin).dp
                            val bannerHeight = GAP_BANNER_HEIGHT_DP.dp
                            
                            Box(
                                modifier = Modifier
                                    .offset(x = timeColWidth, y = bannerTopY)
                                    .width(totalGridWidth - timeColWidth)
                                    .height(bannerHeight)
                                    .padding(vertical = 4.dp, horizontal = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                val startStr = ScheduleHelper.formatMinutesTo12Hr(gap.startMin)
                                val endStr = ScheduleHelper.formatMinutesTo12Hr(gap.endMin - 1) // Display up to 1 minute before next class
                                Text(
                                    text = "FREE TIME • $startStr - $endStr",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Render Course Class Blocks on Top
                        for ((cIndex, course) in courses.withIndex()) {"""
content = content.replace(old_render_courses, new_render_courses)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
