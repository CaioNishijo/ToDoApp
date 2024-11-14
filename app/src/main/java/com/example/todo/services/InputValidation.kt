package com.example.todo.services

import android.content.Context

fun textInputValidation(text: String?): Boolean{
    return !(text.isNullOrBlank() || text.isEmpty())
}

fun hourValidation(hour: String?): Boolean{
    if(hour.isNullOrBlank()) return false
    val hourInt = hour.toInt()
    return !(hourInt < 0 || hourInt > 24 )
}

fun minutesValidation(minutes: String?): Boolean{
    if(minutes.isNullOrBlank()) return false
    val minutesInt = minutes.toInt()
    return !(minutesInt < 0 || minutesInt > 60 )
}

fun sendValidationMessages(context: Context, errorMessages: List<String>){
    val errorMessage = errorMessages.joinToString("\n")

    if(errorMessages.isNotEmpty()){
        sendDialog(context, errorMessage, "Os campos devem ser válidos")
    }
}