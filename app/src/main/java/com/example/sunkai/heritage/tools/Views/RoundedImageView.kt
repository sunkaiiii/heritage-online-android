package com.example.sunkai.heritage.tools.Views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

class RoundedImageView:ImageView {
    constructor(context: Context):super(context)
    constructor(context: Context, attrs: AttributeSet):super(context,attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle:Int):super(context,attrs,defStyle)
    private var mShader:BitmapShader?=null
    private var mCurrentBitmap:Bitmap?=null
    private val mMatrix = Matrix()
    private val mPaintBitmap=Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas: Canvas?) {
        val rawBitmap=getBitmap(drawable)
        if(rawBitmap==null){
            super.onDraw(canvas)
        }else {
            val viewWidth = width
            val viewHeight = height
            val viewMinSize = Math.min(viewWidth, viewHeight)
            val dstWidth = viewMinSize.toFloat()
            val dstHeight = viewMinSize.toFloat()
            if (mShader == null || rawBitmap != mCurrentBitmap) {
                mCurrentBitmap = rawBitmap
                mShader = BitmapShader(mCurrentBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
            if (mShader != null) {
                mMatrix.setScale(dstWidth / rawBitmap.width, dstHeight / rawBitmap.height)
                mShader?.setLocalMatrix(mMatrix)
            }
            mPaintBitmap.shader = mShader
            val radius = viewMinSize / 2.0f
            canvas?.drawCircle(radius, radius, radius, mPaintBitmap)
        }
    }

    private fun getBitmap(drawable:Drawable?):Bitmap?{
        drawable?:return null
        if(drawable is BitmapDrawable){
            return drawable.bitmap
        }else if(drawable is ColorDrawable){
            val rect=drawable.bounds
            val width=rect.right-rect.left
            val height=rect.bottom-rect.bottom
            val color=drawable.color
            val bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
            val canvas=Canvas(bitmap)
            canvas.drawARGB(Color.alpha(color),Color.red(color),Color.green(color),Color.blue(color))
            return bitmap
        }
        return null
    }
}