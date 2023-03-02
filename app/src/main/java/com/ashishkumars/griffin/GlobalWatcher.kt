package com.ashishkumars.griffin

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager

class GlobalWatcher(private val context: Context) {

    private val receiver = GlobalBroadcastReceiver()
    private val filter = IntentFilter().also {
        it.addAction(Intent.ACTION_USER_PRESENT)
        it.addAction(Intent.ACTION_SCREEN_ON)
        it.addAction(Intent.ACTION_SCREEN_OFF)
        it.addAction(Intent.ACTION_BOOT_COMPLETED)
        it.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
    }

    fun startWatch() {
        context.registerReceiver(receiver, filter)
    }

    fun stopWatch() {
        context.unregisterReceiver(receiver)
    }
}