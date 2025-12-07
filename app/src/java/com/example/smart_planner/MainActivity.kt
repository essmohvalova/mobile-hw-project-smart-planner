package com.example.smart_planner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var textTaskCount: TextView
    private lateinit var bottomNavigation: BottomNavigationView

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

        // Настройка BottomNavigation
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tasks -> {
                    loadFragment(TasksFragment())
                    true
                }
                R.id.nav_notes -> {
                    loadFragment(NotesFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
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
        val taskCount = TaskRepository.getTasks().size
        textTaskCount.text = if (taskCount == 0) {
            "Нажми + чтобы добавить задачу"
        } else {
            "Задач: $taskCount"
        }
    }
}