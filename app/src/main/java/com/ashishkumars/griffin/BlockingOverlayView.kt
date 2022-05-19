package com.ashishkumars.griffin

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.ashishkumars.griffin.databinding.LayoutBlockingOverlayViewBinding
import com.ashishkumars.griffin.utils.SettingsManager
import timber.log.Timber

class BlockingOverlayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val binding: LayoutBlockingOverlayViewBinding =
        LayoutBlockingOverlayViewBinding.inflate(LayoutInflater.from(context))
    private val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var countDownTimer: CountDownTimer? = null
    private val settingsManager: SettingsManager

    init {
        binding.btnClose.setOnClickListener {
            closeOverlay()
        }
        settingsManager = SettingsManager(context)
    }

    fun showOverlay() {
        closeOverlay()
        val overlayBlockDurationInMs = (settingsManager.getDelay() * 1000).toLong()
        binding.circularProgress.max = overlayBlockDurationInMs.toInt()
        if (settingsManager.getIsDebugMode()) {
            binding.btnClose.visibility = VISIBLE
        }

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val mParams = WindowManager.LayoutParams(
            layoutFlag,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.OPAQUE
        )
        windowManager.addView(binding.root, mParams)

        Timber.d("Overlay shown")

        // Don't disable button in debug mode
        binding.btnClose.isEnabled = BuildConfig.DEBUG

        countDownTimer = object : CountDownTimer(overlayBlockDurationInMs, 100) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = (millisUntilFinished / 1000).toString()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.circularProgress.setProgress(millisUntilFinished.toInt(), true)
                } else {
                    binding.circularProgress.progress = millisUntilFinished.toInt()
                }
            }

            override fun onFinish() {
                binding.circularProgress.progress = 0
                binding.btnClose.isEnabled = true
                closeOverlay()
            }
        }
        countDownTimer?.start()
    }

    fun closeOverlay() {
        if (binding.root.parent != null) {
            Timber.d("Overlay closed")
            countDownTimer?.cancel()
            windowManager.removeView(binding.root)
        }
    }
}