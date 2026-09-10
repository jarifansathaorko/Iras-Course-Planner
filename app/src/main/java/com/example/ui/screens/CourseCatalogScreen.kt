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
import androidx.compose.ui.platform.LocalContext

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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.SelectedBlueBg
import com.example.ui.theme.SelectedBlueBorder
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
    modifier: Modifier = Modifier
) {
    var showCourseCodeMenu by remember { mutableStateOf(false) }
    var showSectionMenu by remember { mutableStateOf(false) }
    var showAdvancedPortalFilters by remember { mutableStateOf(false) }

    // Available sections for the currently chosen course code in dropdown
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
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Top Search Header ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Branded App Logo & Name Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_iras_logo),
                        contentDescription = "Iras Course Planner Logo",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Iras Course Planner",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "University Advising & Fast Conflict Detector",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Primary Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("course_search_bar"),
                    placeholder = {
                        Text(
                            "Search course e.g. 'CSE100' or 'CSE100 2'",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty() || selectedCourseCodeDropdown != null) {
                            IconButton(onClick = onClearFilters) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle Portal-style Course Code & Section Dropdowns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter by Course & Section Dropdown",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showAdvancedPortalFilters = !showAdvancedPortalFilters }
                    )
                    IconButton(
                        onClick = { showAdvancedPortalFilters = !showAdvancedPortalFilters },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (showAdvancedPortalFilters) Icons.Default.KeyboardArrowDown else Icons.Default.FilterList,
                            contentDescription = "Toggle Dropdowns",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Portal Dropdowns (Course Code & Section)
                AnimatedVisibility(visible = showAdvancedPortalFilters) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Course Code Selector
                        Box(modifier = Modifier.weight(1.3f)) {
                            OutlinedButton(
                                onClick = { showCourseCodeMenu = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("course_code_dropdown_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = selectedCourseCodeDropdown ?: "Select Code",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = showCourseCodeMenu,
                                onDismissRequest = { showCourseCodeMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All Courses") },
                                    onClick = {
                                        onCourseCodeDropdownSelect(null)
                                        showCourseCodeMenu = false
                                    }
                                )
                                allCourseCodes.forEach { code ->
                                    DropdownMenuItem(
                                        text = { Text(code) },
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
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (selectedSectionDropdown != null) "Sec $selectedSectionDropdown" else "All Sec",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = showSectionMenu,
                                onDismissRequest = { showSectionMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All Sections") },
                                    onClick = {
                                        onSectionDropdownSelect(null)
                                        showSectionMenu = false
                                    }
                                )
                                availableSectionsForDropdown.forEach { sec ->
                                    val secCourse = courses.find { it.courseCode.equals(selectedCourseCodeDropdown, ignoreCase = true) && it.section == sec }
                                    val enrolledInfo = if (secCourse != null) " (${secCourse.enrolled}/${secCourse.capacity})" else ""
                                    DropdownMenuItem(
                                        text = { Text("Section $sec$enrolledInfo") },
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

                // Department Horizontal Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DEPARTMENTS.forEach { dept ->
                        val isSelected = selectedDept == dept
                        FilterChip(
                            selected = isSelected,
                            onClick = { onDeptSelect(dept) },
                            label = { Text(dept, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        // Search Results Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${courses.size} Course Sections Found",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            .padding(start = 8.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Course List",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (selectedCourses.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${selectedCourses.size} Selected (${selectedCourses.sumOf { it.credit }} Cr)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No matching courses found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Try searching by course code (e.g. CSE100) or check department filters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = onClearFilters,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Reset Search")
                    }
                }
            }
        } else {
            val selectedIds = remember(selectedCourses) {
                selectedCourses.mapTo(HashSet(selectedCourses.size)) { it.id }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(courses, key = { it.id }) { course ->
                    val isSelected = course.id in selectedIds
                    val conflict = if (isSelected) null else ScheduleHelper.findConflict(course, selectedCourses)
                    val parsedTime = ScheduleHelper.parse(course.timeSlot)

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
    val isSameCourseConflict = conflict is com.example.domain.ConflictResult.SameCourse
    
    val cardBorderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        conflict != null -> MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    val cardBgColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("course_card_${course.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Code, Section & Credits
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = course.courseCode,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Sec ${course.section}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${course.credit} Credits",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Course Title
            Text(
                text = course.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Time Slot Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = parsedTime.formattedTime,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Raw: ${course.timeSlot}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val seatsLeft = (course.capacity - course.enrolled).coerceAtLeast(0)
            val isFull = course.capacity > 0 && course.enrolled >= course.capacity
            val isAlmostFull = seatsLeft in 1..5
            val occupancyFraction = if (course.capacity > 0) (course.enrolled.toFloat() / course.capacity.toFloat()).coerceIn(0f, 1f) else 0f

            // Faculty & Enrolled Info
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (course.faculty.isNotBlank()) course.faculty else "TBA",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Enrolled / Capacity Counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = if (isFull) ConflictRed else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${course.enrolled} / ${course.capacity} Enrolled",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isFull) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isFull) ConflictRed else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Enrollment Status Badge & Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            isFull -> ConflictRed.copy(alpha = 0.1f)
                            isAlmostFull -> Color(0xFFFF9800).copy(alpha = 0.12f)
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        }
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when {
                        isFull -> "Section Full"
                        isAlmostFull -> "Only $seatsLeft seats left!"
                        else -> "$seatsLeft seats available"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = when {
                        isFull -> ConflictRed
                        isAlmostFull -> Color(0xFFE65100)
                        else -> SuccessGreen
                    }
                )

                Text(
                    text = "${(occupancyFraction * 100).toInt()}% Filled",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { occupancyFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = when {
                    isFull -> ConflictRed
                    isAlmostFull -> Color(0xFFFF9800)
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Conflict / Status Alert Strip if applicable
            if (conflict != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ConflictRed.copy(alpha = 0.08f))
                        .border(1.dp, ConflictRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = ConflictRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (conflict) {
                            is ConflictResult.SameCourse -> "Already taking ${course.courseCode} (Sec ${conflict.existing.section})"
                            is ConflictResult.TimeOverlap -> "Clashes with ${conflict.existing.courseCode} (Sec ${conflict.existing.section})"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ConflictRed,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selection Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isSelected) {
                    Button(
                        onClick = onSelectClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("select_course_${course.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Deselect", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Selected")
                    }
                } else if (isFull) {
                    Button(
                        onClick = { /* Disabled: Course is 100% full */ },
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("select_course_${course.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Section Full (Closed)")
                    }
                } else {
                    Button(
                        onClick = onSelectClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isSameCourseConflict -> MaterialTheme.colorScheme.secondary
                                conflict != null -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.primary
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("select_course_${course.id}")
                    ) {
                        if (conflict != null) {
                            val btnIcon = if (isSameCourseConflict) androidx.compose.material.icons.Icons.Default.Info else androidx.compose.material.icons.Icons.Default.Warning
                            val btnText = if (isSameCourseConflict) "Switch to Sec ${course.section}" else "Resolve & Select"
                            Icon(imageVector = btnIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(btnText)
                        } else {
                            Text("+ Select Course")
                        }
                    }
                }
            }
        }
    }
}
