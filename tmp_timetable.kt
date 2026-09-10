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

    val parsedCourses = remember(courses) { courses.map { ScheduleHelper.parse(it.timeSlot) }.filter { it.startMinutes < it.endMinutes } }
    
    val startHour = remember(parsedCourses) {
        if (parsedCourses.isEmpty()) 8 else (parsedCourses.minOf { it.startMinutes } / 60).coerceIn(6, 20)
    }
    val endHour = remember(parsedCourses) {
        if (parsedCourses.isEmpty()) 17 else {
            val maxMins = parsedCourses.maxOf { it.endMinutes }
            ((maxMins + 59) / 60).coerceIn(9, 22)
        }
    }

    val startDayMinutes = startHour * 60
    val endDayMinutes = endHour * 60
    val totalMinutes = endDayMinutes - startDayMinutes

    // Compact mode: scale hour block based on how many hours we have to fit
    val hourCount = endHour - startHour
    val hourSlotHeightDp = 56.dp
    val totalHeightDp = hourSlotHeightDp * hourCount
    val timeColWidth = 54.dp
    val dayColWidth = 110.dp

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
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
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
                
                val timeColWidth = 46.dp
                val minDayWidth = 72.dp
                val calculatedDayWidth = (availableWidth - timeColWidth - 16.dp) / displayDays.size.coerceAtLeast(1)
                val dayColWidth = maxOf(minDayWidth, calculatedDayWidth)
                val totalGridWidth = timeColWidth + (dayColWidth * displayDays.size)
                
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .horizontalScroll(rememberScrollState())
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
                            .heightIn(max = 480.dp) // Limits max height, but wraps compact if smaller
                            .verticalScroll(verticalScrollState)
                    ) {
                        // Grid background
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
                        }

                        // Render Course Class Blocks on Top
                        for ((cIndex, course) in courses.withIndex()) {
                            val parsed = ScheduleHelper.parse(course.timeSlot)
                            if (parsed.days.isEmpty() || parsed.startMinutes >= parsed.endMinutes) continue

                            val blockColor = BLOCK_COLORS[cIndex % BLOCK_COLORS.size]

                            // Compute top offset and height in Dp
                            val startDiff = (parsed.startMinutes - startDayMinutes).coerceAtLeast(0)
                            val duration = parsed.durationMinutes
                            val topOffsetDp = (startDiff.toFloat() / 60f) * hourSlotHeightDp.value
                            val blockHeightDp = ((duration.toFloat() / 60f) * hourSlotHeightDp.value).coerceAtLeast(36f)

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
}
