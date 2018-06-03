package com.example.sunkai.heritage.Dialog.Base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import android.view.*
import com.example.sunkai.heritage.R

/**
 * 通用的底部dialog
 * Created by sunkai on 2018/2/24.
 */
open class BaseBottomDialog(context: Context):AppCompatDialog(context,R.style.BottomDialog),View.OnClickListener {
    var height=-2
    var view:View?=null
    override fun setContentView(layoutResID: Int) {
        val view=LayoutInflater.from(context).inflate(layoutResID,null)
        this.view=view
        height=view.height
        super.setContentView(view)
    }

    override fun setContentView(view: View?) {
        this.view=view
        view?.let{
            height=view.height
        }
        super.setContentView(view)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        this.view=view
        view?.let{
            height=view.height
        }
        super.setContentView(view, params)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window=window
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.BOTTOM)
        window.setWindowAnimations(R.style.BottomDialog_Animation)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(isOutOfViews(context,event)){
            cancel()
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun isOutOfViews(context: Context,event: MotionEvent?):Boolean{
        return if(event==null){
            false
        }else{
            val y=(event.y).toInt()
            val slop=ViewConfiguration.get(context).scaledWindowTouchSlop
            (y<-slop) or (y>(height+slop))
        }
    }

    override fun onClick(v: View?) {}
}