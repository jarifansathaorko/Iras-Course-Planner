import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# 1. Math replacements
content = content.replace("""    val startDayMinutes = startHour * 60
    val endDayMinutes = endHour * 60
    val totalMinutes = endDayMinutes - startDayMinutes

    // Compact mode: scale hour block based on how many hours we have to fit
    val hourCount = endHour - startHour
    val hourSlotHeightDp = 56.dp
    val totalHeightDp = hourSlotHeightDp * hourCount
    val timeColWidth = 54.dp
    val dayColWidth = 110.dp""", """    val startDayMinutes = startHour * 60
    val endDayMinutes = endHour * 60

    val hourSlotHeightDp = 56.dp
    val freeTimeBlockHeightDp = 42f

    val mergedIntervals = remember(parsedCourses) {
        val list = mutableListOf<IntRange>()
        parsedCourses.map { it.startMinutes..it.endMinutes }.sortedBy { it.first }.forEach { interval ->
            if (list.isEmpty()) {
                list.add(interval)
            } else {
                val last = list.last()
                if (interval.first <= last.last) {
                    list[list.size - 1] = last.first..maxOf(last.last, interval.last)
                } else {
                    list.add(interval)
                }
            }
        }
        list
    }

    val freeGaps = remember(mergedIntervals, startDayMinutes) {
        val gaps = mutableListOf<IntRange>()
        var currentMax = startDayMinutes
        for (interval in mergedIntervals) {
            if (interval.first - currentMax >= 150) { // 2.5 hours threshold to be safe
                gaps.add(currentMax until interval.first)
            }
            currentMax = maxOf(currentMax, interval.last)
        }
        gaps
    }

    val mapMinutesToY = remember(freeGaps, startDayMinutes, hourSlotHeightDp.value) {
        { minutes: Int ->
            var y = 0f
            var currentMin = startDayMinutes
            for (gap in freeGaps) {
                if (minutes <= gap.first) {
                    y += (minutes - currentMin) / 60f * hourSlotHeightDp.value
                    currentMin = minutes
                    break
                }
                y += (gap.first - currentMin) / 60f * hourSlotHeightDp.value
                if (minutes <= gap.last) {
                    y += (minutes - gap.first) / (gap.last - gap.first).toFloat() * freeTimeBlockHeightDp
                    currentMin = minutes
                    break
                }
                y += freeTimeBlockHeightDp
                currentMin = gap.last
            }
            if (currentMin < minutes) {
                y += (minutes - currentMin) / 60f * hourSlotHeightDp.value
            }
            y
        }
    }

    val totalHeightDp = mapMinutesToY(endDayMinutes).dp""")

# 2. Width fix
content = content.replace("""                val timeColWidth = 46.dp
                val minDayWidth = 72.dp
                val calculatedDayWidth = (availableWidth - timeColWidth - 16.dp) / displayDays.size.coerceAtLeast(1)
                val dayColWidth = maxOf(minDayWidth, calculatedDayWidth)
                val totalGridWidth = timeColWidth + (dayColWidth * displayDays.size)""", """                val timeColWidth = 46.dp
                val dayColWidth = (availableWidth - timeColWidth - 16.dp) / displayDays.size.coerceAtLeast(1)
                val totalGridWidth = timeColWidth + (dayColWidth * displayDays.size)""")

# 3. Grid rendering
old_grid = """                        // Grid background
                        Row(modifier = Modifier.height(totalHeightDp)) {
                            // Left time column labels
                            Column(
                                modifier = Modifier
                                    .width(timeColWidth)
                                    .fillMaxHeight()
                            ) {
                                for (hour in startHour..endHour) {
                                    Box(
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
                                    }
                                }
                            }

                            // Day Columns with hour separator lines
                            for (day in displayDays) {
                                Box(
                                    modifier = Modifier
                                        .width(dayColWidth)
                                        .fillMaxHeight()
                                        .border(
                                            width = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        )
                                ) {
                                    // Draw horizontal hour guidelines
                                    Column {
                                        for (hour in startHour until endHour) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(hourSlotHeightDp)
                                                    .border(
                                                        width = 0.5.dp,
                                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }"""

new_grid = """                        // Grid background
                        Box(modifier = Modifier.width(totalGridWidth).height(totalHeightDp)) {
                            // 1. Day Columns (Vertical lines & background boxes)
                            Row(modifier = Modifier.fillMaxSize()) {
                                Spacer(modifier = Modifier.width(timeColWidth))
                                for (day in displayDays) {
                                    Box(
                                        modifier = Modifier
                                            .width(dayColWidth)
                                            .fillMaxHeight()
                                            .border(
                                                width = 0.5.dp,
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                            )
                                    )
                                }
                            }

                            // 2. Horizontal Hour Guidelines & Labels
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
                            }
                        }"""
content = content.replace(old_grid, new_grid)

# 4. Course tiles Y axis
content = content.replace("""                            // Compute top offset and height in Dp
                            val startDiff = (parsed.startMinutes - startDayMinutes).coerceAtLeast(0)
                            val duration = parsed.durationMinutes
                            val topOffsetDp = (startDiff.toFloat() / 60f) * hourSlotHeightDp.value
                            val blockHeightDp = ((duration.toFloat() / 60f) * hourSlotHeightDp.value).coerceAtLeast(36f)""", """                            // Compute top offset and height in Dp
                            val topOffsetDp = mapMinutesToY(parsed.startMinutes)
                            val bottomOffsetDp = mapMinutesToY(parsed.endMinutes)
                            val blockHeightDp = maxOf(36f, bottomOffsetDp - topOffsetDp)""")

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
