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

    // Time boundary: 08:00 (480 mins) to 21:30 (1290 mins)
    val startDayMinutes = 8 * 60
    val endDayMinutes = 21 * 60 + 30
    val totalMinutes = endDayMinutes - startDayMinutes

    val hourSlotHeightDp = 64.dp
    val totalHeightDp = hourSlotHeightDp * 14 // 14 hours range
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
        val horizontalScrollState = rememberScrollState()
        val verticalScrollState = rememberScrollState()

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Days Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState)
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
                                .padding(horizontal = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.shortName.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Scrollable Time Grid & Course Blocks
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState)
                ) {
                    // Grid background
                    Row(modifier = Modifier.height(totalHeightDp)) {
                        // Left time column labels
                        Column(
                            modifier = Modifier
                                .width(timeColWidth)
                                .fillMaxHeight()
                        ) {
                            for (hour in 8..21) {
                                Box(
                                    modifier = Modifier
                                        .width(timeColWidth)
                                        .height(hourSlotHeightDp)
                                        .padding(end = 6.dp),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    val amPm = if (hour >= 12) "PM" else "AM"
                                    val h12 = if (hour > 12) hour - 12 else hour
                                    Text(
                                        text = "$h12 $amPm",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
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
                                    for (hour in 8..21) {
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
                                val leftOffsetDp = timeColWidth.value + (dayIdx * dayColWidth.value) + 3f
                                val blockWidthDp = dayColWidth.value - 6f

                                Box(
                                    modifier = Modifier
                                        .offset(x = leftOffsetDp.dp, y = topOffsetDp.dp)
                                        .width(blockWidthDp.dp)
                                        .height(blockHeightDp.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(blockColor)
                                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
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
                                            text = "${course.courseCode} - ${course.section}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = parsed.timeRange12Hr,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 9.sp,
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
