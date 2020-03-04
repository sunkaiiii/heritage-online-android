package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.request.ProjectDetailRequest
import com.example.sunkai.heritage.entity.response.ProjectDetailResponse
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.Utils
import com.example.sunkai.heritage.value.DATA
import kotlinx.android.synthetic.main.activity_project_detail.*
import kotlinx.android.synthetic.main.activity_project_detail_inheritate_layout.*
import kotlinx.android.synthetic.main.activity_project_detail_top_layout.*
import java.lang.Exception

class ProjectDetailActivity : BaseGlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)
        loadPageDetail()
    }

    private fun loadPageDetail() {
        val link = intent.getStringExtra(DATA) ?: return
        requestHttp(ProjectDetailRequest(link), EHeritageApi.GetProjectDetail)
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when (api.getRequestApi()) {
            EHeritageApi.GetProjectDetail -> {
                val projectDetail = fromJsonToObject(response, ProjectDetailResponse::class.java)
                setDatatoView(projectDetail)
            }
        }
    }

    private fun setDatatoView(projectDetail: ProjectDetailResponse) {
        setContentView(R.layout.activity_project_detail)
        projectDetailTitle.text = projectDetail.title
        projectDesc.text = projectDetail.text
        projectDetail.desc.forEach {
            val textView = TextView(this)
            textView.text = it
            projectDetailGridlayout.addView(textView)
        }
        if (projectDetail.inheritate.isNotEmpty()) {
            activityProjectDetailInheritateLayout.visibility = View.VISIBLE
            var i = 0
            while (i < projectDetail.inheritate.size) {
                val content = projectDetail.inheritate[i]
                val contentView = LayoutInflater.from(this).inflate(R.layout.activity_project_detail_inheritate_content, projectDetailRelativeInheritate, false)
                val contentLayout: LinearLayout = contentView.findViewById(R.id.inheritateMainContentLayout)
                contentView.findViewById<ImageView>(R.id.inheritateShowMore).setOnClickListener {
                    TransitionManager.beginDelayedTransition(projectDetailRelativeInheritate, ChangeBounds())
                    contentLayout.visibility = if (contentLayout.visibility == View.GONE) View.VISIBLE else View.GONE
                }
                val name: TextView = contentView.findViewById(R.id.inheritateName)
                name.text = String.format("%d.%s", i + 1, content.content.first { it.key.contains("姓名") }.value)
                content.content.filter { (!it.key.contains("姓名")).and(it.value.isNotBlank()) }.forEach {
                    val textView = TextView(this)
                    textView.text = String.format("%s:%s", it.key, it.value)
                    contentLayout.addView(textView)
                }
                projectDetailRelativeInheritate.addView(contentView)
                i++
            }

            if (!projectDetail.ralevant.isNullOrEmpty()) {
                activityProjectDetailRalevantProject.visibility = View.VISIBLE
                val contentView: LinearLayout = activityProjectDetailRalevantProject.findViewById(R.id.projectDetailRelativeInheritate)
                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(Utils.dip2px(8))
                projectDetail.ralevant.forEach { it ->
                    try {
                        val textView = TextView(this)
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
    }


}
