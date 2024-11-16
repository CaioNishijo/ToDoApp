package com.example.todo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.entities.Category
import com.example.todo.services.DbServices

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DbServices(this)
        val categoriesSpinner = findViewById<Spinner>(R.id.categoriesSpinner)
        val rv = findViewById<RecyclerView>(R.id.todoList)
        val filterBtn = findViewById<ImageView>(R.id.filterBtn)
        val categoriesList = db.getCategorias()
        val searchInput = findViewById<EditText>(R.id.searchInput)
        val searchBtn = findViewById<Button>(R.id.searchBtn)

        val adapter = ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            categoriesList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesSpinner.adapter = adapter

        filterBtn.setOnClickListener {
            val selectedCategory = categoriesSpinner.selectedItem as Category
            val categoryId = selectedCategory.id
            val list = db.getTodoByCategoryId(categoryId = categoryId)

            db.loadTodos(rv, list)
        }

        searchBtn.setOnClickListener {
            val input = searchInput.text.toString()

            val list = db.findTodoByName(input)

            db.loadTodos(rv, list)
        }
    }
}