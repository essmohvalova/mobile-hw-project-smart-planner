package com.example.smart_planner.data.repository.tasks

import com.example.smart_planner.data.models.tasks.TaskEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor() : TaskRepository {

    private val taskList = mutableListOf<TaskEntity>()

    override fun getAllTasks(): List<TaskEntity> = taskList.toList()

    override fun getTaskById(taskId: Long): TaskEntity? {
        return taskList.find { it.id == taskId }
    }

    override fun addTask(task: TaskEntity) {
        taskList.add(task)
    }

    override fun updateTask(updatedTask: TaskEntity) {
        val index = taskList.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            taskList[index] = updatedTask
        }
    }

    override fun deleteTask(taskId: Long): Boolean {
        return taskList.removeIf { it.id == taskId }
    }

    override fun clearAllTasks() {
        taskList.clear()
    }
}