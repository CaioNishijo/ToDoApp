package com.example.todo

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.services.DbServices
import com.example.todo.services.closeFabMenu
import com.example.todo.services.createNotificationsChannel
import com.example.todo.services.openFabMenu
import android.Manifest
import android.view.View
import android.widget.TextView
import com.example.todo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val REQUEST_CODE = 1

        createNotificationsChannel(this)
        var isOpen = false
        val db = DbServices(this)
        binding.layoutButtons.bringToFront()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE
            )
        }

        binding.menuButton.setOnClickListener {
            if(isOpen){
                isOpen = closeFabMenu(binding.layoutButtons)
                binding.menuButton.setImageResource(R.drawable.options_lines_svgrepo_com)
            } else {
                isOpen = openFabMenu(binding.layoutButtons)
                binding.menuButton.setImageResource(R.drawable.close_svgrepo_com)
            }
        }

        binding.btnOpenAddForms.setOnClickListener {
            intent = Intent(this, AddTodoFormsActivity::class.java)
            startActivity(intent)

            isOpen = closeFabMenu(binding.layoutButtons)
            binding.menuButton.setImageResource(R.drawable.options_lines_svgrepo_com)
        }

        binding.btnClearAll.setOnClickListener {
            isOpen = closeFabMenu(binding.layoutButtons)
            binding.menuButton.setImageResource(R.drawable.options_lines_svgrepo_com)
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Alerta")
            dialog.setMessage("Você realmente deseja limpar todas as tarefas?")
            dialog.setPositiveButton("Sim") { _, _ ->
                db.clearAll()

                val noListText = findViewById<TextView>(R.id.empty_title)
                val haveList = db.loadTodos(binding.todoList, null)

                updateEmptyTitle(noListText, haveList)
            }
            dialog.setNegativeButton("Cancelar", null)
            dialog.show()
        }

        binding.btnOpenSearchActivity.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)

            isOpen = closeFabMenu(binding.layoutButtons)
            binding.menuButton.setImageResource(R.drawable.options_lines_svgrepo_com)
        }
    }

    override fun onResume() {
        super.onResume()
        val noListText = findViewById<TextView>(R.id.empty_title)
        val db = DbServices(this)
        val rv = findViewById<RecyclerView>(R.id.todo_list)

        val haveList = db.loadTodos(rv, null)
        updateEmptyTitle(noListText, haveList)
    }

    fun updateEmptyTitle(noListText: TextView, haveList: Boolean){
        if(!haveList){
            noListText.visibility = View.VISIBLE
        } else{
            noListText.visibility = View.GONE
        }
    }
}