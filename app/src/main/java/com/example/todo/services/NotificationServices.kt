package com.example.todo.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todo.MainActivity
import com.example.todo.R

fun createNotificationsChannel(context: Context){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val channelId = "channel_id"
        val channelName = "Canal de notificações"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Mandar mensagens avisando que uma tarefa está próxima do horário de início"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun sendNotification(context: Context, notificationTitle: String, notificationContent: String, action:
NotificationCompat.Action?){
    val channelId = "channel_id"
    val notificationId = 1

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.baseline_notifications_24)
        .setContentTitle(notificationTitle)
        .setContentText(notificationContent)
        .addAction(action)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
            return
        }
        notify(notificationId, builder.build())
    }
}

@SuppressLint("ScheduleExactAlarm")
fun setAlarmForNotification(context: Context, intervalInMillis: Long, content: String){
    val intent = Intent(context, NotificationReceiver::class.java)
    intent.putExtra("TITLE", content)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, intervalInMillis, pendingIntent)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun sendClickOnNotification(context: Context, title: String, content: String){
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val action = NotificationCompat.Action.Builder(
        R.drawable.baseline_open_in_full_24, "Abrir tarefas", pendingIntent
    ).build()

    sendNotification(context, title, content, action)
}

fun sendToast(message: String, context: Context){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun sendDialog(context: Context, message: String, title: String){
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
        .setMessage(message)
        .setPositiveButton("Entendido", null)
        .create()
        .show()
}

fun isHeadUpAllowed(context: Context, channelId: String): Boolean{
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel(channelId)
        return channel?.importance == NotificationManager.IMPORTANCE_HIGH
    }
    return true
}

@RequiresApi(Build.VERSION_CODES.O)
fun redirectToNotificationsSettings(context: Context){
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}

@RequiresApi(Build.VERSION_CODES.O)
fun createAlertDialogForNotificationSettings(context: Context){
    AlertDialog.Builder(context)
        .setTitle("Permitir notificações")
        .setMessage("Para garantir que as notificações estejam ativadas, habilite elas")
        .setPositiveButton("Ir para configurações") { _, _ ->
            redirectToNotificationsSettings(context)
        }
        .setNegativeButton("Cancelar", null)
        .show()
}