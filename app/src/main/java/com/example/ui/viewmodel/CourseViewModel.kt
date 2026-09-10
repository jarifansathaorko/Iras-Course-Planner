package com.example.ui.viewmodel

import android.app.Application
import androidx.core.content.edit

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.CourseEntity
import com.example.data.entity.PlanMetaEntity
import com.example.data.repository.CourseRepository
import com.example.domain.ConflictResult
import com.example.domain.CourseSearchHelper
import com.example.domain.ScheduleHelper
import com.example.util.PlanImageExporter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConflictAlert(
    val candidate: CourseEntity,
    val conflict: ConflictResult
)

sealed class UiMessage {
    data class Success(val message: String) : UiMessage()
    data class Info(val message: String) : UiMessage()
    data class Error(val message: String) : UiMessage()
}

class CourseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CourseRepository

    // Update States
    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()
    private var _pendingUpdateCsvUrl: String? = null

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = CourseRepository(db.courseDao(), db.selectedCourseDao(), db.planDao())
        viewModelScope.launch {
            repository.ensureDataInitialized()
            listenForUpdates()
        }
    }

    private fun listenForUpdates() {
        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        
        Firebase.firestore.collection("config").document("latest_data")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                
                val cloudVersion = snapshot.getLong("version")?.toInt() ?: 0
                val localVersion = prefs.getInt("local_csv_version", 0)
                
                if (cloudVersion > localVersion) {
                    _pendingUpdateCsvUrl = snapshot.getString("csvUrl")
                    if (_pendingUpdateCsvUrl != null) {
                        _showUpdateDialog.value = true
                    }
                }
            }
    }

    fun dismissUpdateDialog() {
        _showUpdateDialog.value = false
    }

    fun confirmUpdate() {
        val url = _pendingUpdateCsvUrl ?: return
        _showUpdateDialog.value = false
        
        viewModelScope.launch {
            try {
                val newCourses = com.example.data.UpdateManager.downloadAndParseCsv(url)
                if (newCourses.isNotEmpty()) {
                    repository.updateEntireCatalog(newCourses)
                    
                    // update local version
                    Firebase.firestore.collection("config").document("latest_data").get()
                        .addOnSuccessListener { snap ->
                            val v = snap.getLong("version")?.toInt() ?: 0
                            getApplication<Application>().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                                .edit { putInt("local_csv_version", v) }
                        }
                    
                    _uiEvents.emit(UiMessage.Success("Catalog updated successfully!"))
                } else {
                    _uiEvents.emit(UiMessage.Error("Failed to parse updated catalog."))
                }
            } catch (e: Exception) {
                _uiEvents.emit(UiMessage.Error("Network error while updating: ${e.message}"))
            }
        }
    }

    // --- Search & Filter States for Tab 1 ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDeptFilter = MutableStateFlow("ALL")
    val selectedDeptFilter: StateFlow<String> = _selectedDeptFilter.asStateFlow()

    private val _selectedCourseCodeDropdown = MutableStateFlow<String?>(null)
    val selectedCourseCodeDropdown: StateFlow<String?> = _selectedCourseCodeDropdown.asStateFlow()

    private val _selectedSectionDropdown = MutableStateFlow<Int?>(null)
    val selectedSectionDropdown: StateFlow<Int?> = _selectedSectionDropdown.asStateFlow()

    // Conflict Alert State
    private val _pendingConflict = MutableStateFlow<ConflictAlert?>(null)
    val pendingConflict: StateFlow<ConflictAlert?> = _pendingConflict.asStateFlow()

    // Change Section Dialog State (for Tab 2)
    private val _courseToChange = MutableStateFlow<CourseEntity?>(null)
    val courseToChange: StateFlow<CourseEntity?> = _courseToChange.asStateFlow()

    // Save to Plan Dialog State (for Tab 2)
    private val _showSavePlanDialog = MutableStateFlow(false)
    val showSavePlanDialog: StateFlow<Boolean> = _showSavePlanDialog.asStateFlow()

    // Active Plan sub-tab for Tab 3 (1, 2, 3, 4)
    private val _activePlanTab = MutableStateFlow(1)
    val activePlanTab: StateFlow<Int> = _activePlanTab.asStateFlow()

    // One-time UI events (Snackbar messages)
    private val _uiEvents = MutableSharedFlow<UiMessage>()
    val uiEvents: SharedFlow<UiMessage> = _uiEvents.asSharedFlow()

    // Data streams from repository
    val allCourses: StateFlow<List<CourseEntity>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCourseCodes: StateFlow<List<String>> = repository.allCourseCodes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCourses: StateFlow<List<CourseEntity>> = repository.selectedCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlanMeta: StateFlow<List<PlanMetaEntity>> = repository.allPlanMeta
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _plan1Courses = MutableStateFlow<List<CourseEntity>>(emptyList())
    val plan1Courses: StateFlow<List<CourseEntity>> = _plan1Courses.asStateFlow()

    private val _plan2Courses = MutableStateFlow<List<CourseEntity>>(emptyList())
    val plan2Courses: StateFlow<List<CourseEntity>> = _plan2Courses.asStateFlow()

    private val _plan3Courses = MutableStateFlow<List<CourseEntity>>(emptyList())
    val plan3Courses: StateFlow<List<CourseEntity>> = _plan3Courses.asStateFlow()

    private val _plan4Courses = MutableStateFlow<List<CourseEntity>>(emptyList())
    val plan4Courses: StateFlow<List<CourseEntity>> = _plan4Courses.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPlanCourses(1).collect { _plan1Courses.value = it }
        }
        viewModelScope.launch {
            repository.getPlanCourses(2).collect { _plan2Courses.value = it }
        }
        viewModelScope.launch {
            repository.getPlanCourses(3).collect { _plan3Courses.value = it }
        }
        viewModelScope.launch {
            repository.getPlanCourses(4).collect { _plan4Courses.value = it }
        }
    }

    fun getCoursesForPlan(planNumber: Int): List<CourseEntity> {
        return when (planNumber) {
            1 -> _plan1Courses.value
            2 -> _plan2Courses.value
            3 -> _plan3Courses.value
            4 -> _plan4Courses.value
            else -> emptyList()
        }
    }

    // Filtered courses for Tab 1:
    // Matches by Course Code or Course Code & Section according to spec:
    // "if only searched via course code it will return all the sections and their details,
    //  if it's searched via course code and section then it will return only that section details only."
    val filteredCourses: StateFlow<List<CourseEntity>> = combine(
        allCourses,
        _searchQuery,
        _selectedDeptFilter,
        _selectedCourseCodeDropdown,
        _selectedSectionDropdown
    ) { courses, query, dept, dropdownCode, dropdownSec ->
        CourseSearchHelper.filterCourses(
            courses = courses,
            query = query,
            dept = dept,
            dropdownCode = dropdownCode,
            dropdownSec = dropdownSec
        )
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions ---

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        // Reset dropdown if manual typing
        if (query.isNotEmpty()) {
            _selectedCourseCodeDropdown.value = null
            _selectedSectionDropdown.value = null
        }
    }

    fun onDeptFilterChange(dept: String) {
        _selectedDeptFilter.value = dept
    }

    fun onSelectCourseCodeDropdown(code: String?) {
        _selectedCourseCodeDropdown.value = code
        _selectedSectionDropdown.value = null
        if (code != null) {
            _searchQuery.value = ""
        }
    }

    fun onSelectSectionDropdown(sec: Int?) {
        _selectedSectionDropdown.value = sec
    }

    fun clearSearchAndFilters() {
        _searchQuery.value = ""
        _selectedDeptFilter.value = "ALL"
        _selectedCourseCodeDropdown.value = null
        _selectedSectionDropdown.value = null
    }

    /**
     * Attempts to select candidate course.
     * Checks conflict with currently selected courses.
     */
    fun selectCourse(candidate: CourseEntity) {
        val currentSelection = selectedCourses.value

        // If already selected, clicking the Selected button deselects the course
        if (currentSelection.any { it.id == candidate.id }) {
            dropCourse(candidate)
            return
        }

        // If course is 100% full, the user cannot select it
        if (candidate.capacity > 0 && candidate.enrolled >= candidate.capacity) {
            viewModelScope.launch {
                _uiEvents.emit(UiMessage.Error("${candidate.courseCode} (Sec ${candidate.section}) is 100% full (${candidate.enrolled}/${candidate.capacity}). Cannot select."))
            }
            return
        }

        // Run conflict check
        val conflict = ScheduleHelper.findConflict(candidate, currentSelection)
        if (conflict != null) {
            // Alert user of conflict
            _pendingConflict.value = ConflictAlert(candidate, conflict)
        } else {
            // No conflict -> add
            viewModelScope.launch {
                repository.selectCourse(candidate.id)
                _uiEvents.emit(UiMessage.Success("Added ${candidate.courseCode} (Sec ${candidate.section}) to schedule"))
            }
        }
    }

    fun dismissConflictDialog() {
        _pendingConflict.value = null
    }

    /**
     * Replaces an existing conflicting course with candidate (e.g. switching section or overriding)
     */
    fun resolveConflictByReplacing(candidate: CourseEntity, conflictingCourse: CourseEntity) {
        if (candidate.capacity > 0 && candidate.enrolled >= candidate.capacity) {
            viewModelScope.launch {
                _pendingConflict.value = null
                _uiEvents.emit(UiMessage.Error("${candidate.courseCode} (Sec ${candidate.section}) is 100% full. Cannot select."))
            }
            return
        }
        viewModelScope.launch {
            repository.changeSection(conflictingCourse.id, candidate.id)
            _pendingConflict.value = null
            _uiEvents.emit(UiMessage.Success("Replaced ${conflictingCourse.courseCode} (Sec ${conflictingCourse.section}) with Sec ${candidate.section}"))
        }
    }

    fun dropCourse(course: CourseEntity) {
        viewModelScope.launch {
            repository.deselectCourse(course.id)
            _uiEvents.emit(UiMessage.Info("Deselected ${course.courseCode} (Sec ${course.section})"))
        }
    }

    fun clearAllSelected() {
        viewModelScope.launch {
            repository.clearSelection()
            _uiEvents.emit(UiMessage.Info("Cleared all selected courses"))
        }
    }

    fun openChangeSectionDialog(course: CourseEntity) {
        _courseToChange.value = course
    }

    fun dismissChangeSectionDialog() {
        _courseToChange.value = null
    }

    fun changeCourseSection(oldCourse: CourseEntity, newCourse: CourseEntity) {
        val currentRemaining = selectedCourses.value.filter { it.id != oldCourse.id }
        val conflict = ScheduleHelper.findConflict(newCourse, currentRemaining)
        if (conflict != null) {
            _pendingConflict.value = ConflictAlert(newCourse, conflict)
        } else {
            viewModelScope.launch {
                repository.changeSection(oldCourse.id, newCourse.id)
                _courseToChange.value = null
                _uiEvents.emit(UiMessage.Success("Changed to ${newCourse.courseCode} (Sec ${newCourse.section})"))
            }
        }
    }

    fun openSavePlanDialog() {
        _showSavePlanDialog.value = true
    }

    fun dismissSavePlanDialog() {
        _showSavePlanDialog.value = false
    }

    fun confirmScheduleToPlan(planNumber: Int, planName: String) {
        val currentCourseIds = selectedCourses.value.map { it.id }
        if (currentCourseIds.isEmpty()) {
            viewModelScope.launch {
                _uiEvents.emit(UiMessage.Error("Cannot confirm empty schedule. Select courses first."))
            }
            return
        }
        viewModelScope.launch {
            repository.saveCurrentSelectionToPlan(planNumber, planName, currentCourseIds)
            _showSavePlanDialog.value = false
            _uiEvents.emit(UiMessage.Success("Schedule successfully confirmed and saved to $planName!"))
        }
    }

    fun setActivePlanTab(planNumber: Int) {
        _activePlanTab.value = planNumber
    }

    fun loadPlanIntoCurrentSelection(planNumber: Int) {
        viewModelScope.launch {
            repository.loadPlanIntoSelection(planNumber)
            _uiEvents.emit(UiMessage.Success("Plan $planNumber loaded into current selection!"))
        }
    }

    fun clearPlan(planNumber: Int) {
        viewModelScope.launch {
            repository.clearPlan(planNumber)
            _uiEvents.emit(UiMessage.Info("Plan $planNumber cleared."))
        }
    }

    fun sharePlanSchedule(context: android.content.Context, planNumber: Int) {
        val courses = getCoursesForPlan(planNumber)
        val meta = allPlanMeta.value.find { it.planNumber == planNumber }
        val planName = meta?.planName ?: "Plan $planNumber"

        if (courses.isEmpty()) {
            viewModelScope.launch {
                _uiEvents.emit(UiMessage.Info("Plan $planNumber has no courses to share yet."))
            }
            return
        }

        try {
            PlanImageExporter.exportAndSharePlan(context, planNumber, planName, courses)
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEvents.emit(UiMessage.Error("Failed to share image: ${e.localizedMessage}"))
            }
        }
    }
}
