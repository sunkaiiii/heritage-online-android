package com.example.sunkai.heritage.tools

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView



/**正方形ImageView
 * Created by sunkai on 2018/2/22.
 */
class RectangleImageView:ImageView{
    private var radius=1.0
    constructor(context: Context):super(context)
    constructor(context: Context,attrs:AttributeSet):super(context,attrs)
    constructor(context: Context,attrs:AttributeSet,defStyle:Int):super(context,attrs,defStyle)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width=MeasureSpec.getSize(widthMeasureSpec)
        val height=(width*radius).toInt()
        setMeasuredDimension(width, height)
    }
}