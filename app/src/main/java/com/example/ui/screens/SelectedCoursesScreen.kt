package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.CourseEntity
import com.example.domain.ConflictResult
import com.example.domain.ScheduleHelper
import com.example.ui.components.WeeklyTimetableGrid
import com.example.ui.theme.ConflictRed
import com.example.ui.theme.ConflictRedBg
import com.example.ui.theme.ConflictRedBorder
import com.example.ui.theme.StitchBackground
import com.example.ui.theme.StitchOutline
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchPrimaryContainer
import com.example.ui.theme.StitchSecondary
import com.example.ui.theme.StitchSecondaryContainer
import com.example.ui.theme.StitchSurfaceContainer
import com.example.ui.theme.StitchSurfaceHigh
import com.example.ui.theme.StitchSurfaceLow
import com.example.ui.theme.StitchTertiary
import com.example.ui.theme.StitchTextMuted
import com.example.ui.theme.StitchTextPrimary
import com.example.ui.theme.StitchTextSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg
import com.example.ui.theme.SuccessGreenBorder

@Composable
fun SelectedCoursesScreen(
    selectedCourses: List<CourseEntity>,
    onChangeSectionClick: (CourseEntity) -> Unit,
    onDropCourseClick: (CourseEntity) -> Unit,
    onConfirmScheduleClick: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var viewModeTab by remember { mutableIntStateOf(0) } // 0: List View, 1: Timetable View
    val totalCredits = remember(selectedCourses) { selectedCourses.sumOf { it.credit } }

    val conflicts = remember(selectedCourses) {
        val detected = mutableListOf<String>()
        for (i in selectedCourses.indices) {
            for (j in i + 1 until selectedCourses.size) {
                val c1 = selectedCourses[i]
                val c2 = selectedCourses[j]
                val conflict = ScheduleHelper.findConflict(c2, listOf(c1))
                if (conflict != null) {
                    when (conflict) {
                        is ConflictResult.SameCourse -> {
                            detected.add("${c1.courseCode} (Sec ${c1.section}) and (Sec ${c2.section}) are same course")
                        }
                        is ConflictResult.TimeOverlap -> {
                            detected.add("${c1.courseCode} (Sec ${c1.section}) clashes with ${c2.courseCode} (Sec ${c2.section})")
                        }
                    }
                }
            }
        }
        detected
    }

    val outlineColor = StitchOutline

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StitchBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- Summary Top Bar (Matching Stitch 03_my-schedule.html) ---
            Surface(
                color = StitchSurfaceLow,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = outlineColor,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(StitchSurfaceContainer)
                                    .border(1.5.dp, StitchPrimaryContainer.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_iras_logo),
                                    contentDescription = "IRAS Logo",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "My Schedule",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        letterSpacing = (-0.01).sp
                                    ),
                                    color = StitchTextPrimary
                                )
                                Text(
                                    text = "Active Semester Routine",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = StitchTextSecondary
                                )
                            }
                        }

                        // Export Schedule Action
                        if (selectedCourses.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    com.example.util.PlanImageExporter.exportAndSharePlan(
                                        context = context,
                                        planNumber = 0,
                                        planName = "Current Schedule",
                                        courses = selectedCourses
                                    )
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StitchSurfaceContainer)
                                    .border(1.dp, StitchOutline, RoundedCornerShape(10.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Export Timetable",
                                    tint = StitchPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats & Status Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Total Courses & Credits Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(StitchSurfaceContainer)
                                .border(1.dp, StitchOutline, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${selectedCourses.size} Courses • $totalCredits Credits",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = StitchTextPrimary,
                                modifier = Modifier.testTag("total_credits_text")
                            )
                        }

                        // Conflict Indicator Pill
                        if (selectedCourses.isNotEmpty()) {
                            if (conflicts.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SuccessGreenBg)
                                        .border(1.dp, SuccessGreenBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 9.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = StitchTertiary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "No Conflicts",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = StitchTertiary
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ConflictRedBg)
                                        .border(1.dp, ConflictRedBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 9.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = ConflictRed,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${conflicts.size} Conflict",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = ConflictRed
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Segmented View Switcher: [ List View | Timetable Grid ]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StitchSurfaceContainer)
                            .border(1.dp, StitchOutline, RoundedCornerShape(10.dp))
                            .padding(3.dp)
                    ) {
                        // Tab 0: List View
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (viewModeTab == 0) StitchPrimaryContainer else Color.Transparent)
                                .clickable { viewModeTab = 0 }
                                .padding(vertical = 6.dp)
                                .testTag("btn_tab_list"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = null,
                                    tint = if (viewModeTab == 0) Color.White else StitchTextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "List View",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (viewModeTab == 0) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (viewModeTab == 0) Color.White else StitchTextSecondary
                                )
                            }
                        }

                        // Tab 1: Timetable Grid
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (viewModeTab == 1) StitchPrimaryContainer else Color.Transparent)
                                .clickable { viewModeTab = 1 }
                                .padding(vertical = 6.dp)
                                .testTag("btn_tab_timetable"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = null,
                                    tint = if (viewModeTab == 1) Color.White else StitchTextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Weekly Grid",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (viewModeTab == 1) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (viewModeTab == 1) Color.White else StitchTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // --- Body Content ---
            if (selectedCourses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = StitchTextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "No Courses Enrolled",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = StitchTextPrimary
                        )
                        Text(
                            text = "You haven't added any course sections yet. Browse the catalog to select courses for your semester routine.",
                            style = MaterialTheme.typography.bodySmall,
                            color = StitchTextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Button(
                            onClick = onNavigateToCatalog,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StitchPrimaryContainer,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Browse Course Catalog")
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    if (viewModeTab == 0) {
                        // --- List View ---
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = selectedCourses,
                                key = { it.id }
                            ) { course ->
                                val parsedTime = remember(course.timeSlot) { ScheduleHelper.parse(course.timeSlot) }
                                SelectedCourseCard(
                                    course = course,
                                    parsedTime = parsedTime,
                                    onChangeSection = { onChangeSectionClick(course) },
                                    onDrop = { onDropCourseClick(course) }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(72.dp))
                            }
                        }
                    } else {
                        // --- Timetable Grid View ---
                        WeeklyTimetableGrid(
                            courses = selectedCourses,
                            modifier = Modifier.fillMaxSize(),
                            onCourseClick = onChangeSectionClick
                        )
                    }
                }

                // --- Bottom Action Floating Bar ---
                Surface(
                    color = StitchSurfaceLow,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = outlineColor,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = onClearAll,
                            modifier = Modifier.testTag("btn_clear_all")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear All",
                                tint = StitchTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Clear All",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = StitchTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = onConfirmScheduleClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StitchPrimaryContainer,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("btn_confirm_schedule")
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Save as Plan",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Save as Plan",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedCourseCard(
    course: CourseEntity,
    parsedTime: com.example.domain.ParsedSchedule,
    onChangeSection: () -> Unit,
    onDrop: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StitchSurfaceContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Code, Section & Credits
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = course.courseCode,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.02).sp
                        ),
                        color = StitchTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StitchSurfaceHigh)
                            .padding(horizontal = 7.dp, vertical = 2.5.dp)
                    ) {
                        Text(
                            text = "SEC ${course.section}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = StitchTextPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StitchSecondaryContainer.copy(alpha = 0.25f))
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = "${course.credit} Cr",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                        color = StitchSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title
            Text(
                text = course.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                color = StitchTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Schedule Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(StitchSurfaceLow)
                    .border(1.dp, StitchOutline.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = StitchPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = parsedTime.formattedTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    color = StitchTextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = course.timeSlot,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                    color = StitchTextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Faculty Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = StitchTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (course.faculty.isNotBlank()) course.faculty else "TBA",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = StitchTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: Change Section & Drop
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onChangeSection,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = StitchSurfaceLow,
                        contentColor = StitchPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline),
                    modifier = Modifier.testTag("btn_change_section_${course.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Change Section",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Change Section",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDrop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ConflictRedBg)
                        .border(1.dp, ConflictRedBorder, RoundedCornerShape(8.dp))
                        .testTag("btn_drop_${course.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Drop Course",
                        tint = ConflictRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
