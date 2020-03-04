package com.example.sunkai.heritage.activity

import android.os.Bundle
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.request.ProjectDetailRequest
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.value.DATA
import kotlinx.android.synthetic.main.activity_project_detail.*

class ProjectDetailActivity : BaseGlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)
        loadPageDetail()
    }

    private fun loadPageDetail() {
        val link = intent.getStringExtra(DATA)?:return
        requestHttp(ProjectDetailRequest(link),EHeritageApi.GetProjectDetail)
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when(api.getRequestApi())
        {
            EHeritageApi.GetProjectDetail->{
                setContentView(R.layout.activity_project_detail)
            }
        }
    }




}
