package com.ashishkumars.griffin

import android.content.Context
import android.graphics.PixelFormat
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
        val mParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.OPAQUE
        )
        windowManager.addView(binding.root, mParams)
    }

    fun closeOverlay() {
        if (binding.root.parent != null) {
            windowManager.removeView(binding.root)
        }
    }
}