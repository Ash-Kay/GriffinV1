package com.ashishkumars.griffin

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ashishkumars.griffin.databinding.ActivityMainBinding
import com.ashishkumars.griffin.utils.Constants.NOTIF_CHANNEL_ID
import com.ashishkumars.griffin.utils.SettingsManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        settingsManager = SettingsManager(this)

        createNotificationChannel()
        checkOverlayPermission()
        checkPhoneStatePermission()
        startForegroundService()
        setupDebugSwitch()
        setupTimePicker()

        binding.btnOverlayGrantPermission.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionResultLauncher.launch(intent)
        }

        binding.btnPhoneGrantPermission.setOnClickListener {
            phonePermissionResultLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }
    }

    override fun onResume() {
        super.onResume()
        checkOverlayPermission()
    }

    private fun setupTimePicker() {
        with(binding.timePicker) {
            val currentDelayInSec = settingsManager.getDelay()
            val mMinutes = currentDelayInSec / 60
            val mSeconds = currentDelayInSec % 60

            // Set delay for first launch
            if (mMinutes == 0 && mSeconds == 0) {
                hour = 0
                minute = 10
                settingsManager.setDelay(10)
            } else {
                hour = mMinutes
                minute = mSeconds
            }

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

    private var phonePermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                checkPhoneStatePermission()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                    Toast.makeText(this, "Phone permission Not Given!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Allow Phone permission form settings!", Toast.LENGTH_LONG).show()

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.parse("package:$packageName")
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }

    private var overlayPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private fun checkOverlayPermission() {
        if (Settings.canDrawOverlays(this)) {
            binding.sectionOverlayPermission.visibility = View.GONE
        } else {
            binding.sectionOverlayPermission.visibility = View.VISIBLE
        }
    }

    private fun checkPhoneStatePermission() {
        if (applicationContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            binding.sectionPhonePermission.visibility = View.VISIBLE
        } else {
            binding.sectionPhonePermission.visibility = View.GONE
        }
    }
}