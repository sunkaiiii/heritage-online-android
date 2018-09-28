package com.example.sunkai.heritage.tools

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.READ
import com.example.sunkai.heritage.value.THEME_COLOR_ARRAYS
import java.io.ByteArrayInputStream

/**
 * 图片压缩辅助类
 */

fun HandleImage(uri: Uri?): Bitmap? {
    return if (uri != null) {
        compressImage(uri)
    } else {
        toast("获取图片失败...")
        null
    }
}

private fun compressImage(uri: Uri): Bitmap? {
    val parcelFileDescrptor = GlobalContext.instance.contentResolver.openFileDescriptor(uri, READ)
            ?: return null
    val fileDescriptor = parcelFileDescrptor.fileDescriptor
    val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor) //用这个方法可以得到Bitmap
    //获取图片exif信息
    val exifInterface = ExifInterface(GlobalContext.instance.contentResolver.openInputStream(uri)
            ?: return null) //用这个方法可以得到流
    parcelFileDescrptor.close()
    return corpImage(bitmap, exifInterface)
}

private fun corpImage(bitmap: Bitmap, exifInterface: androidx.exifinterface.media.ExifInterface): Bitmap? {
    return if (isBitmapNeedCompress(bitmap)) {
        compressBitmap(bitmap, exifInterface)
    } else {
        bitmap
    }
}

private fun isBitmapNeedCompress(bitmap: Bitmap): Boolean {
    return bitmap.toByteArray().size > (compressMinLimit shl 10)
}

private fun compressBitmap(bitmap: Bitmap, exifInterface: ExifInterface): Bitmap? {
    val factoryOptions = BitmapFactory.Options()
    factoryOptions.inSampleSize = calculateSize(bitmap)
    val corpBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(bitmap.toByteArray()), null, factoryOptions)
            ?: return null
    bitmap.recycle()
    return rotatingImage(corpBitmap, exifInterface)
}

private fun calculateSize(bitmap: Bitmap): Int {
    val srcWidth = if (bitmap.width % 2 == 1) bitmap.width + 1 else bitmap.width
    val srcHeight = if (bitmap.height % 2 == 1) bitmap.height + 1 else bitmap.height
    val longSide = Math.max(srcWidth, srcHeight)
    val shortSide = Math.min(srcWidth, srcHeight)
    val scale = shortSide.toFloat() / longSide
    return if (scale <= 1 && scale > 0.5625) {
        if (longSide < 664) {
            1
        } else if (longSide in 900..2200) {
            2
        } else if (longSide in 2200..5889) {
            4
        } else if (longSide in 5889..20480) {
            8
        } else {
            if (longSide / 1280 == 0) 1 else longSide / 1280
        }
    } else if (scale <= 0.5625 && scale > 0.5) {
        if (longSide / 1280 == 0) 1 else longSide / 1280
    } else {
        Math.ceil(longSide / (1280.0 / scale)).toInt()
    }

}

private fun rotatingImage(bitmap: Bitmap, exifInterface: androidx.exifinterface.media.ExifInterface): Bitmap {
    val matrix = Matrix()
    var angle = 0
    val orientation = exifInterface.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL)
    when (orientation) {
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> angle = 90
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> angle = 180
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> angle = 270
    }
    matrix.postRotate(angle.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

const val compressMinLimit = 100

