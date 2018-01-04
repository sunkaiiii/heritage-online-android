package com.example.sunkai.heritage.Data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream

import java.io.InputStream

/**
 * Created by 70472 on 2017/3/4.
 * 此类用于处理图片，包括压缩图片以及缩放图片
 *
 * HandlePic有两个重载方法，picID用于处理在src当中的已有的图片，InputStream用于处理将byte[]转换的从服务器中读取的图片
 * compressBitmapToFile用于将传入的bitmap压缩大小，width为w，height为h
 */

object HandlePic {
    fun handlePic(context: Context, picID: Int, size: Int): Bitmap {
        val `is` = context.resources.openRawResource(picID)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inSampleSize = size
        return BitmapFactory.decodeStream(`is`, null, options)
    }

    fun handlePic(`is`: InputStream, size: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inSampleSize = size
        return BitmapFactory.decodeStream(`is`, null, options)
    }

    fun bitmapToByteArray(bitmap: Bitmap):ByteArray{
        val out=ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out)
        return out.toByteArray()
    }

    fun drawableToByteArray(drawable: Drawable):ByteArray?{
        if(drawable is BitmapDrawable) {
            val bitmapDrawable = drawable as BitmapDrawable
            return bitmapToByteArray(bitmapDrawable.bitmap)
        }
        return null
    }

    fun compressBitmapToFile(bmp: Bitmap, w: Int, h: Int): Bitmap {
        val height: Int
        val width: Int
        if (bmp.height > bmp.width) {
            height = w
            width = h
        } else {
            height = h
            width = w
        }
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val rect = Rect(0, 0, width, height)
        canvas.drawBitmap(bmp, null, rect, null)
        return result
    }
}
