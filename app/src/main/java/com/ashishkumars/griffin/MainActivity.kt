package com.ashishkumars.griffin

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.ashishkumars.griffin.utils.Constants.NOTIF_CHANNEL_ID
import com.ashishkumars.griffin.databinding.ActivityMainBinding
import com.ashishkumars.griffin.utils.Constants
import com.ashishkumars.griffin.utils.SettingsManager
import com.ashishkumars.griffin.utils.SharedPreferenceManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        settingsManager = SettingsManager(this)

        createNotificationChannel()
        checkPermission()
        startForegroundService()
        setupDebugSwitch()
        setupTimePicker()
    }

    private fun setupTimePicker() {
        with(binding.timePicker) {
            val currentDelayInSec = settingsManager.getDelay()
            val minutes = currentDelayInSec / 60
            val seconds = currentDelayInSec % 60

            hour = minutes
            minute = seconds

            setIs24HourView(true)
            setOnTimeChangedListener { _, hourOfDay, minute ->
                val delayInIntSec = hourOfDay * 60 + minute
                settingsManager.setDelay(delayInIntSec)
            }
        }
    }

    private fun setupDebugSwitch() {
        val currentMode = settingsManager.getIsDebugMode()
        binding.switchDebugMode.isChecked = currentMode
        binding.switchDebugMode.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setIsDebugMode(isChecked)
        }
    }

    private fun startForegroundService() {
        val foregroundServiceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, foregroundServiceIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIF_CHANNEL_ID, "Main", importance)
            channel.description = "Keeps app alive"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                println("ASHTEST: got permission")
            }
        }

    private fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            resultLauncher.launch(intent)
        }
    }
}