package com.example.smart_planner.presentation.ui.tasks

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_planner.R
import com.example.smart_planner.data.models.tasks.TaskEntity
import com.example.smart_planner.domain.models.tasks.Priority
import com.example.smart_planner.domain.models.tasks.Task
import com.example.smart_planner.data.repository.tasks.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateTaskActivity : AppCompatActivity() {

    private var selectedDate: Calendar? = null
    private val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())

    @Inject
    lateinit var taskRepository: TaskRepository

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

        setupKeyboardListeners(rootLayout, editTextTitle, editTextDescription)
        setupDatePicker(buttonPickDate, editTextDate)
        setupSaveButton(buttonSave, editTextTitle, editTextDescription, radioGroupPriority, checkBoxFlag)
        setupCancelButton(buttonCancel)
    }

    private fun setupKeyboardListeners(rootLayout: View, vararg editTexts: EditText) {
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }

        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    hideKeyboard()
                }
            }
        }
    }

    private fun setupDatePicker(buttonPickDate: Button, editTextDate: EditText) {
        val clickListener = View.OnClickListener { showDateTimePicker(editTextDate) }
        buttonPickDate.setOnClickListener(clickListener)
        editTextDate.setOnClickListener(clickListener)
    }

    private fun setupSaveButton(
        buttonSave: Button,
        editTextTitle: EditText,
        editTextDescription: EditText,
        radioGroupPriority: RadioGroup,
        checkBoxFlag: CheckBox
    ) {
        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = createTaskFromInput(
                title,
                editTextDescription.text.toString().trim(),
                radioGroupPriority.checkedRadioButtonId,
                checkBoxFlag.isChecked
            )

            taskRepository.addTask(TaskEntity.fromDomain(task))

            Toast.makeText(this, "Задача сохранена: ${task.title}", Toast.LENGTH_SHORT).show()

            hideKeyboard()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun createTaskFromInput(
        title: String,
        description: String,
        checkedRadioButtonId: Int,
        hasFlag: Boolean
    ): Task {
        val priority = when (checkedRadioButtonId) {
            R.id.radio_low -> Priority.LOW
            R.id.radio_high -> Priority.HIGH
            else -> Priority.MEDIUM
        }

        return Task(
            id = System.currentTimeMillis(),
            title = title,
            description = description,
            priority = priority,
            dueDate = selectedDate?.time,
            hasFlag = hasFlag,
            isCompleted = false,
            createdAt = Date()
        )
    }

    private fun setupCancelButton(buttonCancel: Button) {
        buttonCancel.setOnClickListener {
            hideKeyboard()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun showDateTimePicker(editTextDate: EditText) {
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
                        selectedDate?.time?.let {
                            editTextDate.setText(dateFormat.format(it))
                        }
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