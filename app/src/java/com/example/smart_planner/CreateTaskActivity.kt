package com.example.smart_planner

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_planner.data.Priority
import com.example.smart_planner.data.Task
import java.text.SimpleDateFormat
import java.util.*

class CreateTaskActivity : AppCompatActivity() {

    private var selectedDate: Calendar? = null
    private val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)



        val editTextTitle = findViewById<EditText>(R.id.edit_text_title)
        val editTextDescription = findViewById<EditText>(R.id.edit_text_description)
        val radioGroupPriority = findViewById<RadioGroup>(R.id.radio_group_priority)
        val checkBoxFlag = findViewById<CheckBox>(R.id.check_box_flag)
        val editTextDate = findViewById<EditText>(R.id.edit_text_date)
        val buttonPickDate = findViewById<Button>(R.id.button_pick_date)
        val buttonSave = findViewById<Button>(R.id.button_save)
        val buttonCancel = findViewById<Button>(R.id.button_cancel)

        val rootLayout = findViewById<View>(android.R.id.content)


        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }


        editTextTitle.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
            }
        }

        editTextDescription.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
            }
        }

        buttonPickDate.setOnClickListener {
            showDateTimePicker()
        }

        editTextDate.setOnClickListener {
            showDateTimePicker()
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val description = editTextDescription.text.toString().trim()

            val priority = when (radioGroupPriority.checkedRadioButtonId) {
                R.id.radio_low -> Priority.LOW
                R.id.radio_high -> Priority.HIGH
                else -> Priority.MEDIUM
            }

            val hasFlag = checkBoxFlag.isChecked


            val task = Task(
                title = title,
                description = description,
                priority = priority,
                dueDate = selectedDate?.time,
                hasFlag = hasFlag
            )


            TaskRepository.addTask(task)

            Toast.makeText(this, "Задача сохранена: ${task.title}", Toast.LENGTH_SHORT).show()

            setResult(Activity.RESULT_OK)

            hideKeyboard()

            finish()
        }

        buttonCancel.setOnClickListener {

            hideKeyboard()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    // не получилось реализовать..
    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun showDateTimePicker() {
        hideKeyboard()

        val currentDate = Calendar.getInstance()


        DatePickerDialog(
            this,
            { _, year, month, day ->

                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        selectedDate = Calendar.getInstance().apply {
                            set(year, month, day, hour, minute)
                        }

                        findViewById<EditText>(R.id.edit_text_date).setText(
                            dateFormat.format(selectedDate!!.time)
                        )
                    },
                    currentDate.get(Calendar.HOUR_OF_DAY),
                    currentDate.get(Calendar.MINUTE),
                    true
                ).show()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        hideKeyboard()
        return super.onTouchEvent(event)
    }
}
