package com.example.sunkai.heritage.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.Utils.dip2px
import com.example.sunkai.heritage.tools.getResourceColor

class PeopleDetailRoundedBackgroundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val p = Paint().apply {
        style = Paint.Style.FILL
        color = getResourceColor(R.color.material_dynamic_primary90)
        isAntiAlias = true
        strokeWidth = 20f
    }
    private val boundry = 116.dip2px().toFloat()
    private val start = PointF(0f, boundry)
    private val end = PointF(0f,boundry)
    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.reset()
        path.moveTo(start.x,start.y)
        val controlX = width/2f
        val controlY = 0f
        path.quadTo(controlX,controlY,width.toFloat(),start.y)
        path.lineTo(width.toFloat(),height.toFloat())
        path.lineTo(0f,height.toFloat())
        path.lineTo(end.x,end.y)
        canvas?.apply {
            drawPath(path,p)
        }
    }
}