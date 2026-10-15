package com.example.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.entity.PlanMetaEntity
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

@Composable
fun SavedPlansScreen(
    activePlanNumber: Int,
    plan1Courses: List<CourseEntity>,
    plan2Courses: List<CourseEntity>,
    plan3Courses: List<CourseEntity>,
    plan4Courses: List<CourseEntity>,
    allPlanMeta: List<PlanMetaEntity>,
    onSelectPlanTab: (Int) -> Unit,
    onSharePlan: (Context, Int) -> Unit,
    onLoadPlanIntoSelection: (Int) -> Unit,
    onClearPlan: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var viewModeTab by remember { mutableIntStateOf(0) } // 0: Timetable View, 1: Course List

    val currentPlanCourses = when (activePlanNumber) {
        1 -> plan1Courses
        2 -> plan2Courses
        3 -> plan3Courses
        4 -> plan4Courses
        else -> emptyList()
    }

    val currentMeta = allPlanMeta.find { it.planNumber == activePlanNumber }
    val planName = currentMeta?.planName ?: "Plan $activePlanNumber"
    val totalCredits = currentPlanCourses.sumOf { it.credit }

    val planCounts = listOf(
        plan1Courses.size,
        plan2Courses.size,
        plan3Courses.size,
        plan4Courses.size
    )

    val outlineColor = StitchOutline

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StitchBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- Top App Bar (Matching Stitch 04_saved-semester-plans.html) ---
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
                    // Title Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                text = "Saved Semester Plans",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    letterSpacing = (-0.01).sp
                                ),
                                color = StitchTextPrimary
                            )
                            Text(
                                text = "Switch Between 4 Planned Routines",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = StitchTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Plan Tabs Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StitchSurfaceContainer)
                            .border(1.dp, StitchOutline, RoundedCornerShape(10.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (p in 1..4) {
                            val isSelected = activePlanNumber == p
                            val count = planCounts[p - 1]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) StitchPrimaryContainer else Color.Transparent)
                                    .clickable { onSelectPlanTab(p) }
                                    .padding(vertical = 7.dp)
                                    .testTag("plan_tab_$p"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Plan $p",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.5.sp
                                        ),
                                        color = if (isSelected) Color.White else StitchTextSecondary
                                    )
                                    if (count > 0) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) Color.White.copy(alpha = 0.25f) else StitchSurfaceHigh)
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "$count",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.5.sp
                                                ),
                                                color = if (isSelected) Color.White else StitchTertiary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- Active Plan Overview Card & Body ---
            if (currentPlanCourses.isEmpty()) {
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
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = StitchTextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "Plan $activePlanNumber is Empty",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = StitchTextPrimary
                        )
                        Text(
                            text = "You haven't saved any schedule into Plan $activePlanNumber yet. Go to 'My Schedule' and tap 'Save as Plan' to store a routine here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = StitchTextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                // Header Card for Active Plan
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = StitchSurfaceContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = planName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            letterSpacing = (-0.01).sp
                                        ),
                                        color = StitchTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${currentPlanCourses.size} Courses • $totalCredits Credits",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                                        color = StitchTextSecondary
                                    )
                                }

                                // View switcher icon toggle
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StitchSurfaceLow)
                                        .border(1.dp, StitchOutline, RoundedCornerShape(8.dp))
                                        .padding(2.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModeTab = 0 },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (viewModeTab == 0) StitchPrimaryContainer else Color.Transparent)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = "Timetable",
                                            tint = if (viewModeTab == 0) Color.White else StitchTextSecondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModeTab = 1 },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (viewModeTab == 1) StitchPrimaryContainer else Color.Transparent)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.List,
                                            contentDescription = "List",
                                            tint = if (viewModeTab == 1) Color.White else StitchTextSecondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Plan Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { onLoadPlanIntoSelection(activePlanNumber) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StitchPrimaryContainer,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_load_plan")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restore,
                                        contentDescription = "Load",
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Load Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onSharePlan(context, activePlanNumber) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = StitchSurfaceLow,
                                        contentColor = StitchPrimary
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline),
                                    modifier = Modifier.testTag("btn_share_plan")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }

                                IconButton(
                                    onClick = { onClearPlan(activePlanNumber) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ConflictRedBg)
                                        .border(1.dp, ConflictRedBorder, RoundedCornerShape(8.dp))
                                        .testTag("btn_clear_plan")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Clear Plan",
                                        tint = ConflictRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Plan Content: Timetable or List
                Box(modifier = Modifier.weight(1f)) {
                    if (viewModeTab == 0) {
                        WeeklyTimetableGrid(
                            courses = currentPlanCourses,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = currentPlanCourses,
                                key = { it.id }
                            ) { course ->
                                val parsedTime = remember(course.timeSlot) { ScheduleHelper.parse(course.timeSlot) }
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = StitchSurfaceContainer),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = course.courseCode,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                                                    color = StitchTextPrimary
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(StitchSurfaceHigh)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "SEC ${course.section}",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                                                        color = StitchTextPrimary
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = parsedTime.formattedTime,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                                color = StitchTextSecondary
                                            )
                                            if (course.faculty.isNotBlank()) {
                                                Text(
                                                    text = course.faculty,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                                    color = StitchTextMuted
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(StitchSecondaryContainer.copy(alpha = 0.25f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${course.credit} Cr",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                                                color = StitchSecondary
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
