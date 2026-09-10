package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Int,
    val courseCode: String,
    val title: String,
    val section: Int,
    val timeSlot: String,
    val enrolled: Int,
    val capacity: Int,
    val credit: Int,
    val faculty: String
)
