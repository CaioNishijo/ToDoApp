package com.example.todo.entities

data class Category(
    val id: Int,
    val name: String
){
    override fun toString(): String {
        return name
    }
}