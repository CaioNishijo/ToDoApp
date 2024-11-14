package com.example.todo.services

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun convertTime(startHour: String, startMinutes: String): String {
    val hour = startHour.toInt()
    val minutes = startMinutes.toInt()

    return String.format("%02d:%02d", hour, minutes)
}