package com.ashishkumars.griffin

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.getSystemService
import timber.log.Timber

class GlobalWatcher(private val context: Context) {

    private val receiver = GlobalBroadcastReceiver()
    private val filter = IntentFilter().also {
        it.addAction(Intent.ACTION_USER_PRESENT)
        it.addAction(Intent.ACTION_SCREEN_ON)
        it.addAction(Intent.ACTION_SCREEN_OFF)
        it.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
    }

    fun startWatch() {
        context.registerReceiver(receiver, filter)
    }

    fun stopWatch() {
        context.unregisterReceiver(receiver)
    }

    var screenUnlockTime: Long = 0L
    fun checkDebounce(): Boolean {
        return System.currentTimeMillis() - screenUnlockTime > 1_000
    }

    inner class GlobalBroadcastReceiver : BroadcastReceiver() {
        private val overlayView = BlockingOverlayView(context)
        private val powerManager = getSystemService(context, PowerManager::class.java)

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Intent.ACTION_USER_PRESENT)) {
                if (checkDebounce()) {
                    Timber.d("ScreenUnlocked-KeyGuard unlocked User present")
                    overlayView.showOverlay()
                    screenUnlockTime = System.currentTimeMillis()
                }
            } else if (intent.action.equals(Intent.ACTION_SCREEN_ON)) {
                val keyguardManager = getSystemService(context, KeyguardManager::class.java)
                if (keyguardManager != null && keyguardManager.isDeviceLocked.not()) {
                    if (checkDebounce()) {
                        Timber.d("ScreenUnlocked-KeyGuard was not locked")
                        overlayView.showOverlay()
                        screenUnlockTime = System.currentTimeMillis()
                    }
                }
            } else if (intent.action.equals(Intent.ACTION_SCREEN_OFF)) {
                Timber.d("ScreenLocked")
                if (powerManager?.isInteractive == false) {
                    overlayView.closeOverlay()
                }
            } else if (intent.action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                if (intent.extras?.get("state") == TelephonyManager.EXTRA_STATE_RINGING) {
                    Timber.d("PhoneRinging")
                    overlayView.closeOverlay()
                }
            }
        }
    }
}