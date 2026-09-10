package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_courses")
data class SelectedCourseEntity(
    @PrimaryKey val courseId: Int,
    val selectedAt: Long = System.currentTimeMillis()
)
