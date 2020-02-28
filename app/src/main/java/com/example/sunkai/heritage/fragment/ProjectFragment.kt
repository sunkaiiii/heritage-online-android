package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.rsponse.ProjectBasicInformation
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.interfaces.RequestAction
import kotlinx.android.synthetic.main.fragment_project.*

class ProjectFragment : BaseLazyLoadFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun startLoadInformation() {
        requestHttp(EHeritageApi.GetProjectBasicInformation )
    }

    override fun onRestoreFragmentLoadInformation() {}

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        when(api.getRequestApi())
        {
            EHeritageApi.GetProjectBasicInformation->
            {
                val projectInformation=fromJsonToObject(response,ProjectBasicInformation::class.java)
                FillProjectBasicDataIntoView(projectInformation)
            }
        }
    }

    private fun FillProjectBasicDataIntoView(projectInformation: ProjectBasicInformation) {
        ProjectDescLoading.visibility=View.GONE
        ProjectPageLayout.visibility=View.VISIBLE
        ProjectPageTitle.text=projectInformation.title
        ProjectPageContent.text=projectInformation.content
        val layout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
        val textViewLayout=ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.weight=1.0f
        projectInformation.numItem.forEach {
            val linearLayout=LinearLayout(context)
            linearLayout.orientation=LinearLayout.VERTICAL
            linearLayout.layoutParams=layout
            ProjectDescLayout.addView(linearLayout)
            val itemTitle=TextView(context)
            itemTitle.layoutParams=textViewLayout
            itemTitle.gravity=Gravity.CENTER
            val itemNum=TextView(context)
            itemNum.layoutParams=textViewLayout
            itemNum.gravity=Gravity.CENTER
            itemTitle.text=it.desc
            itemNum.text=it.num
            linearLayout.addView(itemTitle)
            linearLayout.addView(itemNum)
        }
    }
}