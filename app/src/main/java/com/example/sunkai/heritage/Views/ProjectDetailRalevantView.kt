package com.example.sunkai.heritage.Views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setMargins
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.response.ProjectDetailResponse
import com.example.sunkai.heritage.tools.Utils

class ProjectDetailRalevantView(context:Context,attr:AttributeSet?):FrameLayout(context,attr){
    init {
        LayoutInflater.from(context).inflate(R.layout.activity_project_detail_relative_project_layout,this,true)
    }

    fun setData(datas:List<ProjectDetailResponse.RelevantProject>){
        val contentView: LinearLayout = findViewById(R.id.projectDetailRelativeInheritate)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(Utils.dip2px(8))
        datas.forEach { it ->
            try {
                val textView = TextView(context)
                textView.layoutParams = layoutParams
                textView.setTextColor(resources.getColor(R.color.black))
                textView.text = String.format("%s(%s)", it.content.first { it.key.contains("名称") }.value, it.content.first { it.key.contains("地区") }.value)
                contentView.addView(textView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}