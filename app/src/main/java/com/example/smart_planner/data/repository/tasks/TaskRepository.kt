package com.example.smart_planner.data.repository.tasks

import com.example.smart_planner.data.models.tasks.TaskEntity

interface TaskRepository {
    fun getAllTasks(): List<TaskEntity>
    fun getTaskById(taskId: Long): TaskEntity?
    fun addTask(task: TaskEntity)
    fun updateTask(task: TaskEntity)
    fun deleteTask(taskId: Long): Boolean
    fun clearAllTasks()
}