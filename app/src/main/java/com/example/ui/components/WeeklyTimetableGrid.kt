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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.StitchBackground
import com.example.ui.theme.StitchOutline
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchPrimaryContainer
import com.example.ui.theme.StitchSecondaryContainer
import com.example.ui.theme.StitchSurfaceContainer
import com.example.ui.theme.StitchSurfaceHigh
import com.example.ui.theme.StitchSurfaceLow
import com.example.ui.theme.StitchTertiary
import com.example.ui.theme.StitchTextMuted
import com.example.ui.theme.StitchTextPrimary
import com.example.ui.theme.StitchTextSecondary
import com.example.ui.theme.TimetableBlockColors

data class TimeGap(val startMin: Int, val endMin: Int)

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

    val courseAndParsed = remember(courses) {
        courses.map { it to ScheduleHelper.parse(it.timeSlot) }
            .filter { it.second.startMinutes < it.second.endMinutes }
    }

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
    val hourSlotHeightDp = 52.dp

    // Color palette mapping
    val courseColorMap = remember(courses) {
        val uniqueCodes = courses.map { it.courseCode }.distinct()
        uniqueCodes.mapIndexed { index, code ->
            code to TimetableBlockColors[index % TimetableBlockColors.size]
        }.toMap()
    }

    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    val timeColumnWidth = 56.dp
    val dayColumnWidth = 110.dp
    val totalGridWidth = timeColumnWidth + (dayColumnWidth * displayDays.size)
    val totalHours = (endHour - startHour).coerceAtLeast(1)
    val totalGridHeight = hourSlotHeightDp * totalHours

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StitchBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main Scrollable Schedule Grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
            ) {
                Column(modifier = Modifier.width(totalGridWidth)) {
                    // Header Row: Days
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .background(StitchSurfaceLow)
                            .border(1.dp, StitchOutline)
                    ) {
                        // Empty Corner cell for Time Column
                        Box(
                            modifier = Modifier
                                .width(timeColumnWidth)
                                .fillMaxHeight()
                                .border(androidx.compose.foundation.BorderStroke(0.5.dp, StitchOutline)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TIME",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = StitchTextMuted
                            )
                        }

                        // Day headers
                        displayDays.forEach { day ->
                            Box(
                                modifier = Modifier
                                    .width(dayColumnWidth)
                                    .fillMaxHeight()
                                    .border(androidx.compose.foundation.BorderStroke(0.5.dp, StitchOutline)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.name.take(3),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = StitchTextPrimary
                                )
                            }
                        }
                    }

                    // Vertically Scrollable Grid Body
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(totalGridHeight)
                            .verticalScroll(verticalScrollState)
                    ) {
                        // Background Hour Guidelines
                        Column(modifier = Modifier.fillMaxSize()) {
                            for (hour in startHour until endHour) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(hourSlotHeightDp)
                                ) {
                                    // Time Label Cell
                                    Box(
                                        modifier = Modifier
                                            .width(timeColumnWidth)
                                            .fillMaxHeight()
                                            .border(androidx.compose.foundation.BorderStroke(0.5.dp, StitchOutline.copy(alpha = 0.5f))),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        val displayHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
                                        val amPm = if (hour >= 12) "PM" else "AM"
                                        Text(
                                            text = "$displayHour $amPm",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = StitchTextMuted,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }

                                    // Day Columns background slots
                                    displayDays.forEach { _ ->
                                        Box(
                                            modifier = Modifier
                                                .width(dayColumnWidth)
                                                .fillMaxHeight()
                                                .border(androidx.compose.foundation.BorderStroke(0.5.dp, StitchOutline.copy(alpha = 0.35f)))
                                        )
                                    }
                                }
                            }
                        }

                        // Overlay Course Blocks
                        displayDays.forEachIndexed { dayIndex, day ->
                            val xOffset = timeColumnWidth + (dayColumnWidth * dayIndex)

                            courseAndParsed.forEach { (course, parsed) ->
                                if (parsed.days.contains(day)) {
                                    val startMinutes = parsed.startMinutes
                                    val endMinutes = parsed.endMinutes

                                    val topMinutes = (startMinutes - startDayMinutes).coerceAtLeast(0)
                                    val durationMinutes = (endMinutes - startMinutes).coerceAtLeast(15)

                                    val topDp = (topMinutes.toFloat() / 60f) * hourSlotHeightDp.value
                                    val heightDp = (durationMinutes.toFloat() / 60f) * hourSlotHeightDp.value

                                    val blockColor = courseColorMap[course.courseCode] ?: StitchPrimaryContainer

                                    Box(
                                        modifier = Modifier
                                            .offset(x = xOffset + 2.dp, y = topDp.dp + 1.dp)
                                            .width(dayColumnWidth - 4.dp)
                                            .height(heightDp.dp - 2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(blockColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                if (onCourseClick != null) {
                                                    onCourseClick(course)
                                                } else {
                                                    selectedCourseForInfo = course
                                                }
                                            }
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = course.courseCode,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    ),
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "S${course.section}",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 9.sp
                                                    ),
                                                    color = Color.White.copy(alpha = 0.9f)
                                                )
                                            }

                                            Text(
                                                text = if (course.faculty.isNotBlank()) course.faculty else "TBA",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Normal
                                                ),
                                                color = Color.White.copy(alpha = 0.85f),
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

    // Detail popup on click
    selectedCourseForInfo?.let { course ->
        val parsedTime = remember(course.timeSlot) { ScheduleHelper.parse(course.timeSlot) }
        AlertDialog(
            onDismissRequest = { selectedCourseForInfo = null },
            containerColor = StitchSurfaceContainer,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${course.courseCode} (Sec ${course.section})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = StitchTextPrimary
                    )
                    IconButton(
                        onClick = { selectedCourseForInfo = null },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = StitchTextSecondary)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = course.title, style = MaterialTheme.typography.bodyMedium, color = StitchTextSecondary)
                    Text(text = "Instructor: ${if (course.faculty.isNotBlank()) course.faculty else "TBA"}", style = MaterialTheme.typography.bodySmall, color = StitchTextPrimary)
                    Text(text = "Time: ${parsedTime.formattedTime}", style = MaterialTheme.typography.bodySmall, color = StitchTextPrimary)
                    Text(text = "Enrolled: ${course.enrolled}/${course.capacity}", style = MaterialTheme.typography.bodySmall, color = StitchTextPrimary)
                    Text(text = "Credits: ${course.credit} Cr", style = MaterialTheme.typography.bodySmall, color = StitchSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedCourseForInfo = null },
                    colors = ButtonDefaults.buttonColors(containerColor = StitchPrimaryContainer)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
}
