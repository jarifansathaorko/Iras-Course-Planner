package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY courseCode ASC, section ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: Int): CourseEntity?

    @Query("SELECT * FROM courses WHERE id IN (:ids)")
    fun getCoursesByIds(ids: List<Int>): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id IN (:ids)")
    suspend fun getCoursesByIdsSync(ids: List<Int>): List<CourseEntity>

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun getCourseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Query("DELETE FROM courses")
    suspend fun deleteAllCourses()

    @Query("SELECT DISTINCT courseCode FROM courses ORDER BY courseCode ASC")
    fun getAllCourseCodes(): Flow<List<String>>
}
