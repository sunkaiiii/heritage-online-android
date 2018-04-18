package com.example.sunkai.heritage.Activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewAnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.net.toUri
import com.example.sunkai.heritage.Activity.BaseActivity.BaseStopGlideActivity
import com.example.sunkai.heritage.Dialog.LicenceDialog
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.activity_about_us.*

/**
 * Created by sunkai on 2017/12/29.
 *关于我们页面
 */
class AboutUSActivity:BaseStopGlideActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        val actionBack:ActionBar?= supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)
        licenceTextView.setOnClickListener {
            LicenceDialog().show(supportFragmentManager,"licence")
        }
        base_git_url.setOnClickListener {
            val intent=Intent(Intent.ACTION_VIEW).setData(base_git_url.text.toString().toUri())
            startActivity(intent)
//            base_git_url.pivotX=0f
//            base_git_url.pivotY=base_git_url.height.toFloat()
//            val objectAnimator=ObjectAnimator.ofFloat(base_git_url,"scaleX",0f,1f).setDuration(1000)
//            val objectAnimator2=ObjectAnimator.ofFloat(base_git_url,"scaleY",0f,1f).setDuration(1000)
//            objectAnimator.start()
//            objectAnimator2.start()
//            val animator=ViewAnimationUtils.createCircularReveal(base_git_url,0,base_git_url.height,0f,base_git_url.width.toFloat())
//            animator.interpolator=LinearInterpolator()
//            animator.startDelay=2000
//            animator.duration=1000
//            animator.start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->finish()
        }
        return super.onOptionsItemSelected(item)
    }
}