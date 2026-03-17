package com.example.smart_planner.data.models.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smart_planner.domain.models.tasks.Priority
import com.example.smart_planner.domain.models.tasks.Task
import java.util.Date

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String,
    val priority: Priority,
    val dueDate: Long?,
    val hasFlag: Boolean,
    val isCompleted: Boolean,
    val createdAt: Long
) {
    // Маппер из Entity в Domain модель
    fun toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            priority = priority,
            dueDate = dueDate?.let { Date(it) },
            hasFlag = hasFlag,
            isCompleted = isCompleted,
            createdAt = Date(createdAt)
        )
    }

    companion object {
        // Маппер из Domain модели в Entity
        fun fromDomain(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = task.priority,
                dueDate = task.dueDate?.time,
                hasFlag = task.hasFlag,
                isCompleted = task.isCompleted,
                createdAt = task.createdAt.time
            )
        }
    }
}