package com.example.sunkai.heritage.views

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.ProjectDetailActivity
import com.example.sunkai.heritage.entity.response.nestedData.RelevantProject
import com.example.sunkai.heritage.value.DATA

class ProjectDetailRalevantView(context: Context, attr: AttributeSet?) : FrameLayout(context, attr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.activity_project_detail_relative_project_layout, this, true)
    }

    fun setData(datas: List<RelevantProject>) {
        val contentView: LinearLayout = findViewById(R.id.projectDetailRelativeInheritate)
        datas.forEach { project ->
            try {
                val itemView = LayoutInflater.from(context).inflate(R.layout.activity_project_detail_inheritate_layout_item, contentView, false)
                val name: TextView = itemView.findViewById(R.id.relevant_name)
                val province: TextView = itemView.findViewById(R.id.relevant_provance)
                name.text = project.content.first { it.key.contains("名称") }.value
                province.text = project.content.first { it.key.contains("地区") }.value
                itemView.setOnClickListener {
                    val intent = Intent(context, ProjectDetailActivity::class.java)
                    intent.putExtra(DATA, project.link)
                    context.startActivity(intent)
                }
                contentView.addView(itemView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}