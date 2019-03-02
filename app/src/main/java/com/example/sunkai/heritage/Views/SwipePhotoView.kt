package com.example.sunkai.heritage.Views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.github.chrisbanes.photoview.PhotoView

class SwipePhotoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {
    var firstX = Integer.MIN_VALUE
    var firstY = Integer.MIN_VALUE
    var offsetX = Integer.MIN_VALUE
    var offsetY = Integer.MIN_VALUE
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
                    firstX = Integer.MIN_VALUE
                    firstY = Integer.MIN_VALUE
                }
            }
            return@setOnTouchListener attacher.onTouch(v, event)
        }

    }

    fun setOnDragListner(listener: OnDragListner) {
        this.onDragListner = listener
    }

    interface OnDragListner {
        fun onDrag(dx: Int, dy: Int);
    }
}