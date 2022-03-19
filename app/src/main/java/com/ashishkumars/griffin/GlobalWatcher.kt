package com.ashishkumars.griffin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import timber.log.Timber

class GlobalWatcher(private val context: Context) {

    private val receiver = GlobalBroadcastReceiver()
    private val filter = IntentFilter().also {
        it.addAction(Intent.ACTION_USER_PRESENT)
        it.addAction(Intent.ACTION_SCREEN_OFF)
    }

    fun startWatch() {
        context.registerReceiver(receiver, filter)
    }

    fun stopWatch() {
        context.unregisterReceiver(receiver)
    }

    inner class GlobalBroadcastReceiver : BroadcastReceiver() {
        private val overlayView = BlockingOverlayView(context)

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Intent.ACTION_USER_PRESENT)) {
                Timber.d("ScreenUnlocked")
                overlayView.showOverlay()
            } else if (intent.action.equals(Intent.ACTION_SCREEN_OFF)) {
                Timber.d("ScreenLocked")
                overlayView.closeOverlay()
            }
        }
    }
}
