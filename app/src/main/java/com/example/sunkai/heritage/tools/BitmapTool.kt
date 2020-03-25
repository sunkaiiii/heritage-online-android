package com.example.sunkai.heritage.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import java.io.ByteArrayOutputStream

/**
 * Bitmap相关的辅助类
 * Created by sunkai on 2018/3/21.
 */

fun Bitmap.toByteArray(quality: Int = 60): ByteArray {
    val boas = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, quality, boas)
    return boas.toByteArray()
}

fun ByteArray?.toBitmap(): Bitmap? {
    if (this == null) {
        return null
    }
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

fun Bitmap.toBlurBitmap(context: Context, radius: Float = 16.0f): Bitmap {
    val inputBmp = this
    val renderScript = RenderScript.create(context)
    val input = Allocation.createFromBitmap(renderScript, inputBmp)
    val output = Allocation.createTyped(renderScript, input.type)
    val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    scriptIntrinsicBlur.setInput(input)
    scriptIntrinsicBlur.setRadius(radius)
    scriptIntrinsicBlur.forEach(output)
    output.copyTo(inputBmp)
    renderScript.destroy()
    return inputBmp
}