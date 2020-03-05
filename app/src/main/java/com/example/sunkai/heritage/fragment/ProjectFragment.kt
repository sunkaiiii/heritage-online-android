package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.request.BasePathRequest
import com.example.sunkai.heritage.entity.response.ProjectBasicInformation
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.OnSrollHelper
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_project.*

class ProjectFragment : BaseLazyLoadFragment() {
    private var pageNumber = 1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun startLoadInformation() {
        requestHttp(EHeritageApi.GetProjectBasicInformation)
    }

    override fun onRestoreFragmentLoadInformation() {}

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        when (api.getRequestApi()) {
            EHeritageApi.GetProjectBasicInformation -> {
                val projectInformation = fromJsonToObject(response, ProjectBasicInformation::class.java)
                FillProjectBasicDataIntoView(projectInformation)
                loadProjectList()
            }
            EHeritageApi.GetHeritageProjectList -> {
                val projectListData = fromJsonToList(response, ProjectListInformation::class.java)
                var adapter = ProjectInformationList.adapter
                if (adapter == null) {
                    adapter = ProjectInformationAdapter(context!!, projectListData, glide)
                    ProjectInformationList.adapter = adapter
                    fragmentProjectAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, _ ->
                        fragmentProjectSearchCard.visibility = if (ProjectInformationList.y == 0f) View.VISIBLE else View.GONE
                    })
                    ProjectInformationList.addOnScrollListener(onScrollDirectionHelper)
                    ProjectInformationList.addOnScrollListener(onScrollHelper)
                } else {
                    if (adapter is ProjectInformationAdapter) {
                        adapter.addNewData(projectListData)
                        onScrollHelper.setPageLoaded()
                    }
                }

            }
        }
    }

    private val onScrollHelper = object : OnSrollHelper() {
        override fun loadMoreData(recyclerView: RecyclerView) {
            loadProjectList()
        }
    }

    private val onScrollDirectionHelper = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            fragmentProjectSearchCard.visibility = if (recyclerView.y == 0f) View.VISIBLE else View.GONE
//            if (dy < 0 && fragmentProjectSearchCard.y <= Utils.dip2px(32)) {
//                fragmentProjectSearchCard.y = fragmentProjectSearchCard.y - dy
//            } else if (dy > 0 && fragmentProjectSearchCard.y + fragmentProjectSearchCard.height >= 0) {
//                fragmentProjectSearchCard.y = fragmentProjectSearchCard.y - dy
//            }
        }
    }

    private fun loadProjectList() {
        requestHttp(EHeritageApi.GetHeritageProjectList, object : BasePathRequest() {
            override fun getPathParamerater(): List<String> {
                return listOf(pageNumber++.toString())
            }
        })
    }

    private fun FillProjectBasicDataIntoView(projectInformation: ProjectBasicInformation) {
        ProjectDescLoading.visibility = View.GONE
        ProjectPageLayout.visibility = View.VISIBLE
        ProjectPageTitle.text = projectInformation.title
        ProjectPageContent.text = projectInformation.content
        ProjectShowContent.setOnClickListener {
            val animation = ChangeBounds()
            animation.duration = 300
            TransitionManager.beginDelayedTransition(ProjectPageLayout, animation)
            ProjectPageContent.maxLines = if (ProjectPageContent.maxLines == 999) 3 else 999
        }
        projectInformation.numItem.forEach {
            val itemLayout = LayoutInflater.from(context).inflate(R.layout.fragment_project_item_layout, ProjectDescLayout, false)
            itemLayout.findViewById<TextView>(R.id.FragmentProjectItemTitle).text = it.desc
            itemLayout.findViewById<TextView>(R.id.FragmentProjectItemContent).text = it.num
            val layoutParams = itemLayout.layoutParams
            if (layoutParams is LinearLayout.LayoutParams) {
                layoutParams.weight = 1.0f
                itemLayout.layoutParams = layoutParams
            }
            ProjectDescLayout.addView(itemLayout)
        }
    }
}