package com.example.sunkai.heritage.Views

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.InheritateDetailActivity
import com.example.sunkai.heritage.entity.response.nestedData.InheritatePeople
import com.example.sunkai.heritage.value.DATA

class ProjectDetailInheritateView(context: Context, attr: AttributeSet? = null) : FrameLayout(context, attr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.activity_project_detail_inheritate_layout, this, true)
    }

    fun setData(datas: List<InheritatePeople>) {
        val parentLayout: LinearLayout = findViewById(R.id.projectDetailRelativeInheritate)
        var i = 0
        while (i < datas.size) {
            val content = datas[i]
            val contentView = LayoutInflater.from(context).inflate(R.layout.activity_project_detail_inheritate_content, parentLayout, false)
            val contentLayout: LinearLayout = contentView.findViewById(R.id.inheritateMainContentLayout)
            contentView.setOnClickListener {
                val intent = Intent(context,InheritateDetailActivity::class.java)
                intent.putExtra(DATA, content.link)
                context.startActivity(intent)
            }
            contentView.findViewById<ImageView>(R.id.inheritateShowMore).setOnClickListener {
                if (context is AppCompatActivity) {
                    TransitionManager.beginDelayedTransition((context as AppCompatActivity).window.decorView as ViewGroup, ChangeBounds())
                }

                contentLayout.visibility = if (contentLayout.visibility == View.GONE) View.VISIBLE else View.GONE
            }
            val name: TextView = contentView.findViewById(R.id.inheritateName)
            name.text = String.format("%d.%s", i + 1, content.content.first { it.key.contains("姓名") }.value)
            content.content.filter { (!it.key.contains("姓名")).and(it.value.isNotBlank()) }.forEach {
                val textView = TextView(context)
                textView.text = String.format("%s:%s", it.key, it.value)
                contentLayout.addView(textView)
            }
            parentLayout.addView(contentView)
            i++
        }
    }
}