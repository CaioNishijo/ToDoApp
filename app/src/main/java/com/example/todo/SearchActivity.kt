package com.example.todo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
        var haveInput: Boolean
        val clearBtn = findViewById<ImageView>(R.id.clearBtn)
        val categoriesSpinner = findViewById<Spinner>(R.id.categoriesSpinner)
        val isFinishedSpinner = findViewById<Spinner>(R.id.isFinishedSpinner)
        val isFinishedSpinnerItens = listOf("---", "Finalizadas", "Não finalizadas")
        val rv = findViewById<RecyclerView>(R.id.todoList)
        val categoriesList = db.getCategorias()
        val mutableCategoriesList = categoriesList.toMutableList()
        val searchInput = findViewById<EditText>(R.id.searchInput)
        val noOption = Category(
            id = 999,
            name = "---"
        )
        mutableCategoriesList.add(0, noOption)

        val isFinishedAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            isFinishedSpinnerItens
        )

        isFinishedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        isFinishedSpinner.adapter = isFinishedAdapter

        val categoryAdapter = ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            mutableCategoriesList
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesSpinner.adapter = categoryAdapter

        db.loadTodos(rv, null)

        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val name = searchInput.text.toString()
                val selectedCategory = categoriesSpinner.selectedItem as Category
                val categoryId = selectedCategory.id
                val selectedIsFinished = isFinishedSpinner.selectedItem as String

                val list = db.filterService(name, categoryId.toString(), selectedIsFinished)
                db.loadTodos(rv, list)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        isFinishedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val name = searchInput.text.toString()
                val selectedCategory = categoriesSpinner.selectedItem as Category
                val categoryId = selectedCategory.id
                val selectedIsFinished = isFinishedSpinner.selectedItem as String

                val list = db.filterService(name, categoryId.toString(), selectedIsFinished)
                db.loadTodos(rv, list)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                haveInput = true
                updateClearBtn(haveInput, clearBtn, searchInput)
                val name = searchInput.text.toString()
                val selectedCategory = categoriesSpinner.selectedItem as Category
                val categoryId = selectedCategory.id
                val selectedIsFinished = isFinishedSpinner.selectedItem as String

                val list = db.filterService(name, categoryId.toString(), selectedIsFinished)
                db.loadTodos(rv, list)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        clearBtn.setOnClickListener {
            haveInput = false
            searchInput.setText("")
            updateClearBtn(haveInput, clearBtn, searchInput)
        }
    }

    fun updateClearBtn(haveInput: Boolean, clearBtn: ImageView, searchInput: EditText){
        if(haveInput && searchInput.text.toString().isNotEmpty()){
            clearBtn.visibility = View.VISIBLE
            val layoutParams = searchInput.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.matchConstraintPercentWidth = 0.8f
            searchInput.layoutParams = layoutParams
        } else{
            clearBtn.visibility = View.GONE
            val layoutParams = searchInput.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.matchConstraintPercentWidth = 0.9f
            searchInput.layoutParams = layoutParams
        }
    }
}