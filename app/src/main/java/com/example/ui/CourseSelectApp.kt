package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.ConflictResult
import com.example.ui.components.ChangeSectionDialog
import com.example.ui.components.ConflictAlertDialog
import com.example.ui.components.ConfirmScheduleDialog
import com.example.ui.components.UpdateDataDialog
import com.example.ui.screens.CourseCatalogScreen
import com.example.ui.screens.SavedPlansScreen
import com.example.ui.screens.SelectedCoursesScreen
import com.example.ui.viewmodel.CourseViewModel
import com.example.ui.viewmodel.UiMessage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CourseSelectApp(
    viewModel: CourseViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Browse, 1: Schedule, 2: Plans
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect states from ViewModel
    val allCourses by viewModel.allCourses.collectAsState()
    val filteredCourses by viewModel.filteredCourses.collectAsState()
    val allCourseCodes by viewModel.allCourseCodes.collectAsState()
    val selectedCourses by viewModel.selectedCourses.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDept by viewModel.selectedDeptFilter.collectAsState()
    val selectedCourseCodeDropdown by viewModel.selectedCourseCodeDropdown.collectAsState()
    val selectedSectionDropdown by viewModel.selectedSectionDropdown.collectAsState()

    val pendingConflict by viewModel.pendingConflict.collectAsState()
    val courseToChange by viewModel.courseToChange.collectAsState()
    val showSavePlanDialog by viewModel.showSavePlanDialog.collectAsState()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()

    val activePlanTab by viewModel.activePlanTab.collectAsState()
    val allPlanMeta by viewModel.allPlanMeta.collectAsState()
    val plan1Courses by viewModel.plan1Courses.collectAsState()
    val plan2Courses by viewModel.plan2Courses.collectAsState()
    val plan3Courses by viewModel.plan3Courses.collectAsState()
    val plan4Courses by viewModel.plan4Courses.collectAsState()

    // Listen to ViewModel events (Snackbars)
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is UiMessage.Success -> snackbarHostState.showSnackbar(event.message)
                is UiMessage.Info -> snackbarHostState.showSnackbar(event.message)
                is UiMessage.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                // Tab 1: Browse Courses
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.testTag("tab_browse"),
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Browse Courses",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Browse",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                // Tab 2: Selected Courses (with live badge)
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.testTag("tab_schedule"),
                    icon = {
                        if (selectedCourses.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text("${selectedCourses.size}")
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "My Schedule",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "My Schedule",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "My Schedule",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                // Tab 3: Saved Plans
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    modifier = Modifier.testTag("tab_plans"),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Saved Plans",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Saved Plans",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    CourseCatalogScreen(
                        courses = filteredCourses,
                        allCourseCodes = allCourseCodes,
                        selectedCourses = selectedCourses,
                        searchQuery = searchQuery,
                        selectedDept = selectedDept,
                        selectedCourseCodeDropdown = selectedCourseCodeDropdown,
                        selectedSectionDropdown = selectedSectionDropdown,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onDeptSelect = viewModel::onDeptFilterChange,
                        onCourseCodeDropdownSelect = viewModel::onSelectCourseCodeDropdown,
                        onSectionDropdownSelect = viewModel::onSelectSectionDropdown,
                        onSelectCourse = viewModel::selectCourse,
                        onClearFilters = viewModel::clearSearchAndFilters
                    )
                }
                1 -> {
                    SelectedCoursesScreen(
                        selectedCourses = selectedCourses,
                        onChangeSectionClick = viewModel::openChangeSectionDialog,
                        onDropCourseClick = viewModel::dropCourse,
                        onConfirmScheduleClick = viewModel::openSavePlanDialog,
                        onNavigateToCatalog = { selectedTab = 0 },
                        onClearAll = viewModel::clearAllSelected
                    )
                }
                2 -> {
                    SavedPlansScreen(
                        activePlanNumber = activePlanTab,
                        plan1Courses = plan1Courses,
                        plan2Courses = plan2Courses,
                        plan3Courses = plan3Courses,
                        plan4Courses = plan4Courses,
                        allPlanMeta = allPlanMeta,
                        onSelectPlanTab = viewModel::setActivePlanTab,
                        onSharePlan = viewModel::sharePlanSchedule,
                        onLoadPlanIntoSelection = { planNum ->
                            viewModel.loadPlanIntoCurrentSelection(planNum)
                            selectedTab = 1
                        },
                        onClearPlan = viewModel::clearPlan
                    )
                }
            }
        }

        // --- Dialogs ---

        // Conflict Alert Dialog
        pendingConflict?.let { alert ->
            ConflictAlertDialog(
                conflictAlert = alert,
                onDismiss = viewModel::dismissConflictDialog,
                onReplaceConflicting = {
                    val conflicting = when (val c = alert.conflict) {
                        is ConflictResult.SameCourse -> c.existing
                        is ConflictResult.TimeOverlap -> c.existing
                    }
                    viewModel.resolveConflictByReplacing(alert.candidate, conflicting)
                }
            )
        }

        // Change Section Dialog (from Tab 2)
        courseToChange?.let { course ->
            val allSectionsOfThisCode = allCourses.filter {
                it.courseCode.equals(course.courseCode, ignoreCase = true)
            }
            ChangeSectionDialog(
                currentCourse = course,
                allCoursesOfCode = allSectionsOfThisCode,
                selectedCourses = selectedCourses,
                onDismiss = viewModel::dismissChangeSectionDialog,
                onSelectNewSection = { newCandidate ->
                    viewModel.changeCourseSection(course, newCandidate)
                }
            )
        }

        // Confirm Schedule Dialog (from Tab 2)
        if (showSavePlanDialog) {
            ConfirmScheduleDialog(
                selectedCourses = selectedCourses,
                allPlanMeta = allPlanMeta,
                plan1Count = plan1Courses.size,
                plan2Count = plan2Courses.size,
                plan3Count = plan3Courses.size,
                plan4Count = plan4Courses.size,
                onDismiss = viewModel::dismissSavePlanDialog,
                onConfirm = { planNumber, planName ->
                    viewModel.confirmScheduleToPlan(planNumber, planName)
                }
            )
        }

        // Update Data Dialog
        UpdateDataDialog(
            showDialog = showUpdateDialog,
            onDismiss = viewModel::dismissUpdateDialog,
            onConfirm = viewModel::confirmUpdate
        )
    }
}
