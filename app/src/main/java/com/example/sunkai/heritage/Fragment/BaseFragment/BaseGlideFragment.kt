package com.example.sunkai.heritage.Fragment.BaseFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.tools.ThreadPool

abstract class BaseGlideFragment: Fragment() {
    protected lateinit var glide: RequestManager
    private val runnableList:MutableList<Runnable>
    init {
        runnableList= arrayListOf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glide= Glide.with(this)

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

}