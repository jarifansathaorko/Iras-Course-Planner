package com.example.data.entity

import androidx.room.Entity

@Entity(
    tableName = "plan_courses",
    primaryKeys = ["planNumber", "courseId"]
)
data class PlanCourseEntity(
    val planNumber: Int,
    val courseId: Int
)
