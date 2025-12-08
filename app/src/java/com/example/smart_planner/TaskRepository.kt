package com.example.smart_planner

import com.example.smart_planner.data.Task

object TaskRepository {
    private val taskList = mutableListOf<Task>()

    fun addTask(task: Task) {
        taskList.add(task)
    }

    fun getTasks(): List<Task> = taskList.toList()

    // Новый метод для обновления задачи
    fun updateTask(updatedTask: Task) {
        val index = taskList.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            taskList[index] = updatedTask
        }
    }

    // Удаление задачи
    fun deleteTask(taskId: Long): Boolean {
        return taskList.removeIf { it.id == taskId }
    }

    // Получение задачи по ID
    fun getTaskById(taskId: Long): Task? {
        return taskList.find { it.id == taskId }
    }

    fun clearTasks() {
        taskList.clear()
    }
}