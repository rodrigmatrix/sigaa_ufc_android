package com.rodrigmatrix.sigaaufc.internal.notification

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rodrigmatrix.sigaaufc.R
import kotlin.random.Random


@TargetApi(Build.VERSION_CODES.O)
private fun Context.createNotificationChannel(channelId: String, name: String, description: String) {
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val mChannel = NotificationChannel(channelId, name, importance)
    mChannel.description = description
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(mChannel)
}

fun Context.sendNotification(
    title: String,
    message: String,
    channelId: String = CLASSES_CHANNEL_ID
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notificações de disciplinas"
        val description = "Notificações de notas, notícias e arquivos das disciplinas"
        createNotificationChannel(CLASSES_CHANNEL_ID, name, description)
    }
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_stat_sigaa)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSound(soundUri)
        .build()
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(Random.nextInt(), builder)

}



const val CLASSES_CHANNEL_ID = "classes_channel"