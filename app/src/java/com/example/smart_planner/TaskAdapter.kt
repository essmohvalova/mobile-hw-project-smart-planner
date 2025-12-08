package com.example.smart_planner


import com.example.smart_planner.data.Priority
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_planner.data.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val tasks: List<Task>,
    private val onTaskChecked: (Task, Boolean) -> Unit // Колбэк для изменения статуса
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.text_task_title)
        val description: TextView = itemView.findViewById(R.id.text_task_description)
        val priorityFlag: TextView = itemView.findViewById(R.id.text_priority_flag)
        val dueDate: TextView = itemView.findViewById(R.id.text_due_date)
        val flagIcon: ImageView = itemView.findViewById(R.id.icon_flag)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_completed)

        // Для стилизации выполненной задачи
        val cardView: View = itemView.findViewById(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Устанавливаем текст
        holder.title.text = task.title

        // Отображаем описание
        if (task.description.isNotEmpty()) {
            holder.description.text = task.description
            holder.description.visibility = View.VISIBLE
        } else {
            holder.description.visibility = View.GONE
        }

        // Отображаем приоритет
        val priorityText = when (task.priority) {
            Priority.HIGH -> "❗️ ВЫСОКИЙ"
            Priority.MEDIUM -> "⚠️ СРЕДНИЙ"
            Priority.LOW -> "🔵 НИЗКИЙ"
        }
        holder.priorityFlag.text = priorityText

        // Устанавливаем цвет приоритета
        val priorityColor = when (task.priority) {
            Priority.HIGH -> R.color.priority_high
            Priority.MEDIUM -> R.color.priority_medium
            Priority.LOW -> R.color.priority_low
        }
        holder.priorityFlag.setTextColor(holder.itemView.context.getColor(priorityColor))

        // Отображаем флаг
        if (task.hasFlag) {
            holder.flagIcon.visibility = View.VISIBLE
        } else {
            holder.flagIcon.visibility = View.GONE
        }

        // Отображаем дату выполнения
        if (task.dueDate != null) {
            holder.dueDate.text = dateFormat.format(task.dueDate)
            holder.dueDate.visibility = View.VISIBLE
        } else {
            holder.dueDate.visibility = View.GONE
        }

        // Устанавливаем состояние чекбокса
        holder.checkBox.isChecked = task.isCompleted

        // Стилизуем выполненную задачу
        if (task.isCompleted) {
            // Затемняем текст
            holder.title.alpha = 0.5f
            holder.description.alpha = 0.5f
            holder.priorityFlag.alpha = 0.5f
            holder.dueDate.alpha = 0.5f

            // Добавляем strike-through (зачеркивание)
            holder.title.paintFlags = holder.title.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            holder.description.paintFlags = holder.description.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG

            // Серый фон для выполненной задачи
            holder.cardView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.darker_gray))
        } else {
            // Восстанавливаем стили
            holder.title.alpha = 1f
            holder.description.alpha = 1f
            holder.priorityFlag.alpha = 1f
            holder.dueDate.alpha = 1f

            holder.title.paintFlags = holder.title.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.description.paintFlags = holder.description.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()

            // Белый фон для невыполненной задачи
            holder.cardView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.white))
        }

        // Обработчик изменения чекбокса
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onTaskChecked(task, isChecked)
        }

        // Также можно кликать на всю карточку для отметки
        holder.itemView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    override fun getItemCount(): Int = tasks.size
}