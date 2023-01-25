package com.ashishkumars.griffin

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import timber.log.Timber

class EdgeTimerView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    var duration = 0L

    private var p1 = 0F
    private var p2 = 0F
    private var p3 = 0F
    private var p4 = 0F
    private var paint: Paint = Paint()
    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.GREEN
        paint.strokeWidth = 50F
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        //TODO: move all the init outside
        //TODO: multiple instance fix
        //TODO: issue with firefox, fix the flag value

        val va1 = ValueAnimator.ofFloat(0f, 1f)
        val va2 = ValueAnimator.ofFloat(0f, 1f)
        val va3 = ValueAnimator.ofFloat(0f, 1f)
        val va4 = ValueAnimator.ofFloat(0f, 1f)

        val peri = (2 * width + 2 * height).toFloat()

        va1.duration = (width / peri * duration).toLong() //in millis
        va1.interpolator = LinearInterpolator()
        va1.addUpdateListener { animation ->
            Timber.d("P1 - ${animation.animatedFraction}")
            p1 = animation.animatedFraction
            invalidate()
            if (animation.animatedFraction == 1F) {
                Timber.d("START 2")
                va2.start()
            }
        }
        Timber.d("START 1")
        va1.start()

        va2.duration = (height / peri * duration).toLong() //in millis
        va2.interpolator = LinearInterpolator()
        va2.addUpdateListener { animation ->
            Timber.d("P2 - ${animation.animatedFraction}")
            p2 = animation.animatedFraction
            invalidate()
            if (animation.animatedFraction == 1F) {
                Timber.d("START 3")
                va3.start()
            }
        }

        va3.duration = (width / peri * duration).toLong() //in millis
        va3.interpolator = LinearInterpolator()
        va3.addUpdateListener { animation ->
            Timber.d("P3 - ${animation.animatedFraction}")
            p3 = animation.animatedFraction
            invalidate()
            if (animation.animatedFraction == 1F) {
                Timber.d("START 4")
                va4.start()
            }
        }

        va4.duration = (height / peri * duration).toLong() //in millis
        va4.interpolator = LinearInterpolator()
        va4.addUpdateListener { animation ->
            Timber.d("P4 - ${animation.animatedFraction}")
            p4 = animation.animatedFraction
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawLine(0F, 0F, p1 * width, 0F, paint)
        canvas.drawLine(width.toFloat(), 0F, width.toFloat(), p2 * height, paint)
        canvas.drawLine(
            width.toFloat(), height.toFloat(), (1 - p3) * width, height.toFloat(), paint
        )
        canvas.drawLine(0F, height.toFloat(), 0F, (1 - p4) * height, paint)
    }
}