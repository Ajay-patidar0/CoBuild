package com.example.cobuild.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.cobuild.MainActivity
import com.example.cobuild.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save token to Firestore whenever it refreshes
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update("fcmToken", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title  = message.notification?.title ?: "CoBuild"
        val body   = message.notification?.body  ?: ""
        val screen = message.data["screen"]      ?: "notifications"

        showNotification(title, body, screen)
    }

    private fun showNotification(title: String, body: String, screen: String) {
        val channelId = "cobuild_requests"
        val manager   = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (required on Android 8+)
        val channel = NotificationChannel(
            channelId,
            "CoBuild Requests",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Join requests and invites" }
        manager.createNotificationChannel(channel)

        // Intent opens MainActivity and passes screen=notifications
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("screen", screen)   // read this in MainActivity to navigate
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)  // change to your icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}