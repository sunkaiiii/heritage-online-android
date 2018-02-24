package com.example.sunkai.heritage.Adapter.BaseAdapter

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.view.*
import com.example.sunkai.heritage.R

/**
 * 通用的底部dialog
 * Created by sunkai on 2018/2/24.
 */
class BaseBottomDialog(context: Context):AppCompatDialog(context,R.style.BottomDialog) {
    var height=-2
    override fun setContentView(layoutResID: Int) {
        val view=LayoutInflater.from(context).inflate(layoutResID,null)
        height=view.height
        super.setContentView(view)
    }

    override fun setContentView(view: View?) {
        view?.let{
            height=view.height
        }
        super.setContentView(view)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
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
}