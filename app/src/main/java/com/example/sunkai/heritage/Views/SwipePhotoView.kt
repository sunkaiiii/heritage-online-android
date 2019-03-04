package com.example.sunkai.heritage.Views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import com.github.chrisbanes.photoview.PhotoView

class SwipePhotoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {
    var firstX = Integer.MIN_VALUE
    var firstY = Integer.MIN_VALUE
    var offsetX = Integer.MIN_VALUE
    var offsetY = Integer.MIN_VALUE
    private var rawHeight = -1
    private var rawWidth = -1
    private var onDragListner: OnDragListner? = null

    init {
        attacher.setOnViewDragListener { dx, dy ->
            if (scale == 1.0f) {
                Log.d("PhotoView", String.format("dx:%f,dy:%f", dx, dy))
                if (offsetX != Integer.MIN_VALUE && offsetY != Integer.MIN_VALUE) {
                    translationX = offsetX.toFloat()
                    translationY = offsetY.toFloat()
                }
            }
        }
        setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_MOVE && scale == 1.0f) {
                if (firstX == Integer.MIN_VALUE) {
                    firstX = (event.rawX - translationX).toInt()
                }
                if (firstY == Integer.MIN_VALUE) {
                    firstY = (event.rawY - translationY).toInt()
                }
                offsetX = event.rawX.toInt() - firstX
                offsetY = event.rawY.toInt() - firstY
                translationX = offsetX.toFloat()
                translationY = offsetY.toFloat()
                onDragListner?.onDrag(offsetX, offsetY)
                return@setOnTouchListener true
            } else if (event?.action == MotionEvent.ACTION_UP) {
                if (firstX != Integer.MIN_VALUE || firstY != Integer.MIN_VALUE) {
                    if (Math.abs(offsetY) >= rawHeight / 4) {
                        onDragListner?.onDragClose()
                        return@setOnTouchListener true
                    } else {
                        firstX = Integer.MIN_VALUE
                        firstY = Integer.MIN_VALUE
                        val scaledHeight = height
                        val scaledWidth = width
                        animate().translationX(0f).translationY(0f).setUpdateListener {
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
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener attacher.onTouch(v, event)
        }

    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        if (rawHeight <= 0) {
            rawHeight = height
        }
        if (rawWidth <= 0) {
            rawWidth = width
        }
        super.setLayoutParams(params)
    }

    fun setOnDragListner(listener: OnDragListner) {
        this.onDragListner = listener
    }

    fun getRawHeight() = rawHeight

    fun getRawWidth() = rawWidth

    interface OnDragListner {
        fun onDrag(dx: Int, dy: Int)
        fun onDragClose()
    }
}