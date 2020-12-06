package com.rodrigmatrix.sigaaufc.internal.notification

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.view.main.MainActivity
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
    message: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notificações gerais"
        val description = "Notificações gerais do app"
        createNotificationChannel(GENERAL_CHANNEL_ID, name, description)
    }

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val builder = NotificationCompat.Builder(this, GENERAL_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_sigaa)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSound(soundUri)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        .build()
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(Random.nextInt(), builder)

}

fun Context.sendFileNotification(
    title: String,
    message: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notificações de arquivos"
        val description = "Notificações de novos arquivos das disciplinas"
        createNotificationChannel(FILES_CHANNEL_ID, name, description)
    }

    val intent = Intent(this, MainActivity::class.java)
    intent.putExtra("notificationAction", "downloadFile")
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

    val pendingIntent = PendingIntent.getActivity(this, Random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)


    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val builder = NotificationCompat.Builder(this, FILES_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_sigaa)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSound(soundUri)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        .addAction(NotificationCompat.Action(null, "Baixar arquivo", pendingIntent))
        .build()
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(Random.nextInt(), builder)

}


fun Context.sendNewsNotification(
    title: String,
    message: String,
    newsId: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notificações de notícias"
        val description = "Notificações de novas notícias das disciplinas"
        createNotificationChannel(NEWS_CHANNEL_ID, name, description)
    }

    val intent = Intent(this, MainActivity::class.java)
    intent.apply {
        putExtra("newsId", newsId)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val pendingIntent = PendingIntent.getActivity(this, Random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val builder = NotificationCompat.Builder(this, NEWS_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_sigaa)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSound(soundUri)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        .addAction(NotificationCompat.Action(null, "Ver notícia", pendingIntent))
        .build()
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(Random.nextInt(), builder)

}

fun Context.sendGradeNotification(
    title: String,
    message: String,
    gradeId: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notificações de notas"
        val description = "Notificações de novas notas das disciplinas"
        createNotificationChannel(GRADE_CHANNEL_ID, name, description)
    }

    val intent = Intent(this, MainActivity::class.java)
    intent.apply {
        putExtra("gradeId", gradeId)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val pendingIntent = PendingIntent.getActivity(this, Random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val builder = NotificationCompat.Builder(this, GRADE_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_sigaa)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSound(soundUri)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        .addAction(NotificationCompat.Action(null, "Ver nota", pendingIntent))
        .build()
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(Random.nextInt(), builder)

}

const val GENERAL_CHANNEL_ID = "general_channel_id"
const val CLASSES_CHANNEL_ID = "classes_channel_id"
const val NEWS_CHANNEL_ID = "news_channel_id"
const val FILES_CHANNEL_ID = "files_channel_id"
const val GRADE_CHANNEL_ID = "grades_channel_id"