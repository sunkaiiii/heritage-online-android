package com.example.sunkai.heritage.views

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import com.github.chrisbanes.photoview.PhotoView
import java.util.*
import kotlin.math.abs

class SwipePhotoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {
    private val TAG = javaClass.name
    private var firstX = Integer.MIN_VALUE
    private var firstY = Integer.MIN_VALUE
    private var offsetX = Integer.MIN_VALUE
    private var offsetY = Integer.MIN_VALUE
    private var firstCalendar: Calendar? = null
    private var multiTouch = false
    private var horizonScroll: Boolean? = null
    private var isInViewPager = false
    private var rawHeight = -1
    private var rawWidth = -1
    private var onDragListner: OnDragListner? = null
    private var onClickListener: OnClickListener? = null
    private var onLongClickListener: OnLongClickListener? = null

    private var longClickHandler = Handler(Looper.getMainLooper()) {
        onLongClickListener?.onLongClick(this)
        true
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                longClickHandler.sendEmptyMessageDelayed(0, 1000)
                firstCalendar = Calendar.getInstance()
                firstX = (event.rawX - translationX).toInt()
                firstY = (event.rawY - translationY).toInt()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.d(TAG, "Multiple touch detected.")
                multiTouch = true
            }
            MotionEvent.ACTION_MOVE -> {
                longClickHandler.removeMessages(0)
                if (horizonScroll == null && isInViewPager) {
                    Log.d(TAG, isHorizonScroll(event).toString())
                    horizonScroll = isHorizonScroll(event)
                }
                if (shouldHandleEvent()) {
                    handleViewMove(event)
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                longClickHandler.removeMessages(0)
                val endCalendar = Calendar.getInstance()
                val time = endCalendar.timeInMillis - (firstCalendar?.timeInMillis ?: 0)
                if (isClickOrDragUp(time)) {
                    if (dragOutBound()) {
                        onDragListner?.onDragClose()
                    } else {
                        boundToOrinalPlace()
                    }
                    resetVariable()
                    return true
                } else {
                    resetVariable()
                    resetTranslate()
                    multiTouch = false
                    onClickListener?.onClick(this)
                }

            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        this.onLongClickListener = l
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.onClickListener = l
    }

    private fun isHorizonScroll(event: MotionEvent): Boolean? {
        val offsetX = abs(firstX - event.rawX)
        val offsetY = abs(firstY - event.rawY)
        Log.d(TAG, String.format("offsetX:%f,offsetY:%f", offsetX, offsetY))
        if (offsetX == offsetY && offsetX == 0F) {
            return null
        }
        return abs(offsetX) > abs(offsetY)
    }

    private fun resetTranslate() {
        translationX = 0F
        translationY = 0F
        onDragListner?.onImageBack(onDragListner?.getRootViewAlphaWhenImageBack() ?: 255, 1, 1)
        val layoutParams = layoutParams
        layoutParams.height = rawHeight
        layoutParams.width = rawWidth
        setLayoutParams(layoutParams)
    }

    private fun boundToOrinalPlace() {
        val scaledHeight = height
        val scaledWidth = width
        animate().translationX(0f).translationY(0f).setUpdateListener {
            onDragListner?.onImageBack(onDragListner?.getRootViewAlphaWhenImageBack()
                    ?: 255, it.currentPlayTime, it.duration)
            if (it.currentPlayTime != it.duration) {
                val currentHeight = scaledHeight + (rawHeight - scaledHeight) * it.currentPlayTime / it.duration
                val currentWidth = scaledWidth + (rawWidth - scaledWidth) * it.currentPlayTime / it.duration
                val layoutParams = layoutParams
                layoutParams.height = currentHeight.toInt()
                layoutParams.width = currentWidth.toInt()
                setLayoutParams(layoutParams)
            } else {
                val layoutParams = layoutParams
                layoutParams.height = rawHeight
                layoutParams.width = rawWidth
                setLayoutParams(layoutParams)
            }
        }.start()
    }


    private fun resetVariable() {
        firstX = Integer.MIN_VALUE
        firstY = Integer.MIN_VALUE
        offsetX = Integer.MIN_VALUE
        offsetY = Integer.MIN_VALUE
        horizonScroll = null
    }

    private fun dragOutBound() = abs(offsetY) >= getRawHeight() / 4

    private fun handleViewMove(event: MotionEvent) {
        offsetX = event.rawX.toInt() - firstX
        offsetY = event.rawY.toInt() - firstY
        Log.d(TAG, String.format("onDrag firstX:%d,firstY:%d,offsetX:%d,offsetY:%d", firstX, firstY, offsetX, offsetY))
        translationX = offsetX.toFloat()
        translationY = offsetY.toFloat()
        onDragListner?.onDrag(this, offsetX, offsetY)
    }

    private fun shouldHandleEvent() = scale == 1.0f && !multiTouch && ((isInViewPager && horizonScroll == false) || !isInViewPager)

    private fun isClickOrDragUp(time: Long): Boolean = !multiTouch && scale == 1.0f && (firstX != Integer.MIN_VALUE || firstY != Integer.MIN_VALUE) && (offsetY > 15 || time > 100)

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        if (rawHeight <= 0) {
            rawHeight = height
        }
        if (rawWidth <= 0) {
            rawWidth = width
        }
        super.setLayoutParams(params)
    }

    fun setOnDragListner(listener: OnDragListner?) {
        this.onDragListner = listener
    }

    fun getRawHeight() = if (rawHeight > 0) rawHeight else height

    fun getRawWidth() = if (rawWidth > 0) rawWidth else width

    fun setIsInViewPager(inViewpager: Boolean) {
        this.isInViewPager = inViewpager
    }

    fun isMoved() = offsetX != Int.MIN_VALUE || offsetY != Int.MIN_VALUE


    interface OnDragListner {
        fun onDrag(imageView: SwipePhotoView, dx: Int, dy: Int)
        fun onDragClose()
        fun onImageBack(originalAlpha: Int, currentTime: Long, duration: Long)
        fun getRootViewAlphaWhenImageBack(): Int
    }
}