package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        projectDetailTopGridView.setData(projectDetail.desc)
        if (!projectDetail.inheritate.isNullOrEmpty()) {
            activityProjectDetailInheritateLayout.visibility = View.VISIBLE
            activityProjectDetailInheritateLayout.setData(projectDetail.inheritate)
        }

        if (!projectDetail.ralevant.isNullOrEmpty()) {
            activityProjectDetailRalevantProject.visibility = View.VISIBLE
            activityProjectDetailRalevantProject.setData(projectDetail.ralevant)
        }
    }
}
