package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.example.sunkai.heritage.R
import kotlin.math.abs

class RoundedShadowImageView : androidx.appcompat.widget.AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initAttrs(attrs)
    }

    private val paint = Paint()

    private var mShader: BitmapShader? = null
    private var mCurrentBitmap: Bitmap? = null
    private val mMatrix = Matrix()
    private var shadowDimention = 0f
    private val mPaintBitmap = Paint(Paint.ANTI_ALIAS_FLAG)

    private fun initAttrs(attrs: AttributeSet) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedShadowImageView)
        shadowDimention = typeArray.getDimension(R.styleable.RoundedShadowImageView_elevation, 0f)
        typeArray.recycle()
        setLayerType(View.LAYER_TYPE_SOFTWARE, paint)
        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaintBitmap)
        paint.isAntiAlias = true
        paint.setShadowLayer(shadowDimention, 0f, 0f, Color.BLACK)
    }

    override fun onDraw(canvas: Canvas?) {
        val rawBitmap = getBitmap(drawable)
        if (rawBitmap == null) {
            super.onDraw(canvas)
        } else {
            val viewWidth = width
            val viewHeight = height
            val dstWidth = viewWidth.toFloat() - shadowDimention * 2
            val dstHeight = viewHeight.toFloat() - shadowDimention * 2
            if (mShader == null || rawBitmap != mCurrentBitmap) {
                mCurrentBitmap = rawBitmap
                mShader = BitmapShader(mCurrentBitmap
                        ?: return, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
            if (mShader != null) {
                val scalex = dstWidth / rawBitmap.width
                val scaleY = dstHeight / rawBitmap.height
                mMatrix.setScale(scalex, scaleY)
                mMatrix.postTranslate(abs(viewWidth - dstWidth) / 2, abs(viewHeight - dstHeight) / 2)
                mShader?.setLocalMatrix(mMatrix)
            }
            mPaintBitmap.shader = mShader
            val radius = dstWidth / 2.0f
            val centreX = viewWidth / 2f
            val centreY = viewHeight / 2f
            if (shadowDimention > 0) {
                canvas?.drawCircle(centreX, centreY, radius, paint)
            }
            canvas?.drawCircle(centreX, centreY, radius, mPaintBitmap)

        }
    }

    private fun getBitmap(drawable: Drawable?): Bitmap? {
        drawable ?: return null
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        } else if (drawable is ColorDrawable) {
            val rect = drawable.bounds
            val width = rect.right - rect.left
            val height = rect.bottom - rect.bottom
            val color = drawable.color
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color))
            return bitmap
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable is AdaptiveIconDrawable) {
            return drawable.toBitmap()
        }
        return null
    }
}