package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.request.ProjectDetailRequest
import com.example.sunkai.heritage.entity.response.ProjectDetailResponse
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.value.DATA
import kotlinx.android.synthetic.main.activity_project_detail.*

class ProjectDetailActivity : BaseGlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)
        loadPageDetail()
    }

    private var projectDetailToolbarLocationArray = IntArray(2)
    private var projectDetailTitleLocationArray = IntArray(2)
    private fun initView(projectDetail: ProjectDetailResponse) {
        projectDetailToolbarBackArrow.setOnClickListener { finish() }
        projectDetailToolbarTitle.text = projectDetail.title
        projectDetailToolbarTitle.alpha = 0f
        projectDetailScrollView.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            projectDetailTitle.getLocationInWindow(projectDetailTitleLocationArray)
            projectDetailToolbar.getLocationInWindow(projectDetailToolbarLocationArray)
            val scrollDy = scrollY - oldScrollY
            //设置toolbar title的透明度
            var crossY = projectDetailToolbarLocationArray[1] + projectDetailToolbar.height - projectDetailTitleLocationArray[1]
            if (crossY < 0) {
                crossY = 0
            }
            val alpha = crossY.toFloat() / projectDetailToolbar.height
            projectDetailToolbarTitle.alpha = alpha
            //设置toolbar的位置
            if (projectDetailTitleLocationArray[1] < projectDetailToolbarLocationArray[1] && projectDetailToolbar.translationY + projectDetailToolbar.height >= 0 && scrollDy > 0) {
                projectDetailToolbar.translationY = projectDetailToolbar.translationY - scrollDy
            } else if (scrollDy < 0 && projectDetailToolbar.translationY < 0) {
                projectDetailToolbar.translationY = projectDetailToolbar.translationY - scrollDy
                if (projectDetailToolbar.translationY > 0) {
                    projectDetailToolbar.translationY = 0f
                }
            }
        }
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
        changeWidgeTheme()
        initView(projectDetail)
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
