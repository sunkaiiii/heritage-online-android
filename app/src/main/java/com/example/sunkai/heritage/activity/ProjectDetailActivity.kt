package com.example.sunkai.heritage.activity

import android.os.Bundle
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.value.DATA
import kotlinx.android.synthetic.main.activity_project_detail.*

class ProjectDetailActivity : BaseGlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)
        initWidget()
    }

    private fun initWidget() {
        val data = intent.getSerializableExtra(DATA) as ProjectListInformation
        projectDetailTitle.text=data.title
        projectId.text=data.auto_id
        projectNumber.text=data.num
        projectTime.text=data.rx_time
        projectType.text=data.type
        projectLocation.text=data.province
        projectCategory.text=data.cate
        projectDeclaration.text=data.unit
        projectDesc.text=data.content
    }


}
