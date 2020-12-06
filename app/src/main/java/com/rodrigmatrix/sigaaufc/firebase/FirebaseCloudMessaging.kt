package com.rodrigmatrix.sigaaufc.firebase

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.view.main.MainActivity

//  Created by Rodrigo G. Resende on 2019-11-16.

class FirebaseCloudMessaging: FirebaseMessagingService() {

    private val channelId = "alerts_channel_id"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        handleOnlyDataPayload(remoteMessage.data, remoteMessage.notification?.title, remoteMessage.notification?.body)
    }

    private fun handleOnlyDataPayload(payload: MutableMap<String, String>, title: String?, body: String?) {
        if (payload.containsKey("link")) {
            openLink(payload["link"]!!, title, body)
            return
        }
        else {
            openNormalNotification(title, body)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Avisos e atualizações do app"
        val descriptionText = "Notificações sobre informações de desenvolvimento e atualizações do app"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(channelId, name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun openLink(link: String?, title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("link", link)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        showNotification(title, body, pendingIntent)
    }


    private fun showNotification(title: String?, body: String?, pendingIntent: PendingIntent) {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_sigaa)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder)
    }

    private fun openNormalNotification(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        showNotification(title, body, pendingIntent)
    }
}