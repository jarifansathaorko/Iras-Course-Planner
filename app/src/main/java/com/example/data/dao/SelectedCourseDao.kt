package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.entity.CourseEntity
import com.example.data.entity.SelectedCourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedCourseDao {
    @Query("SELECT * FROM selected_courses")
    fun getAllSelected(): Flow<List<SelectedCourseEntity>>

    @Query("""
        SELECT c.* FROM courses c
        INNER JOIN selected_courses s ON c.id = s.courseId
        ORDER BY c.courseCode ASC, c.section ASC
    """)
    fun getSelectedCourseEntities(): Flow<List<CourseEntity>>

    @Query("""
        SELECT c.* FROM courses c
        INNER JOIN selected_courses s ON c.id = s.courseId
    """)
    suspend fun getSelectedCourseEntitiesSync(): List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun selectCourse(selectedCourse: SelectedCourseEntity)

    @Query("DELETE FROM selected_courses WHERE courseId = :courseId")
    suspend fun deselectCourse(courseId: Int)

    @Query("DELETE FROM selected_courses")
    suspend fun clearAllSelected()

    @Transaction
    suspend fun replaceCourse(oldCourseId: Int, newCourseId: Int) {
        deselectCourse(oldCourseId)
        selectCourse(SelectedCourseEntity(courseId = newCourseId))
    }

    @Transaction
    suspend fun setSelectedCourses(courseIds: List<Int>) {
        clearAllSelected()
        for (id in courseIds) {
            selectCourse(SelectedCourseEntity(courseId = id))
        }
    }
}
