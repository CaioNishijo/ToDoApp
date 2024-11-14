package com.example.todo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo.entities.Category
import com.example.todo.services.DbServices
import com.example.todo.services.convertTime
import com.example.todo.services.hourValidation
import com.example.todo.services.minutesValidation
import com.example.todo.services.sendToast
import com.example.todo.services.sendValidationMessages
import com.example.todo.services.setAlarmForNotification
import com.example.todo.services.textInputValidation
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

class AddTodoFormsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_todo_forms)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val categoriesList = findViewById<Spinner>(R.id.categoriesList)
        val submitBtn = findViewById<Button>(R.id.btn_submit)

        val db = DbServices(this)

        val categories = db.getCategorias()

        val adapter = ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesList.adapter = adapter

       submitBtn.setOnClickListener{
           onClickSubmit(db, this, categoriesList)
       }
    }

    fun onClickSubmit(db: DbServices, context: Context, categoriesList: Spinner){
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val contentInput = findViewById<EditText>(R.id.contentInput)
        val startHoursInput = findViewById<EditText>(R.id.hoursInput)
        val startMinutesInput = findViewById<EditText>(R.id.minutesInput)
        val durationHoursInput = findViewById<EditText>(R.id.durationHoursInput)
        val durationMinutesInput = findViewById<EditText>(R.id.durationMinutesInput)

        val selectedCategory = categoriesList.selectedItem as Category

        val todoName = nameInput.text.toString()
        val todoContent = contentInput.text.toString()
        var startHour: String? = null
        var duration: Int? = null
        val categoryId = selectedCategory.id
        val errorMessages = mutableListOf<String>()

        if (startHoursInput == null || !hourValidation(startHoursInput.text.toString())) {
            errorMessages.add("Hora de início inválida.")
        }
        if (durationHoursInput == null || !hourValidation(durationHoursInput.text.toString())) {
            errorMessages.add("Duração de hora inválida.")
        }
        if (startMinutesInput == null || !minutesValidation(startMinutesInput.text.toString())) {
            errorMessages.add("Minutos de início inválidos.")
        }
        if (durationMinutesInput == null || !minutesValidation(durationMinutesInput.text.toString())) {
            errorMessages.add("Minutos de duração inválidos.")
        }
        if(!textInputValidation(todoName)){
            errorMessages.add("Nome da tarefa inválida.")
        }

        sendValidationMessages(this, errorMessages)

        if(
            hourValidation(startHoursInput.text.toString()) &&
            hourValidation(durationHoursInput.text.toString()) &&
            minutesValidation(startMinutesInput.text.toString()) &&
            minutesValidation(durationMinutesInput.text.toString())
            ){
            startHour = convertTime(startHoursInput.text.toString(), startMinutesInput.text.toString())
            duration = (durationHoursInput.text.toString().toInt() * 60) + durationMinutesInput.text.toString().toInt()
        }

        if(startHour != null && textInputValidation(todoName) && duration != null) {
            db.createTodo(
                todoName,
                contentInput.text.toString(),
                startHour,
                duration,
                categoryId,
                false
            )

            val horaAtual = Calendar.getInstance()
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val horaInformada = Calendar.getInstance()
            horaInformada.time = sdf.parse(startHour)

            horaInformada.set(Calendar.YEAR, horaAtual.get(Calendar.YEAR))
            horaInformada.set(Calendar.MONTH, horaAtual.get(Calendar.MONTH))
            horaInformada.set(Calendar.DAY_OF_MONTH, horaAtual.get(Calendar.DAY_OF_MONTH))

            val intervalInMillis = horaInformada.timeInMillis - horaAtual.timeInMillis

            val triggerTime = System.currentTimeMillis() + intervalInMillis

            setAlarmForNotification(this, triggerTime, todoName)

            finish()
        }
    }
}