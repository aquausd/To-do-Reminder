package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "Personal",
    val dueDate: String, // "YYYY-MM-DD"
    val dueTime: String? = null, // "HH:mm" e.g. "09:00", or null
    val priority: String = "Medium", // "Low", "Medium", "High"
    val isCompleted: Boolean = false,
    val notes: String = "",
    val reminderMinutesBefore: Int? = 15, // null or minutes (0, 5, 15, 30, 60, 1440)
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
