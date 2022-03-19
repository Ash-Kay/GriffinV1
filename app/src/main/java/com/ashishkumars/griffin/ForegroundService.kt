package com.ashishkumars.griffin

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ashishkumars.griffin.Constants.NOTIF_CHANNEL_ID
import com.ashishkumars.griffin.Constants.NOTIF_ID
import timber.log.Timber

class ForegroundService : Service() {

    private lateinit var globalWatcher: GlobalWatcher
    private var isRunning = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d("Service Started")
        if (isRunning)
            return START_STICKY
        isRunning = true

        val notification = createNotification()
        startForeground(NOTIF_ID, notification)
        initWatchers(this)
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val openMainActivity = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, openMainActivity, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("Griffin")
            //.setContentText("Service Running...")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun initWatchers(context: Context) {
        globalWatcher = GlobalWatcher(context)
        globalWatcher.startWatch()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Service Destroyed")
        isRunning = false
        globalWatcher.stopWatch()
        stopForeground(true)
        stopSelf()
    }
}