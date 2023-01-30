package com.ashishkumars.griffin

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.ashishkumars.griffin.databinding.LayoutEdgeTimerOverlayViewBinding
import com.ashishkumars.griffin.utils.SettingsManager
import timber.log.Timber


class EdgeTimerOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding: LayoutEdgeTimerOverlayViewBinding =
        LayoutEdgeTimerOverlayViewBinding.inflate(LayoutInflater.from(context))
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var countDownTimer: CountDownTimer? = null
    private val settingsManager: SettingsManager

    init {
        settingsManager = SettingsManager(context)
    }

    fun showOverlay() {
        closeOverlay()
        settingsManager.setEdgeTimerDuration(10)
        val overlayBlockDurationInMs = (settingsManager.getEdgeTimerDuration() * 1000).toLong()

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val mParams = WindowManager.LayoutParams(
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        windowManager.addView(binding.root, mParams)

        Timber.d("Overlay shown")

        binding.edgeTimerView.duration = overlayBlockDurationInMs

        countDownTimer = object : CountDownTimer(overlayBlockDurationInMs, 100) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                closeOverlay()
            }
        }
        countDownTimer?.start()
    }

    fun closeOverlay() {
        if (binding.root.parent != null) {
            Timber.d("Overlay closed")
            countDownTimer?.cancel()
            binding.edgeTimerView.clean()
            windowManager.removeView(binding.root)
        }
    }
}