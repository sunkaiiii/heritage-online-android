package com.example.sunkai.heritage.tools

import android.content.Context
import android.graphics.Bitmap
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur

/**
 * Bitmap相关的辅助类
 * Created by sunkai on 2018/3/21.
 */


fun Bitmap.toBlurBitmap(context: Context, radius: Float = 25.0f): Bitmap {
    try{
        val inputBmp = this
        val renderScript = RenderScript.create(context)
        //创建Allocation，用于存储renderScript对象
        val input = Allocation.createFromBitmap(renderScript, inputBmp)
        val output = Allocation.createTyped(renderScript, input.type)
        //创建ScirptIntrinsic，内置RenderScript通用操作，比如高斯模糊、扭曲变换、图像混合
        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        scriptIntrinsicBlur.setInput(input)
        scriptIntrinsicBlur.setRadius(radius)
        //启动模糊
        scriptIntrinsicBlur.forEach(output)
        output.copyTo(inputBmp)
        //销毁对象
        renderScript.destroy()
        return inputBmp
    }catch (e:Exception){
        e.printStackTrace()
    }
    return this

}