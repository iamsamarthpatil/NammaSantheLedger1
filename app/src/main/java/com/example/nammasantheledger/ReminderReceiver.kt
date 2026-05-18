package com.example.nammasantheledger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description =
                "Daily Ledger Reminder"

            notificationManager.createNotificationChannel(
                channel
            )
        }

        val openIntent = Intent(
            context,
            MainActivity::class.java
        )

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )

            .setSmallIcon(android.R.drawable.ic_dialog_info)

            .setContentTitle(
                "📒 Namma Santhe Reminder"
            )

            .setContentText(
                "Don't forget today's transactions!"
            )

            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Tap to open Namma Santhe Ledger and update today's customer transactions."
                    )
            )

            .setPriority(
                NotificationCompat.PRIORITY_HIGH
            )

            .setContentIntent(pendingIntent)

            .setAutoCancel(true)

            .build()

        notificationManager.notify(
            NOTIFICATION_ID,
            notification
        )
    }

    companion object {

        const val CHANNEL_ID =
            "ledger_reminder_channel"

        const val NOTIFICATION_ID = 1001
    }
}