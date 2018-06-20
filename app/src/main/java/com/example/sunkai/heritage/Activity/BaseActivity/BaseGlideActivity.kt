package com.example.sunkai.heritage.Activity.BaseActivity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool

abstract class BaseGlideActivity :AppCompatActivity(){
    protected var isDestroy=true
    protected lateinit var glide: RequestManager
    private val runnableList:MutableList<Runnable>
    init {
        runnableList= arrayListOf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor= ContextCompat.getColor(this, R.color.colorPrimary)
        }
        isDestroy=false
        glide=Glide.with(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() {
        super.onDestroy()
        isDestroy=true
        runnableList.forEach { ThreadPool.remove(it) }
        runnableList.clear()
    }

    protected fun requestHttp(runnable:()->Unit){
        requestHttp(Runnable(runnable))
    }

    protected fun requestHttp(runnable: Runnable){
        runnableList.add(runnable)
        ThreadPool.execute(runnable)
    }
}