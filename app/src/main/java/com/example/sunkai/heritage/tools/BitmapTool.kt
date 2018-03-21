package com.example.sunkai.heritage.tools

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * Bitmap相关的辅助类
 * Created by sunkai on 2018/3/21.
 */

fun Bitmap.toByteArray(quality:Int=60):ByteArray{
    val boas=ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG,quality,boas)
    return boas.toByteArray()
}