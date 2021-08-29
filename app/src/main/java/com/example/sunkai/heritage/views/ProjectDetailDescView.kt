package com.example.sunkai.heritage.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sunkai.heritage.R

class ProjectDetailDescView(context: Context, attr: AttributeSet?) :
    LinearLayout(context, attr) {

    fun setData(datas: List<String>) {
        datas.forEach {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_project_detail_desc_item, this, false)
            val textView = view.findViewById<TextView>(R.id.desc)
            textView.text = it
            addView(view)
        }
    }
}