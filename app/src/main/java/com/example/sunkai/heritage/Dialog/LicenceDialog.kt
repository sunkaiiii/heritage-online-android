package com.example.sunkai.heritage.Dialog

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.sunkai.heritage.Dialog.Base.BaseDialogFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.GLIDE_LICENCE

class LicenceDialog:BaseDialogFragment() {
    override fun getLayoutID(): Int {
        return R.layout.dialog_licence_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.glide_licence_textview).text = GLIDE_LICENCE
    }
}