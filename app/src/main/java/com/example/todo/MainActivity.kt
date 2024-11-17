package com.example.todo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.services.DbServices
import com.example.todo.services.closeFabMenu
import com.example.todo.services.createNotificationsChannel
import com.example.todo.services.openFabMenu
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
        var isOpen = false
        val db = DbServices(this)
        val addTodoBtn = findViewById<FloatingActionButton>(R.id.add_todo)
        val clearBtn = findViewById<FloatingActionButton>(R.id.clear_btn)
        val rv = findViewById<RecyclerView>(R.id.todo_list)
        val openSearchActBtn = findViewById<FloatingActionButton>(R.id.openSearchActBtn)
        val menuBtn = findViewById<FloatingActionButton>(R.id.menu_button)
        val btnLayout = findViewById<LinearLayout>(R.id.buttonLayout)
        btnLayout.bringToFront()

        menuBtn.setOnClickListener {
            if(isOpen){
                isOpen = closeFabMenu(btnLayout)
                menuBtn.setImageResource(R.drawable.options_lines_svgrepo_com)
            } else {
                isOpen = openFabMenu(btnLayout)
                menuBtn.setImageResource(R.drawable.close_svgrepo_com)
            }
        }

        addTodoBtn.setOnClickListener {
            intent = Intent(this, AddTodoFormsActivity::class.java)
            startActivity(intent)

            isOpen = closeFabMenu(btnLayout)
        }

        clearBtn.setOnClickListener {
            isOpen = closeFabMenu(btnLayout)
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Alerta")
            dialog.setMessage("Você realmente deseja limpar todas as tarefas?")
            dialog.setPositiveButton("Sim") { _, _ ->
                db.clearAll()

                db.loadTodos(rv, null)
            }
            dialog.setNegativeButton("Cancelar", null)
            dialog.show()
        }

        openSearchActBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)

            isOpen = closeFabMenu(btnLayout)
        }
    }

    override fun onResume() {
        super.onResume()
        val db = DbServices(this)
        val rv = findViewById<RecyclerView>(R.id.todo_list)

        db.loadTodos(rv, null)
    }
}