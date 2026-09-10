package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plan_meta")
data class PlanMetaEntity(
    @PrimaryKey val planNumber: Int,
    val planName: String,
    val savedAt: Long = System.currentTimeMillis()
)
