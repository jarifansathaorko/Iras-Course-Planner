package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.ConflictRed
import com.example.ui.theme.ConflictRedBg
import com.example.ui.theme.ConflictRedBorder
import com.example.ui.theme.StitchBackground
import com.example.ui.theme.StitchOutline
import com.example.ui.theme.StitchOutlineVariant
import com.example.ui.theme.StitchPrimary
import com.example.ui.theme.StitchPrimaryContainer
import com.example.ui.theme.StitchSecondary
import com.example.ui.theme.StitchSecondaryContainer
import com.example.ui.theme.StitchSurfaceContainer
import com.example.ui.theme.StitchSurfaceHigh
import com.example.ui.theme.StitchSurfaceHighest
import com.example.ui.theme.StitchSurfaceLow
import com.example.ui.theme.StitchTertiary
import com.example.ui.theme.StitchTextMuted
import com.example.ui.theme.StitchTextPrimary
import com.example.ui.theme.StitchTextSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg
import com.example.ui.theme.SuccessGreenBorder

private val DEPARTMENTS = listOf(
    "ALL", "CSE", "MAT", "PHY", "ENG", "ECN", "CMN", "GSG", "HEA",
    "NCH", "SOC", "CHE", "BLA", "BNG", "BPH", "CHI", "GRM", "HST",
    "LEL", "LFE", "MUS", "PHL"
)

