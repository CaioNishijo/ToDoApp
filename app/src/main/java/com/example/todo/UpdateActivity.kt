package com.example.todo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
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

        // seleção das views
        val nameUpdateInput = findViewById<EditText>(R.id.nameUpdateInput)
        val contentUpdateInput = findViewById<EditText>(R.id.contentUpdateInput)
        val hoursUpdateInput = findViewById<EditText>(R.id.hoursUpdateInput)
        val minutesUpdateInput = findViewById<EditText>(R.id.minutesUpdateInput)
        val durationHoursUpdateInput = findViewById<EditText>(R.id.durationHoursUpdateInput)
        val durationMinutesUpdateInput = findViewById<EditText>(R.id.durationMinutesUpdateInput)
        val categoryIdVal = findViewById<TextView>(R.id.categoryidval)
        val btn_update = findViewById<Button>(R.id.btn_update)

        val adapter = ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            list
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesDisplay.adapter = adapter

        if (todo != null) {
            // splitar horários
            val hours = todo!!.startHour.split(":")
            val hour = hours[0]
            val minutes = hours[1]

            // converter a duração
            val durationHour = todo.duration.toInt() / 60
            val durationMinutes = todo.duration.toInt() % 60

            // popular os inputs
            nameUpdateInput.setText(todo.name)
            contentUpdateInput.setText(todo.content)
            hoursUpdateInput.setText(hour)
            minutesUpdateInput.setText(minutes)
            durationHoursUpdateInput.setText(durationHour.toString())
            durationMinutesUpdateInput.setText(durationMinutes.toString())
            categoryIdVal.text = todo.categoryId.toString()

            val selectedCategoryIndex = list.indexOfFirst { it.id == todo.categoryId }
            if (selectedCategoryIndex != -1) {
                categoriesDisplay.setSelection(selectedCategoryIndex)
            }

            btn_update.setOnClickListener {
                // Pegar os valores atualizado
                val selectedCategory = categoriesDisplay.selectedItem as Category
                val todoName = nameUpdateInput.text.toString()
                val todoContent = contentUpdateInput.text.toString()
                val categoryId = selectedCategory.id
                val errorMessages = mutableListOf<String>()
                val startHourInputValidation = hoursUpdateInput.text.toString()
                val durationHourInputValidation = durationHoursUpdateInput.text.toString()
                val startMinutesInputValidation = durationMinutesUpdateInput.text.toString()
                val durationMinutesInputValidation = durationMinutesUpdateInput.text.toString()

                if (!hourValidation(startHourInputValidation)) {
                    errorMessages.add("Hora de início inválida.")
                }
                if (!hourValidation(durationHourInputValidation)) {
                    errorMessages.add("Duração de hora inválida.")
                }
                if (!minutesValidation(startMinutesInputValidation)) {
                    errorMessages.add("Minutos de início inválidos.")
                }
                if (!minutesValidation(durationMinutesInputValidation)) {
                    errorMessages.add("Minutos de duração inválidos.")
                }
                if(!textInputValidation(todoName)){
                    errorMessages.add("Nome da tarefa inválida.")
                }

                sendValidationMessages(this, errorMessages)

                if(hourValidation(startHourInputValidation) && hourValidation(durationHourInputValidation) && minutesValidation(startMinutesInputValidation) && minutesValidation(durationMinutesInputValidation)
                    && textInputValidation(todoName)){
                    val startHour = convertTime(startHourInputValidation, startMinutesInputValidation)
                    val duration = (durationHourInputValidation.toInt() * 60) + durationMinutesInputValidation.toInt()

                    // Validar os textos
                    val updatedTodo = Todo(
                        id = todoId,
                        name = todoName,
                        content = todoContent,
                        categoryId = categoryId,
                        duration = duration.toString(),
                        isFinished = todo.isFinished,
                        startHour = startHour,
                        creationDate = todo.creationDate
                    )

                    db.updateTodo(todoId, updatedTodo)
                    finish()
                    }
                }


            }
        }
    }
