package com.example.sunkai.heritage.Fragment.BaseFragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.tools.tintTextView

abstract class BaseGlideFragment: Fragment() {
    protected lateinit var glide: RequestManager
    private val runnableList:MutableList<Runnable>
    protected var changeThemeWidge:MutableList<Int>
    private var ignoreToolbar=false
    init {
        runnableList= arrayListOf()
        changeThemeWidge= arrayListOf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //glide不认androidx下的fragment，但是似乎编译能过
        glide= Glide.with(this)
        setNeedChangeThemeColorWidget()
    }

    override fun onStart() {
        super.onStart()
        val color= getThemeColor()
        changeThemeWidge.forEach {
            val view=activity?.findViewById<View>(it)
            if(view is TextView){
                tintTextView(view)
            }else{
                view?.setBackgroundColor(color)
            }
        }
    }

    protected fun requestHttp(runnable:()->Unit){
        requestHttp(Runnable(runnable))
    }

    protected fun requestHttp(runnable: Runnable){
        runnableList.add(runnable)
        ThreadPool.execute(runnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        runnableList.forEach { ThreadPool.remove(it) }
        runnableList.clear()
    }

    open fun setNeedChangeThemeColorWidget(){}

}