package com.example.sunkai.heritage.Dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.content.edit
import com.example.sunkai.heritage.Dialog.Base.BaseDialogFragment
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.push_warining_layout.*

/**
 * 通知是否开启推送的dialog
 * Created by sunkai on 2018/3/8.
 */
class PushDialog:BaseDialogFragment(),View.OnClickListener {
    override fun getLayoutID(): Int {
        return R.layout.push_warining_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        push_open.setOnClickListener(this)
        push_close.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.push_open->{changePushState(true)}
            R.id.push_close->{changePushState(false)}
        }
        dismiss()
    }

    private fun changePushState(pushState:Boolean){
        if(pushState){
        }
        context?.getSharedPreferences("setting",Context.MODE_PRIVATE)?.edit {
            putBoolean("pushSwitch", pushState)
        }
    }


}