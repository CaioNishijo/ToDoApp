package com.example.todo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo.services.DbServices
import org.w3c.dom.Text

class DeleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete)
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

        val taskName = findViewById<TextView>(R.id.task_name)
        val taskContent = findViewById<TextView>(R.id.task_content)
        val taskStartHour = findViewById<TextView>(R.id.task_start_hour)
        val categoriaName = findViewById<TextView>(R.id.categoria)
        val confirmDeleteButton = findViewById<Button>(R.id.confirm_delete_button)

        taskName.text = todo?.name
        taskContent.text = todo?.content
        taskStartHour.text = todo?.startHour
        categoriaName.text = category?.name

        confirmDeleteButton.setOnClickListener {
            db.deleteTodo(todoId)
            finish()
        }
    }
}