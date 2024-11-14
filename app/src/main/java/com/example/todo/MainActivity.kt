package com.example.todo

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.adapters.CustomAdapterForTodos
import com.example.todo.services.DbServices
import com.example.todo.services.TouchHelper
import com.example.todo.services.createNotificationsChannel
import com.example.todo.services.sendNotification
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createNotificationsChannel(this)
        val db = DbServices(this)
        val addTodoBtn = findViewById<FloatingActionButton>(R.id.add_todo)
        val clearBtn = findViewById<Button>(R.id.clear_btn)
        val rv = findViewById<RecyclerView>(R.id.todo_list)

        addTodoBtn.setOnClickListener {
            intent = Intent(this, AddTodoFormsActivity::class.java)
            startActivity(intent)
        }

        clearBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Alerta")
            dialog.setMessage("Você realmente deseja limpar todas as tarefas?")
            dialog.setPositiveButton("Sim") { _, _ ->
                db.clearAll()

                db.loadTodos(rv)
            }
            dialog.setNegativeButton("Cancelar", null)
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        val db = DbServices(this)
        val rv = findViewById<RecyclerView>(R.id.todo_list)

        db.loadTodos(rv)
    }
}