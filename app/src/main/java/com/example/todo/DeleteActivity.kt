package com.example.todo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo.databinding.ActivityDeleteBinding
import com.example.todo.services.DbServices
import com.example.todo.services.cancelAlarm
import org.w3c.dom.Text

class DeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intent = intent
        val todoId = intent.getIntExtra("TODO_ID", -1)
        val db = DbServices(this)

        val todo = db.getTodoById(todoId)

        val category = db.getCategoriaById(todo!!.categoryId)

        binding.taskName.text = todo?.name
        binding.taskContent.text = todo?.content
        binding.taskStartHour.text = todo?.startHour
        binding.categoria.text = category?.name

        binding.confirmDeleteButton.setOnClickListener {
            db.deleteTodo(todoId)
            cancelAlarm(this, todoId)
            finish()
        }
    }
}