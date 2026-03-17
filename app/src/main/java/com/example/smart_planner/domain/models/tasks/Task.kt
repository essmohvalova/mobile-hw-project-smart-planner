package com.example.smart_planner.domain.models.tasks

import java.util.Date

/**
 * Доменная модель задачи - используется во всем приложении
 * Не содержит зависимостей от фреймворков и библиотек
 */
data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val priority: Priority,
    val dueDate: Date?,
    val hasFlag: Boolean,
    val isCompleted: Boolean,
    val createdAt: Date
) {
    // Бизнес-логика, относящаяся к задаче
    fun isOverdue(): Boolean {
        return dueDate?.let { it.before(Date()) && !isCompleted } ?: false
    }

    fun isHighPriority(): Boolean {
        return priority == Priority.HIGH
    }

    fun toggleCompletion(): Task {
        return this.copy(isCompleted = !this.isCompleted)
    }
}