package com.example.myapplication

import android.app.AlarmManager
import android.app.AlarmManager.RTC
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.example.myapplication.components.AlarmReceiver
import java.time.Instant
import java.util.Calendar

class AndroidAlarmScheduler(
    private val context: Context
) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    @RequiresApi(Build.VERSION_CODES.O)
    fun schedule(calendar: Calendar, title:String, intro:String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("intro", intro)
        }
        println(Instant.now().toEpochMilli())
        println(calendar.timeInMillis)
        alarmManager.setExactAndAllowWhileIdle(
            RTC,
            calendar.timeInMillis - 600000,//ahead of 10 minutes
            PendingIntent.getBroadcast(
                context,
                (calendar.timeInMillis + title.hashCode() + intro.hashCode()).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    fun cancel(calendar: Calendar, title:String, intro:String) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                (calendar.timeInMillis + title.hashCode() + intro.hashCode()).toInt(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun clear() {
       alarmManager.cancelAll()
    }

}