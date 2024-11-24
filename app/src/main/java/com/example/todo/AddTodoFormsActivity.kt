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
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo.databinding.ActivityAddTodoFormsBinding
import com.example.todo.entities.Category
import com.example.todo.services.DbServices
import com.example.todo.services.convertTime
import com.example.todo.services.hourValidation
import com.example.todo.services.minutesValidation
import com.example.todo.services.sendToast
import com.example.todo.services.sendValidationMessages
import com.example.todo.services.setAlarmForNotification
import com.example.todo.services.textInputValidation
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

class AddTodoFormsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTodoFormsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoFormsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.timePicker.setIs24HourView(true)

        val db = DbServices(this)

        val categories = db.getCategorias()

        val adapter = ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoriesList.adapter = adapter

       binding.btnSubmit.setOnClickListener{
           onClickSubmit(db, this, binding.categoriesList)
       }
    }

    fun onClickSubmit(db: DbServices, context: Context, categoriesList: Spinner){
        val selectedCategory = categoriesList.selectedItem as Category

        val todoName = binding.editTextName.text.toString()
        val todoContent = binding.editTextContent.text.toString()
        val selectedHour = binding.timePicker.hour
        val selectedMinute = binding.timePicker.minute
        val startHour = convertTime(selectedHour.toString(), selectedMinute.toString())
        val categoryId = selectedCategory.id
        val errorMessages = mutableListOf<String>()

        val horaAtual = Calendar.getInstance()
        val diaAtual = horaAtual.get(Calendar.DAY_OF_MONTH)
        val mesAtual = horaAtual.get(Calendar.MONTH) + 1
        val anoAtual = horaAtual.get(Calendar.YEAR)
        val date = "$anoAtual-$mesAtual-$diaAtual"
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val horaInformada = Calendar.getInstance()
        horaInformada.time = sdf.parse(startHour)

        horaInformada.set(Calendar.YEAR, horaAtual.get(Calendar.YEAR))
        horaInformada.set(Calendar.MONTH, horaAtual.get(Calendar.MONTH))
        horaInformada.set(Calendar.DAY_OF_MONTH, horaAtual.get(Calendar.DAY_OF_MONTH))

        if(!textInputValidation(todoName)){
            errorMessages.add("Nome da tarefa inválida.")
        }
        if(db.verifyIfAlreadyHaveAScheduling(date, startHour)){
            errorMessages.add("Você já tem uma tarefa marcada para este horário hoje")
        }

        sendValidationMessages(this, errorMessages)

        if(textInputValidation(todoName) && !db.verifyIfAlreadyHaveAScheduling(date, startHour)) {
            val id = db.createTodo(
                todoName,
                binding.editTextContent.text.toString(),
                startHour,
                categoryId,
                false
            )

            val intervalInMillis = horaInformada.timeInMillis - horaAtual.timeInMillis

            val triggerTime = System.currentTimeMillis() + intervalInMillis

            setAlarmForNotification(this, id, triggerTime, todoName)

            finish()
        }
    }
}