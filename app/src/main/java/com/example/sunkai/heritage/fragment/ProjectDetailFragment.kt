package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.ProjectDetailViewModel
import com.example.sunkai.heritage.entity.response.ProjectDetailResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_project_detail.*

@AndroidEntryPoint
class ProjectDetailFragment : BaseGlideFragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(ProjectDetailViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_detail, container, false)
        viewModel.projectDetail.observe(viewLifecycleOwner, {
            setDatatoView(it)
        })
        arguments?.getString(DATA)?.let { link ->
            view.post {
                projectDetailProgressBar.visibility = View.GONE
                projectDetailScrollView.visibility = View.VISIBLE
                viewModel.loadProjectDetail(link)
            }
        }
        return view
    }

    private var projectDetailToolbarLocationArray = IntArray(2)
    private var projectDetailTitleLocationArray = IntArray(2)
    private fun initView(projectDetail: ProjectDetailResponse) {
        projectDetailToolbarBackArrow.setOnClickListener { findNavController().popBackStack() }
        projectDetailToolbarTitle.text = projectDetail.title
        projectDetailToolbarTitle.alpha = 0f
        projectDetailScrollView.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            projectDetailTitle.getLocationInWindow(projectDetailTitleLocationArray)
            projectDetailToolbar.getLocationInWindow(projectDetailToolbarLocationArray)
            val scrollDy = scrollY - oldScrollY
            //设置toolbar title的透明度
            var crossY =
                projectDetailToolbarLocationArray[1] + projectDetailToolbar.height - projectDetailTitleLocationArray[1]
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


    private fun setDatatoView(projectDetail: ProjectDetailResponse) {
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
