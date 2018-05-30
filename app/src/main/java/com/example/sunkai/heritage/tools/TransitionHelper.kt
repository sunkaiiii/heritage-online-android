package com.example.sunkai.heritage.tools

import android.util.Pair
import android.view.View
import com.example.sunkai.heritage.value.TransitionPair

fun CreateTransitionPair(view:View,stringID: Int):TransitionPair{
    return if(stringID>0) {
        Pair.create(view, GlobalContext.instance.getString(stringID))
    }else{
        Pair.create(view,"")
    }
}