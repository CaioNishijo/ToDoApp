package com.example.todo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo.services.DbServices
import com.example.todo.entities.Category
import com.example.todo.entities.Todo
import com.example.todo.services.convertTime
import com.example.todo.services.createNotificationsChannel
import com.example.todo.services.hourValidation
import com.example.todo.services.minutesValidation
import com.example.todo.services.sendDialog
import com.example.todo.services.sendValidationMessages
import com.example.todo.services.textInputValidation

class UpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // seleção de serviços e todo
        val db = DbServices(this)
        val intent = intent
        val list = db.getCategorias()
        val categoriesDisplay = findViewById<Spinner>(R.id.categoriesUpdateList)
        val todoId = intent.getIntExtra("TODO_ID", -1)
        val todo = db.getTodoById(todoId)
        val timePicker = findViewById<TimePicker>(R.id.timePicker)

        timePicker.setIs24HourView(true)

        // seleção das views
        val nameUpdateInput = findViewById<EditText>(R.id.nameUpdateInput)
        val contentUpdateInput = findViewById<EditText>(R.id.contentUpdateInput)
        val btn_update = findViewById<Button>(R.id.btn_update)

        val adapter = ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            list
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesDisplay.adapter = adapter

        if (todo != null) {
            // splitar o horário
            val hours = todo.startHour.split(":")
            val hour = hours[0]
            val minutes = hours[1]

            // popular os inputs
            nameUpdateInput.setText(todo.name)
            contentUpdateInput.setText(todo.content)
            timePicker.hour = hour.toInt()
            timePicker.minute = minutes.toInt()

            val selectedCategoryIndex = list.indexOfFirst { it.id == todo.categoryId }
            if (selectedCategoryIndex != -1) {
                categoriesDisplay.setSelection(selectedCategoryIndex)
            }

            btn_update.setOnClickListener {
                // Pegar os valores atualizado
                val selectedCategory = categoriesDisplay.selectedItem as Category
                val todoName = nameUpdateInput.text.toString()
                val updatedHour = timePicker.hour
                val updatedMinutes = timePicker.minute
                val startHour = convertTime(updatedHour.toString(), updatedMinutes.toString())
                val todoContent = contentUpdateInput.text.toString()
                val categoryId = selectedCategory.id
                val errorMessages = mutableListOf<String>()

                if(!textInputValidation(todoName)){
                    errorMessages.add("Nome da tarefa inválida.")
                }

                sendValidationMessages(this, errorMessages)

                if(textInputValidation(todoName)){
                    val updatedTodo = Todo(
                        id = todoId,
                        name = todoName,
                        content = todoContent,
                        categoryId = categoryId,
                        startHour = startHour,
                        isFinished = todo.isFinished,
                        creationDate = todo.creationDate
                    )

                    db.updateTodo(todoId, updatedTodo)
                    finish()
                    }
                }
            }
        }
    }
