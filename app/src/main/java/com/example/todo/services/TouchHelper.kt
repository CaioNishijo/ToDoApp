package com.example.todo.services

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.adapters.CustomAdapterForTodos

class TouchHelper(
    private val adapter: CustomAdapterForTodos,
    private val db: DbServices
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.LEFT

        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val todo = adapter.getTodoItemAtPosition(position)
        val todoId = todo.id
        if(todoId == null) {
            return
        }

        db.deleteTodo(todoId)
    }
}