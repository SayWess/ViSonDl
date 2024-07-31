package com.example.visondl.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.visondl.R


class NotificationManager(private val context: Context) {

    private val CHANNEL_ID = "VERBOSE_NOTIFICATION"
    @JvmField
    val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
        "Verbose WorkManager Notifications"
    private val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications whenever work starts"
    private val NOTIFICATION_GROUP_KEY = "VisonDl"

    fun createNotificationChannel() {
        // Make a channel if necessary
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    fun basicNotificationBuilder(
        notificationTitle: String,
        message: String,
        progress: Int? = null,
        createdTime: Long = System.currentTimeMillis()
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notificationTitle)
            .setContentText(message)
            .setSilent(true)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setWhen(createdTime)

        if (progress != null) builder.setProgress(100, progress, false)

        return builder
    }

    fun summaryNotification(
        title: String,
        summaryText: String = "Downloads"
    ): NotificationCompat.Builder {
        return basicNotificationBuilder(title, "")
            .setGroupSummary(true)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(summaryText)
            )
            .setSortKey("By Title")
    }

    fun buildNotification(notificationId: Int, builder: NotificationCompat.Builder) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun clearNotification(id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }

}