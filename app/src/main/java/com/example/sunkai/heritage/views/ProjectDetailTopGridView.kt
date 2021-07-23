package com.example.sunkai.heritage.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import com.example.sunkai.heritage.R

class ProjectDetailTopGridView(context: Context, attr: AttributeSet?) : FrameLayout(context, attr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.fragment_project_detail_top_layout, this, true)
    }

    fun setData(datas: List<String>) {
        val gridLayout: GridLayout = findViewById(R.id.projectDetailGridlayout)
        datas.forEach {
            val textView = TextView(context)
            textView.text = it
            gridLayout.addView(textView)
        }
    }
}