@Composable
fun CourseCatalogScreen(
    courses: List<CourseEntity>,
    allCourseCodes: List<String>,
    selectedCourses: List<CourseEntity>,
    searchQuery: String,
    selectedDept: String,
    selectedCourseCodeDropdown: String?,
    selectedSectionDropdown: Int?,
    onSearchQueryChange: (String) -> Unit,
    onDeptSelect: (String) -> Unit,
    onCourseCodeDropdownSelect: (String?) -> Unit,
    onSectionDropdownSelect: (Int?) -> Unit,
    onSelectCourse: (CourseEntity) -> Unit,
    onClearFilters: () -> Unit,
    onOpenAppUpdate: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCourseCodeMenu by remember { mutableStateOf(false) }
    var showSectionMenu by remember { mutableStateOf(false) }
    var showAdvancedPortalFilters by remember { mutableStateOf(false) }

    val availableSectionsForDropdown = remember(selectedCourseCodeDropdown, courses) {
        if (selectedCourseCodeDropdown.isNullOrBlank()) emptyList()
        else courses.filter { it.courseCode.equals(selectedCourseCodeDropdown, ignoreCase = true) }
            .map { it.section }
            .distinct()
            .sorted()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StitchBackground)
    ) {
        // --- Top App Bar & Search Container (Matching Stitch 02_browse-and-search.html) ---
        val outlineColor = StitchOutline
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
                // Branded App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
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

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Iras Course Planner",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = (-0.01).sp
                            ),
                            color = StitchTextPrimary
                        )
                        Text(
                            text = "Advising & Conflict Detector",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = StitchTextSecondary
                        )
                    }

                    // App Update Icon Button
                    IconButton(
                        onClick = onOpenAppUpdate,
                        modifier = Modifier
                            .testTag("btn_check_github_update")
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StitchSurfaceContainer)
                            .border(1.dp, StitchOutline, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "Check for Updates",
                            tint = StitchPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Search Bar (Stitch Sleek Input Box)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("course_search_bar"),
                    placeholder = {
                        Text(
                            "Search course e.g. 'CSE100' or 'CSE100 2'",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                            color = StitchTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = StitchTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty() || selectedCourseCodeDropdown != null) {
                            IconButton(onClick = onClearFilters) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Search",
                                    tint = StitchTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StitchSurfaceContainer,
                        unfocusedContainerColor = StitchSurfaceLow,
                        focusedBorderColor = StitchPrimaryContainer,
                        unfocusedBorderColor = StitchOutline,
                        focusedTextColor = StitchTextPrimary,
                        unfocusedTextColor = StitchTextPrimary,
                        cursorColor = StitchPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category & Department Filter Strip (Pill Row with Tune Icon)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DEPARTMENTS.forEach { dept ->
                            val isSelected = selectedDept == dept
                            val pillBg = if (isSelected) StitchPrimaryContainer else StitchSurfaceLow
                            val pillBorder = if (isSelected) StitchPrimaryContainer else StitchOutline
                            val pillText = if (isSelected) Color.White else StitchTextSecondary

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(pillBg)
                                    .border(1.dp, pillBorder, RoundedCornerShape(20.dp))
                                    .clickable { onDeptSelect(dept) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                    }
                                    Text(
                                        text = dept,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.5.sp
                                        ),
                                        color = pillText
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Tune / Portal Filters Toggle Button
                    IconButton(
                        onClick = { showAdvancedPortalFilters = !showAdvancedPortalFilters },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (showAdvancedPortalFilters) StitchPrimaryContainer.copy(alpha = 0.2f) else StitchSurfaceLow)
                            .border(1.dp, if (showAdvancedPortalFilters) StitchPrimaryContainer else StitchOutline, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Toggle Portal Filters",
                            tint = if (showAdvancedPortalFilters) StitchPrimary else StitchTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Advanced Portal Dropdowns (Course Code & Section)
                AnimatedVisibility(visible = showAdvancedPortalFilters) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Course Code Selector
                            Box(modifier = Modifier.weight(1.3f)) {
                                OutlinedButton(
                                    onClick = { showCourseCodeMenu = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("course_code_dropdown_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = StitchSurfaceContainer,
                                        contentColor = StitchTextPrimary
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline)
                                ) {
                                    Text(
                                        text = selectedCourseCodeDropdown ?: "Select Code",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 12.sp,
                                        color = StitchTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = StitchTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showCourseCodeMenu,
                                    onDismissRequest = { showCourseCodeMenu = false },
                                    modifier = Modifier.background(StitchSurfaceContainer)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("All Courses", color = StitchTextPrimary) },
                                        onClick = {
                                            onCourseCodeDropdownSelect(null)
                                            showCourseCodeMenu = false
                                        }
                                    )
                                    allCourseCodes.forEach { code ->
                                        DropdownMenuItem(
                                            text = { Text(code, color = StitchTextPrimary) },
                                            onClick = {
                                                onCourseCodeDropdownSelect(code)
                                                showCourseCodeMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Section Selector
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedButton(
                                    onClick = { showSectionMenu = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("section_dropdown_btn"),
                                    enabled = selectedCourseCodeDropdown != null,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = StitchSurfaceContainer,
                                        contentColor = StitchTextPrimary
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline)
                                ) {
                                    Text(
                                        text = if (selectedSectionDropdown != null) "Sec $selectedSectionDropdown" else "All Sec",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 12.sp,
                                        color = StitchTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = StitchTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showSectionMenu,
                                    onDismissRequest = { showSectionMenu = false },
                                    modifier = Modifier.background(StitchSurfaceContainer)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("All Sections", color = StitchTextPrimary) },
                                        onClick = {
                                            onSectionDropdownSelect(null)
                                            showSectionMenu = false
                                        }
                                    )
                                    availableSectionsForDropdown.forEach { sec ->
                                        val secCourse = courses.find { it.courseCode.equals(selectedCourseCodeDropdown, ignoreCase = true) && it.section == sec }
                                        val enrolledInfo = if (secCourse != null) " (${secCourse.enrolled}/${secCourse.capacity})" else ""
                                        DropdownMenuItem(
                                            text = { Text("Section $sec$enrolledInfo", color = StitchTextPrimary) },
                                            onClick = {
                                                onSectionDropdownSelect(sec)
                                                showSectionMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Results Summary Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${courses.size} Course Sections Found",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    ),
                    color = StitchTextSecondary
                )

                val isFiltered = searchQuery.isNotEmpty() || selectedDept != "ALL" || selectedCourseCodeDropdown != null || selectedSectionDropdown != null
                if (isFiltered && courses.isNotEmpty()) {
                    val context = LocalContext.current
                    IconButton(
                        onClick = {
                            val filterDescription = if (selectedCourseCodeDropdown != null) {
                                if (selectedSectionDropdown != null) {
                                    "Course: $selectedCourseCodeDropdown, Section: $selectedSectionDropdown"
                                } else {
                                    "Course: $selectedCourseCodeDropdown"
                                }
                            } else if (searchQuery.isNotEmpty()) {
                                "Search: \"$searchQuery\""
                            } else {
                                "Department: $selectedDept"
                            }
                            com.example.util.PlanImageExporter.exportAndShareCourseList(
                                context = context,
                                courses = courses,
                                filterText = filterDescription
                            )
                        },
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Course List",
                            tint = StitchPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (selectedCourses.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StitchPrimaryContainer.copy(alpha = 0.16f))
                        .border(1.dp, StitchPrimaryContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${selectedCourses.size} Selected (${selectedCourses.sumOf { it.credit }} Cr)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StitchPrimary
                    )
                }
            }
        }

        // --- Courses List ---
        if (courses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                        text = "No course sections found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = StitchTextPrimary
                    )
                    Text(
                        text = "Try refining your search terms or selecting another department filter above.",
                        style = MaterialTheme.typography.bodySmall,
                        color = StitchTextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    OutlinedButton(
                        onClick = onClearFilters,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = StitchSurfaceContainer,
                            contentColor = StitchPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StitchOutline)
                    ) {
                        Text("Reset Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = courses,
                    key = { it.id }
                ) { course ->
                    val isSelected = selectedCourses.any { it.id == course.id }
                    val conflict = if (isSelected) null else ScheduleHelper.findConflict(course, selectedCourses)
                    val parsedTime = remember(course.timeSlot) { ScheduleHelper.parse(course.timeSlot) }

                    CourseCatalogCard(
                        course = course,
                        parsedTime = parsedTime,
                        isSelected = isSelected,
                        conflict = conflict,
                        onSelectClick = { onSelectCourse(course) }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseCatalogCard(
    course: CourseEntity,
    parsedTime: com.example.domain.ParsedSchedule,
    isSelected: Boolean,
    conflict: ConflictResult?,
    onSelectClick: () -> Unit
) {
    val isSameCourseConflict = conflict is ConflictResult.SameCourse

    val cardBorderColor = when {
        isSelected -> StitchTertiary
        conflict != null -> ConflictRedBorder
        else -> StitchOutline
    }

    val cardBgColor = when {
        isSelected -> StitchSurfaceContainer
        else -> StitchSurfaceContainer
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.5.dp else 1.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("course_card_${course.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Course Code, Section Pill & Status/Credit Tag
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

                // Status or Credit Badge
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SuccessGreenBg)
                            .border(1.dp, SuccessGreenBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.5.dp)
                    ) {
                        Text(
                            text = "ENROLLED",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = StitchTertiary
                        )
                    }
                } else if (conflict != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ConflictRedBg)
                            .border(1.dp, ConflictRedBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.5.dp)
                    ) {
                        Text(
                            text = if (isSameCourseConflict) "DIFF SEC" else "CONFLICT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = ConflictRed
                        )
                    }
                } else {
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
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Course Title
            Text(
                text = course.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                color = StitchTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Schedule & Room Banner
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

            val seatsLeft = (course.capacity - course.enrolled).coerceAtLeast(0)
            val isFull = course.capacity > 0 && course.enrolled >= course.capacity
            val isAlmostFull = seatsLeft in 1..5
            val occupancyFraction = if (course.capacity > 0) (course.enrolled.toFloat() / course.capacity.toFloat()).coerceIn(0f, 1f) else 0f

            // Faculty & Enrolled Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
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

                // Seats Counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = if (isFull) ConflictRed else StitchTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${course.enrolled}/${course.capacity} Seats",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isFull) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.5.sp
                        ),
                        color = if (isFull) ConflictRed else StitchTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Capacity Progress Bar
            LinearProgressIndicator(
                progress = { occupancyFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = when {
                    isFull -> ConflictRed
                    isAlmostFull -> Color(0xFFF59E0B)
                    else -> StitchTertiary
                },
                trackColor = StitchSurfaceHigh
            )

            // Conflict Warning Banner
            if (conflict != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ConflictRedBg)
                        .border(1.dp, ConflictRedBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = ConflictRed,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (conflict) {
                            is ConflictResult.SameCourse -> "Already enrolled in Sec ${conflict.existing.section}"
                            is ConflictResult.TimeOverlap -> "Clashes with ${conflict.existing.courseCode} (Sec ${conflict.existing.section})"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                        color = ConflictRed,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isSelected) {
                    Button(
                        onClick = onSelectClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreenBg,
                            contentColor = StitchTertiary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreenBorder),
                        modifier = Modifier.testTag("select_course_${course.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Enrolled", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Selected", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else if (isFull) {
                    Button(
                        onClick = { /* Full */ },
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = StitchSurfaceHigh,
                            disabledContentColor = StitchTextMuted
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("select_course_${course.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Block, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Section Closed", fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = onSelectClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isSameCourseConflict -> StitchSecondaryContainer
                                conflict != null -> ConflictRedBorder
                                else -> StitchPrimaryContainer
                            },
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("select_course_${course.id}")
                    ) {
                        if (conflict != null) {
                            val btnIcon = if (isSameCourseConflict) Icons.Default.Info else Icons.Default.Warning
                            val btnText = if (isSameCourseConflict) "Switch to Sec ${course.section}" else "Resolve Conflict"
                            Icon(imageVector = btnIcon, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(btnText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text("+ Select Section", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
