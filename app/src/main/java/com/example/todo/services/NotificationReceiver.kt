package com.example.todo.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class NotificationReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {

        val title = intent?.getStringExtra("TITLE") ?: "Notificação"
        val requestCode = intent?.getLongExtra("REQUESTCODE", -1)

        sendClickOnNotification(context!!,title,"Você tem uma tarefa para iniciar agora", requestCode!!.toLong())
    }
}