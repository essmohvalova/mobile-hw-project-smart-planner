package com.example.smart_planner.data

import java.io.Serializable
import java.util.*

data class Task(
    val id: Long = System.currentTimeMillis(), // Используем timestamp как ID
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Date? = null,
    val hasFlag: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date() // Дата создания
) : Serializable