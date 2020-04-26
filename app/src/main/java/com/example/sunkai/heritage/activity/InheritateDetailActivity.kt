package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.view.View
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.request.InheritateDetailRequest
import com.example.sunkai.heritage.entity.response.InheritateDetailResponse
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.value.DATA
import kotlinx.android.synthetic.main.activity_inheritate_detail.*

class InheritateDetailActivity : BaseGlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)
        init()
    }

    private fun init() {
        supportActionBar?.title = getString(R.string.inheritors)
        val link = intent.getStringExtra(DATA) ?: return
        requestHttp(InheritateDetailRequest(link), EHeritageApi.GetInheritateDetail)
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when (api.getRequestApi()) {
            EHeritageApi.GetInheritateDetail -> {
                setContentView(R.layout.activity_inheritate_detail)
                val inheritateData = fromJsonToObject(response, InheritateDetailResponse::class.java)
                setDataToView(inheritateData)
            }
        }
    }

    private fun setDataToView(inheritateData: InheritateDetailResponse) {
        inheritateTitle.text = inheritateData.title
        inheritateDetailDesc.text = inheritateData.text
        inheritateDetailTopGridLayout.setData(inheritateData.desc)
        inheritateData.inheritate?.let {
            if (it.isNotEmpty()) {
                inheritateOthersView.visibility = View.VISIBLE
                inheritateOthersView.setData(it)
            }
        }

    }

}
