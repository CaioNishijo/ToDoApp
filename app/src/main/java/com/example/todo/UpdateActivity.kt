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
import com.example.todo.databinding.ActivityUpdateBinding
import com.example.todo.services.DbServices
import com.example.todo.entities.Category
import com.example.todo.entities.Todo
import com.example.todo.services.cancelAlarm
import com.example.todo.services.convertTime
import com.example.todo.services.createNotificationsChannel
import com.example.todo.services.hourValidation
import com.example.todo.services.minutesValidation
import com.example.todo.services.sendDialog
import com.example.todo.services.sendValidationMessages
import com.example.todo.services.setAlarmForNotification
import com.example.todo.services.textInputValidation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // seleção de serviços e todo
        val db = DbServices(this)
        val intent = intent
        val list = db.getCategorias()
        val todoId = intent.getIntExtra("TODO_ID", -1)
        val todo = db.getTodoById(todoId)

        binding.timePicker.setIs24HourView(true)

        val adapter = ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            list
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoriesUpdateList.adapter = adapter

        if (todo != null) {
            // splitar o horário
            val hours = todo.startHour.split(":")
            val hour = hours[0]
            val minutes = hours[1]

            // popular os inputs
            binding.nameUpdateInput.setText(todo.name)
            binding.contentUpdateInput.setText(todo.content)
            binding.timePicker.hour = hour.toInt()
            binding.timePicker.minute = minutes.toInt()

            val selectedCategoryIndex = list.indexOfFirst { it.id == todo.categoryId }
            if (selectedCategoryIndex != -1) {
                binding.categoriesUpdateList.setSelection(selectedCategoryIndex)
            }

            binding.btnUpdate.setOnClickListener {
                // Pegar os valores atualizado
                val selectedCategory = binding.categoriesUpdateList.selectedItem as Category
                val todoName = binding.nameUpdateInput.text.toString()
                val updatedHour = binding.timePicker.hour
                val updatedMinutes = binding.timePicker.minute
                val startHour = convertTime(updatedHour.toString(), updatedMinutes.toString())
                val todoContent = binding.contentUpdateInput.text.toString()
                val categoryId = selectedCategory.id
                val errorMessages = mutableListOf<String>()
                val horaAtual = Calendar.getInstance()
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val horaInformada = Calendar.getInstance()
                horaInformada.time = sdf.parse(startHour)

                horaInformada.set(Calendar.YEAR, horaAtual.get(Calendar.YEAR))
                horaInformada.set(Calendar.MONTH, horaAtual.get(Calendar.MONTH))
                horaInformada.set(Calendar.DAY_OF_MONTH, horaAtual.get(Calendar.DAY_OF_MONTH))


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
                    cancelAlarm(this, todoId)

                    val intervalInMillis = horaInformada.timeInMillis - horaAtual.timeInMillis

                    val triggerTime = System.currentTimeMillis() + intervalInMillis

                    setAlarmForNotification(this, todoId.toLong(), triggerTime, todoName)

                    finish()
                    }
                }
            }
        }
    }
