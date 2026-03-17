package com.example.smart_planner.domain.models.tasks

import com.example.smart_planner.R
enum class Priority {
    LOW,
    MEDIUM,
    HIGH;

    fun getDisplayName(): String {
        return when (this) {
            LOW -> "Низкий"
            MEDIUM -> "Средний"
            HIGH -> "Высокий"
        }
    }

    fun getColorRes(): Int {
        return when (this) {
            LOW -> R.color.priority_low
            MEDIUM -> R.color.priority_medium
            HIGH -> R.color.priority_high
        }
    }
}