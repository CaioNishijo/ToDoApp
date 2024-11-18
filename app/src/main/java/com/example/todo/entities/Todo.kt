package com.example.todo.entities

data class Todo(
    val id: Int?,
    val name: String,
    val content: String,
    val creationDate: String?,
    val startHour: String,
    val categoryId: Int,
    var isFinished: Boolean
)