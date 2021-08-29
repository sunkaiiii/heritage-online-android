package com.example.sunkai.heritage.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.response.nestedData.InheritatePeople
import com.example.sunkai.heritage.value.DATA

class ProjectDetailInheritateView(context: Context, attr: AttributeSet? = null) :
    FrameLayout(context, attr) {
    init {
        LayoutInflater.from(context)
            .inflate(R.layout.fragment_project_detail_inheritate_layout, this, true)
    }

    fun setData(datas: List<InheritatePeople>) {
        val parentLayout: LinearLayout = findViewById(R.id.projectDetailRelativeInheritate)
        datas.forEach { people ->
            val contentView = LayoutInflater.from(context)
                .inflate(R.layout.fragment_project_detail_inheritate_content, parentLayout, false)
            val peopleName = contentView.findViewById<TextView>(R.id.people_name)
            peopleName.text = people.content.first { it.key.contains("姓名") }.value
            contentView.setOnClickListener {
                findNavController().navigate(
                    R.id.project_detail_to_inheritate_detail,
                    bundleOf(DATA to people.link)
                )
            }
            parentLayout.addView(contentView)
        }
    }
}