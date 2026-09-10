package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.entity.CourseEntity
import com.example.data.entity.PlanCourseEntity
import com.example.data.entity.PlanMetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("""
        SELECT c.* FROM courses c
        INNER JOIN plan_courses p ON c.id = p.courseId
        WHERE p.planNumber = :planNumber
        ORDER BY c.courseCode ASC, c.section ASC
    """)
    fun getPlanCourses(planNumber: Int): Flow<List<CourseEntity>>

    @Query("""
        SELECT c.* FROM courses c
        INNER JOIN plan_courses p ON c.id = p.courseId
        WHERE p.planNumber = :planNumber
        ORDER BY c.courseCode ASC, c.section ASC
    """)
    suspend fun getPlanCoursesSync(planNumber: Int): List<CourseEntity>

    @Query("SELECT * FROM plan_meta ORDER BY planNumber ASC")
    fun getAllPlanMeta(): Flow<List<PlanMetaEntity>>

    @Query("SELECT * FROM plan_meta WHERE planNumber = :planNumber")
    fun getPlanMeta(planNumber: Int): Flow<PlanMetaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanMeta(meta: PlanMetaEntity)

    @Query("DELETE FROM plan_courses WHERE planNumber = :planNumber")
    suspend fun clearPlanCourses(planNumber: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanCourses(courses: List<PlanCourseEntity>)

    @Transaction
    suspend fun savePlan(planNumber: Int, planName: String, courseIds: List<Int>) {
        insertPlanMeta(
            PlanMetaEntity(
                planNumber = planNumber,
                planName = planName,
                savedAt = System.currentTimeMillis()
            )
        )
        clearPlanCourses(planNumber)
        val entities = courseIds.map { PlanCourseEntity(planNumber = planNumber, courseId = it) }
        insertPlanCourses(entities)
    }

    @Transaction
    suspend fun clearPlan(planNumber: Int) {
        clearPlanCourses(planNumber)
        insertPlanMeta(
            PlanMetaEntity(
                planNumber = planNumber,
                planName = "Plan $planNumber (Empty)",
                savedAt = System.currentTimeMillis()
            )
        )
    }
}
