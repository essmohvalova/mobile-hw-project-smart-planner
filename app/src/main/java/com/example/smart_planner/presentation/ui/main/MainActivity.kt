package com.example.smart_planner.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smart_planner.presentation.ui.news.NewsFragment
import com.example.smart_planner.presentation.ui.tasks.CreateTaskActivity
import com.example.smart_planner.presentation.ui.notes.NotesFragment
import com.example.smart_planner.R
import com.example.smart_planner.presentation.ui.tasks.TasksFragment
import com.example.smart_planner.data.repository.tasks.TaskRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var textTaskCount: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddTask = findViewById(R.id.button_add_task)
        textTaskCount = findViewById(R.id.text_task_count)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        fabAddTask.setOnClickListener {
            val intent = Intent(this, CreateTaskActivity::class.java)
            startActivity(intent)
        }

        // Обновленная настройка BottomNavigation с 3 элементами
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tasks -> {
                    loadFragment(TasksFragment())
                    true
                }
                R.id.nav_news -> {
                    loadFragment(NewsFragment())
                    true
                }
                R.id.nav_notes -> {
                    loadFragment(NotesFragment())
                    true
                }
                else -> false
            }
        }

        // Загружаем TasksFragment по умолчанию
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.nav_tasks
            loadFragment(TasksFragment())
        }

        updateTaskCount()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        updateTaskCount()
    }

    private fun updateTaskCount() {
        val taskCount = taskRepository.getAllTasks().size
        textTaskCount.text = if (taskCount == 0) {
            "Нажми + чтобы добавить задачу"
        } else {
            "Задач: $taskCount"
        }
    }
}