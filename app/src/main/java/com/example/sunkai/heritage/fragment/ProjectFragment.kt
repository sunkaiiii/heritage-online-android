package com.example.sunkai.heritage.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.AboutUSActivity
import com.example.sunkai.heritage.activity.SearchProjectActivity
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.dialog.FragmentProjectContentDialog
import com.example.sunkai.heritage.entity.request.BasePathRequest
import com.example.sunkai.heritage.entity.response.ProjectBasicInformation
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_project.*

class ProjectFragment : BaseLazyLoadFragment() {
    private var pageNumber = 1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun startLoadInformation() {
        requestHttp(EHeritageApi.GetProjectBasicInformation)
        initview()
    }

    override fun changeSpecificViewTheme() {
        val background = fragmentProjectTopBackgroundRelativeLayout.background
        if (background is GradientDrawable) {
            background.setColor(getTransparentColor(getThemeColor()))
        }

        projectOverViewCardview.setCardBackgroundColor(getDarkThemeColor())
    }

    private fun initview() {
        fragmentProjectSearchCard.setOnClickListener {
            navigateToSearchPage()
        }
        fragmentProjectToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_menu -> {
                    navigateToSearchPage()
                }
            }

            true
        }
    }


    private fun navigateToSearchPage() {
        val intent = Intent(context, SearchProjectActivity::class.java)
        startActivity(intent)
    }


    override fun onRestoreFragmentLoadInformation() {
        startLoadInformation()
    }


    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
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
            val visibility = if (recyclerView.y == 0f) View.VISIBLE else View.GONE
            if (fragmentProjectSearchCard.visibility != visibility) {
                when (visibility) {
                    View.VISIBLE -> {
                        fragmentProjectSearchCard.startAnimation(AnimationUtils.loadAnimation(context, R.anim.popup_enter))
                    }
                    View.GONE -> {
                        fragmentProjectSearchCard.startAnimation(AnimationUtils.loadAnimation(context, R.anim.popup_exit))
                    }
                }
            }
            fragmentProjectSearchCard.visibility = if (recyclerView.y == 0f) View.VISIBLE else View.GONE
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
        projectFragmentShowContent.setOnClickListener {
            val dialog = FragmentProjectContentDialog(context
                    ?: return@setOnClickListener, projectInformation)
            dialog.show()
            dialog.window?.setLayout(Utils.getScreenWidth(), (Utils.getScreenHeight() * 0.7).toInt())
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

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.fragment_project_top_background_image)
        fragmentProjectTopImage.setImageBitmap(bitmap.toBlurBitmap(context ?: return))
    }
}