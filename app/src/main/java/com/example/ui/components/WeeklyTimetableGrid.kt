package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CourseEntity
import com.example.domain.Day
import com.example.domain.ScheduleHelper

data class TimeGap(val startMin: Int, val endMin: Int)

private val BLOCK_COLORS = listOf(
    Color(0xFF2563EB), // Royal Blue
    Color(0xFF0D9488), // Teal
    Color(0xFF7C3AED), // Violet
    Color(0xFFD97706), // Amber
    Color(0xFFBE123C), // Rose
    Color(0xFF059669), // Emerald
    Color(0xFF4F46E5), // Indigo
    Color(0xFFEA580C)  // Deep Orange
)

@Composable
fun WeeklyTimetableGrid(
    courses: List<CourseEntity>,
    modifier: Modifier = Modifier,
    onCourseClick: ((CourseEntity) -> Unit)? = null
) {
    var selectedCourseForInfo by remember { mutableStateOf<CourseEntity?>(null) }

    val displayDays = listOf(
        Day.SUNDAY,
        Day.MONDAY,
        Day.TUESDAY,
        Day.WEDNESDAY,
        Day.THURSDAY,
        Day.SATURDAY
    )

    val courseAndParsed = remember(courses) { courses.map { it to ScheduleHelper.parse(it.timeSlot) }.filter { it.second.startMinutes < it.second.endMinutes } }
    
    val startHour = remember(courseAndParsed) {
        if (courseAndParsed.isEmpty()) 8 else (courseAndParsed.minOf { it.second.startMinutes } / 60).coerceIn(6, 20)
    }
    val endHour = remember(courseAndParsed) {
        if (courseAndParsed.isEmpty()) 17 else {
            val maxMins = courseAndParsed.maxOf { it.second.endMinutes }
            ((maxMins + 59) / 60).coerceIn(9, 22)
        }
    }

    val startDayMinutes = startHour * 60
    val endDayMinutes = endHour * 60

    val hourSlotHeightDp = 50.dp

    val gaps = remember(courseAndParsed) {
        val intervals = courseAndParsed.map { it.second.startMinutes to it.second.endMinutes }.sortedBy { it.first }
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
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Selected Course Preview Banner if user tapped on a class
        selectedCourseForInfo?.let { course ->
            val parsed = ScheduleHelper.parse(course.timeSlot)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { selectedCourseForInfo = null }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${course.courseCode} (Sec ${course.section}) - ${course.title}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${parsed.formattedTime} • Faculty: ${course.faculty} • ${course.credit} Cr",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // Horizontal scrollable schedule grid container
        val verticalScrollState = rememberScrollState()

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val availableWidth = maxWidth
                
                // Smart active days filtering
                val activeDays = remember(courses) {
                    courses.flatMap { ScheduleHelper.parse(it.timeSlot).days }.toSet()
                }
                
                val displayDays = remember(activeDays) {
                    if (activeDays.isEmpty()) {
                        listOf(Day.SUNDAY, Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY)
                    } else {
                        listOf(Day.SUNDAY, Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY, Day.SATURDAY)
                            .filter { it in activeDays }
                    }
                }
                
                val timeColWidth = 40.dp
                val dayColWidth = (availableWidth - timeColWidth - 8.dp) / displayDays.size.coerceAtLeast(1)
                val totalGridWidth = timeColWidth + (dayColWidth * displayDays.size)
                
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(totalGridWidth)
                ) {
                    // Days Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Empty corner for time column
                        Box(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(38.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TIME",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 10.sp
                            )
                        }

                        for (day in displayDays) {
                            Box(
                                modifier = Modifier
                                    .width(dayColWidth)
                                    .height(38.dp)
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.shortName.uppercase(),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Scrollable Time Grid & Course Blocks (Vertical Only)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(verticalScrollState)
                    ) {
                        // Grid background
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
                                )
                                // Time Label
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
                                        fontSize = 8.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        // Render Free Time Banners
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
                        for ((cIndex, pair) in courseAndParsed.withIndex()) {
                            val course = pair.first
                            val parsed = pair.second

                            val blockColor = BLOCK_COLORS[cIndex % BLOCK_COLORS.size]

                            // Compute top offset and height in Dp
                            val topOffsetDp = mapMinutesToY(parsed.startMinutes)
                            val bottomOffsetDp = mapMinutesToY(parsed.endMinutes)
                            val blockHeightDp = maxOf(36f, bottomOffsetDp - topOffsetDp)

                            for (day in parsed.days) {
                                val dayIdx = displayDays.indexOf(day)
                                if (dayIdx >= 0) {
                                    val leftOffsetDp = timeColWidth.value + (dayIdx * dayColWidth.value)
                                    val blockWidthDp = dayColWidth.value

                                    Box(
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
