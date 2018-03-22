package com.example.sunkai.heritage.Dialog

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.sunkai.heritage.Dialog.Base.BaseDialogFragment
import com.example.sunkai.heritage.R

/**
 * Created by sunkai on 2018/3/22.
 */
class NormalWarningDialog:BaseDialogFragment() {

    class Holder(view:View){
        val title:TextView
        val content:TextView
        val submit:TextView
        val cancel:TextView
        init {
            title=view.findViewById(R.id.warning_title)
            content=view.findViewById(R.id.warning_content)
            submit=view.findViewById(R.id.warning_submit)
            cancel=view.findViewById(R.id.warning_cancel)
        }
    }
    override fun getLayoutID(): Int {
        return R.layout.normal_warning_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val holder=Holder(view)
    }

    companion object {
        class Builder(){
            var title:String=""
            var content:String=""
        }
    }
}