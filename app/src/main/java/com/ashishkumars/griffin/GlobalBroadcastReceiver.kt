package com.ashishkumars.griffin

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import timber.log.Timber

class GlobalBroadcastReceiver : BroadcastReceiver() {
    private lateinit var context: Context
    private val overlayView by lazy { BlockingOverlayView(context) }
    private val powerManager by lazy { getSystemService(context, PowerManager::class.java) }
    private var screenUnlockTime: Long = 0L
    private fun checkDebounce(): Boolean {
        return System.currentTimeMillis() - screenUnlockTime > 1_000
    }

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context

        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val foregroundServiceIntent = Intent(context, ForegroundService::class.java)
            ContextCompat.startForegroundService(context, foregroundServiceIntent)
        } else if (intent.action.equals(Intent.ACTION_USER_PRESENT)) {
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