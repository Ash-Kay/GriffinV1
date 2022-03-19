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


class BlockingOverlayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val binding: LayoutBlockingOverlayViewBinding =
        LayoutBlockingOverlayViewBinding.inflate(LayoutInflater.from(context))
    private val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    init {
        binding.btnClose.setOnClickListener {
            closeOverlay()
        }
    }

    fun showOverlay() {
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

        binding.btnClose.isEnabled = false
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                println("ASHTEST: ${millisUntilFinished / 1000}")
                binding.tvTimer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                binding.btnClose.isEnabled = true
            }
        }.start()
    }

    fun closeOverlay() {
        if (binding.root.parent != null) {
            windowManager.removeView(binding.root)
        }
    }
}