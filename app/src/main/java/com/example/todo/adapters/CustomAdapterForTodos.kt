package com.example.todo.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.DeleteActivity
import com.example.todo.R
import com.example.todo.UpdateActivity
import com.example.todo.entities.Todo
import com.example.todo.services.DbServices
import com.example.todo.services.TouchHelper
import org.w3c.dom.Text

class CustomAdapterForTodos(
    val context: Context,
    var todos: List<Todo>,
    private val db: DbServices
) : RecyclerView.Adapter<CustomAdapterForTodos.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.todo_row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val todo = todos[position]
        holder.bind(todo)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("TODO_ID", todo.id)
            context.startActivity(intent)
        }
        holder.itemView.findViewById<ImageView>(R.id.delete_btn).setOnClickListener {
            val intent = Intent(context, DeleteActivity::class.java)
            intent.putExtra("TODO_ID", todo.id)
            context.startActivity(intent)
        }
        holder.itemView.findViewById<Button>(R.id.btn_start).setOnClickListener {
            val intent = Intent(AlarmClock.ACTION_SHOW_TIMERS)
            try{
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException){
                Toast.makeText(context, "Erro ao abrir temporizador", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateIsFinish(id: Int, isFinished: Boolean){
        db.updateIsFinish(id, isFinished)
    }

    fun updateItemLayout(isFinished: Boolean, checkBtn: ImageView, todoName: TextView, startBtn: Button){
        if(isFinished){
            checkBtn.setColorFilter(ContextCompat.getColor(context, R.color.black))
            checkBtn.visibility = View.INVISIBLE
            startBtn.isEnabled = false
            todoName.paintFlags = todoName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else{
            checkBtn.setColorFilter(ContextCompat.getColor(context, R.color.green))
            checkBtn.visibility = View.VISIBLE
            startBtn.isEnabled = true
            todoName.paintFlags = todoName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    fun getTodoItemAtPosition(position: Int): Todo {
        return todos[position]
    }

    inner class MyViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        private val todoName: TextView = itemView.findViewById(R.id.todo_name)
        private val todoCategory: TextView = itemView.findViewById(R.id.todo_category)
        private val startHour:TextView = itemView.findViewById(R.id.todo_start_time)
        private val checkBtn = itemView.findViewById<ImageView>(R.id.check_btn)
        private val startBtn = itemView.findViewById<Button>(R.id.btn_start)


        fun bind(todo: Todo) {
            val categoryName = db.getCategoriaById(todo.categoryId)

            checkBtn.setOnClickListener {
                val todoIsFinishedVal = !todo.isFinished
                updateIsFinish(todo.id!!, todoIsFinishedVal)
                todo.isFinished = todoIsFinishedVal

                updateItemLayout(todoIsFinishedVal, checkBtn, todoName, startBtn)
            }

            todoName.text = todo.name
            todoCategory.text = categoryName!!.name
            startHour.text = todo.startHour

            updateItemLayout(todo.isFinished, checkBtn, todoName, startBtn)
        }
    }


}