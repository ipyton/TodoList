package com.example.myapplication.components

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings.Global.getString
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.MainApplication.Companion.context
import com.example.myapplication.R
import com.google.android.material.snackbar.Snackbar
import java.time.Instant

val manager = context.applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
@RequiresApi(Build.VERSION_CODES.O)
fun sendNotifications(title:String, introduction:String, context: Context) {

    var intent1 = Intent(context, MainActivity::class.java)
    var pendingIntent = PendingIntent.getActivity(context, 200, intent1,
        PendingIntent.FLAG_IMMUTABLE)

    val builder = context?.let { NotificationCompat.Builder(it, "todolist")
        .setContentTitle(title).setContentText(introduction)
        .setPriority(2).setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher_1).setAutoCancel(true)
        .setDefaults(NotificationCompat.DEFAULT_ALL) }

    manager.notify(((introduction+title).hashCode() + Instant.now().epochSecond).toInt(), builder?.build())


}
class AlarmReceiver:BroadcastReceiver(){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context1: Context?, intent: Intent?) {
        var stringExtra = intent?.getStringExtra("title")
        var introductionExtra = intent?.getStringExtra("intro")


        sendNotifications(stringExtra.toString(), introductionExtra.toString(), context.applicationContext)




    }
}