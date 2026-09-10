package com.example.data.repository

import com.example.data.CourseDataCatalog
import com.example.data.dao.CourseDao
import com.example.data.dao.PlanDao
import com.example.data.dao.SelectedCourseDao
import com.example.data.entity.CourseEntity
import com.example.data.entity.PlanMetaEntity
import com.example.data.entity.SelectedCourseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CourseRepository(
    private val courseDao: CourseDao,
    private val selectedCourseDao: SelectedCourseDao,
    private val planDao: PlanDao
) {
    val allCourses: Flow<List<CourseEntity>> = courseDao.getAllCourses()
    val allCourseCodes: Flow<List<String>> = courseDao.getAllCourseCodes()
    val selectedCourses: Flow<List<CourseEntity>> = selectedCourseDao.getSelectedCourseEntities()
    val allPlanMeta: Flow<List<PlanMetaEntity>> = planDao.getAllPlanMeta()

    fun getPlanCourses(planNumber: Int): Flow<List<CourseEntity>> {
        return planDao.getPlanCourses(planNumber)
    }

    suspend fun getPlanCoursesSync(planNumber: Int): List<CourseEntity> = withContext(Dispatchers.IO) {
        planDao.getPlanCoursesSync(planNumber)
    }

    suspend fun ensureDataInitialized() = withContext(Dispatchers.IO) {
        courseDao.insertAll(CourseDataCatalog.initialCourses)
        for (p in 1..4) {
            planDao.insertPlanMeta(
                PlanMetaEntity(
                    planNumber = p,
                    planName = "Plan $p",
                    savedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun selectCourse(courseId: Int) = withContext(Dispatchers.IO) {
        selectedCourseDao.selectCourse(SelectedCourseEntity(courseId = courseId))
    }

    suspend fun deselectCourse(courseId: Int) = withContext(Dispatchers.IO) {
        selectedCourseDao.deselectCourse(courseId)
    }

    suspend fun changeSection(oldCourseId: Int, newCourseId: Int) = withContext(Dispatchers.IO) {
        selectedCourseDao.replaceCourse(oldCourseId, newCourseId)
    }

    suspend fun clearSelection() = withContext(Dispatchers.IO) {
        selectedCourseDao.clearAllSelected()
    }

    suspend fun saveCurrentSelectionToPlan(
        planNumber: Int,
        planName: String,
        courseIds: List<Int>
    ) = withContext(Dispatchers.IO) {
        planDao.savePlan(planNumber, planName, courseIds)
    }

    suspend fun clearPlan(planNumber: Int) = withContext(Dispatchers.IO) {
        planDao.clearPlan(planNumber)
    }

    suspend fun loadPlanIntoSelection(planNumber: Int) = withContext(Dispatchers.IO) {
        val planCourses = planDao.getPlanCoursesSync(planNumber)
        selectedCourseDao.setSelectedCourses(planCourses.map { it.id })
    }
}
