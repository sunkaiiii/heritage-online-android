package com.example.sunkai.heritage.tools

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import java.util.ArrayList

/**
 *  一个Transition的工具类
 * Created by sunkai on 2018/3/13.
 */
object TransitionHelper {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun createSafeTransitionParticipants(activity: Activity,
                                         includeStatusBar: Boolean, vararg otherParticipants: Pair<View, String>): Array<Pair<View, String>> {
        //避免一些迷之UI问题
        val decor=activity.window.decorView
        val statusBar:View?=if(includeStatusBar){
            decor.findViewById(android.R.id.statusBarBackground)
        }else{
            null
        }
        val navBar=decor.findViewById<View>(android.R.id.navigationBarBackground)

        //创建需要执行Transiton的对象
        val participants = ArrayList<Pair<View, String>>(3)
        addNonNullViewToTransitionParticipants(statusBar,participants)
        addNonNullViewToTransitionParticipants(navBar,participants)


        //如果有至少一个非空的元素，就只添加Transition参与者
        if(otherParticipants.size != 1){
            participants.addAll(otherParticipants)
        }
        return participants.toTypedArray()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addNonNullViewToTransitionParticipants(view: View?,participants:MutableList<Pair<View,String>>){
        val addView=view?:return
        participants.add(Pair(addView,addView.transitionName))
    }
}