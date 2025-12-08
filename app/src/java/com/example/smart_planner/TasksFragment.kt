package com.example.smart_planner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_planner.data.Task

class TasksFragment : Fragment() {

    companion object {
        const val REQUEST_CODE_ADD_TASK = 1001
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var textNoTasks: TextView
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        textNoTasks = view.findViewById(R.id.text_no_tasks)

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Инициализация адаптера с колбэком для изменения статуса
        taskAdapter = TaskAdapter(TaskRepository.getTasks()) { task, isCompleted ->
            // Создаем обновленную задачу
            val updatedTask = task.copy(isCompleted = isCompleted)

            // Обновляем в репозитории
            TaskRepository.updateTask(updatedTask)

            // Показываем сообщение
            val status = if (isCompleted) "выполнена" else "не выполнена"
            Toast.makeText(
                requireContext(),
                "Задача \"${task.title}\" отмечена как $status",
                Toast.LENGTH_SHORT
            ).show()
        }

        recyclerView.adapter = taskAdapter

        // Слушатель для обновления при возвращении из CreateTaskActivity
        parentFragmentManager.setFragmentResultListener("task_added", this) { _, _ ->
            updateTasksList()
        }

        updateUI()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем список при возвращении на фрагмент
        updateTasksList()
    }

    private fun updateTasksList() {
        // Получаем все задачи и сортируем: сначала невыполненные, потом выполненные
        val tasks = TaskRepository.getTasks().sortedBy { it.isCompleted }
        taskAdapter = TaskAdapter(tasks) { task, isCompleted ->
            val updatedTask = task.copy(isCompleted = isCompleted)
            TaskRepository.updateTask(updatedTask)

            // Пересоздаем адаптер с обновленным списком
            updateTasksList()

            val status = if (isCompleted) "выполнена" else "не выполнена"
            Toast.makeText(
                requireContext(),
                "Задача \"${task.title}\" отмечена как $status",
                Toast.LENGTH_SHORT
            ).show()
        }
        recyclerView.adapter = taskAdapter
        updateUI()
    }

    private fun updateUI() {
        val hasTasks = TaskRepository.getTasks().isNotEmpty()

        if (hasTasks) {
            textNoTasks.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            textNoTasks.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            textNoTasks.text = "Нажми + чтобы добавить задачу"
        }
    }
}