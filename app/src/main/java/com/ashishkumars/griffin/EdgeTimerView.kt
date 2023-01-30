package com.ashishkumars.griffin

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
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
    private lateinit var va1: ValueAnimator
    private lateinit var va2: ValueAnimator
    private lateinit var va3: ValueAnimator
    private lateinit var va4: ValueAnimator

    var FLAG_CLEAN = false

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.GREEN
        paint.strokeWidth = 50F
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        println("ASHTEST: ATTACHED!")

        va1 = ValueAnimator.ofFloat(0f, 1f)
        va2 = ValueAnimator.ofFloat(0f, 1f)
        va3 = ValueAnimator.ofFloat(0f, 1f)
        va4 = ValueAnimator.ofFloat(0f, 1f)

        va1.interpolator = LinearInterpolator()
        va1.addUpdateListener { animation ->
//            Timber.d("P1 - ${animation.animatedFraction}")
            p1 = animation.animatedFraction
            invalidate()
            if (animation.animatedFraction == 1F) {
                Timber.d("START 2")
                va2.start()
            }
        }

        va2.interpolator = LinearInterpolator()
        va2.addUpdateListener { animation ->
//            Timber.d("P2 - ${animation.animatedFraction}")
            p2 = animation.animatedFraction
            invalidate()
            if (animation.animatedFraction == 1F) {
                Timber.d("START 3")
                va3.start()
            }
        }

        va3.interpolator = LinearInterpolator()
        va3.addUpdateListener { animation ->
//            Timber.d("P3 - ${animation.animatedFraction}")
            p3 = animation.animatedFraction
            invalidate()
            if (animation.animatedFraction == 1F) {
                Timber.d("START 4")
                va4.start()
            }
        }

        va4.interpolator = LinearInterpolator()
        va4.addUpdateListener { animation ->
//            Timber.d("P4 - ${animation.animatedFraction}")
            p4 = animation.animatedFraction
            invalidate()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        println("ASHTEST: onLayout Changed!")

        //TODO: move all the init outside
        //TODO: multiple instance fix
        //TODO: issue with firefox, fix the flag value

        if (va1.isRunning.not() && va2.isRunning.not() && va3.isRunning.not() && va4.isRunning.not()) {
            val peri = (2 * width + 2 * height).toFloat()

            va1.duration = (width / peri * duration).toLong() //in millis
            va2.duration = (height / peri * duration).toLong() //in millis
            va3.duration = (width / peri * duration).toLong() //in millis
            va4.duration = (height / peri * duration).toLong() //in millis

            Timber.d("START 1")
            va1.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (FLAG_CLEAN) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            FLAG_CLEAN = false
        }

        val paint2 = Paint()
        paint2.color = Color.RED
        paint2.strokeWidth = 10F
        paint2.textSize = 50F
        canvas.drawText("${this.id}", 100F, 400F, paint2)

        canvas.drawLine(0F, 0F, p1 * width, 0F, paint)
        canvas.drawLine(width.toFloat(), 0F, width.toFloat(), p2 * height, paint)
        canvas.drawLine(
            width.toFloat(), height.toFloat(), (1 - p3) * width, height.toFloat(), paint
        )
        canvas.drawLine(0F, height.toFloat(), 0F, (1 - p4) * height, paint)
    }

    fun clean() {
        FLAG_CLEAN = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        va1.cancel()
        va2.cancel()
        va3.cancel()
        va4.cancel()
        clean()
        invalidate()
        println("ASHTEST: onDetachedFromWindow!")
    }
}