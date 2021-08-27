package com.example.sunkai.heritage.fragment

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.databinding.FragmentProjectDetailBinding
import com.example.sunkai.heritage.entity.ProjectDetailViewModel
import com.example.sunkai.heritage.entity.response.ProjectDetailResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectDetailFragment : BaseViewBindingFragment<FragmentProjectDetailBinding>() {

    private val viewModel by lazy { ViewModelProvider(this).get(ProjectDetailViewModel::class.java) }

    override fun getBindingClass(): Class<FragmentProjectDetailBinding> = FragmentProjectDetailBinding::class.java

    override fun initView() {
        viewModel.projectDetail.observe(viewLifecycleOwner, {
            setDatatoView(it)
        })
        arguments?.getString(DATA)?.let { link ->
            binding.root.post {
                binding.projectDetailProgressBar.visibility = View.GONE
                binding.projectDetailScrollView.visibility = View.VISIBLE
                viewModel.loadProjectDetail(link)
            }
        }
    }
    private var projectDetailToolbarLocationArray = IntArray(2)
    private var projectDetailTitleLocationArray = IntArray(2)
    private fun initView(projectDetail: ProjectDetailResponse) {
        binding.projectDetailToolbarBackArrow.setOnClickListener { findNavController().popBackStack() }
        binding.projectDetailToolbarTitle.text = projectDetail.title
        binding.projectDetailToolbarTitle.alpha = 0f
        binding.projectDetailScrollView.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            binding.projectDetailToolbar.getLocationInWindow(projectDetailToolbarLocationArray)
            val scrollDy = scrollY - oldScrollY
            //设置toolbar title的透明度
            var crossY =
                projectDetailToolbarLocationArray[1] + binding.projectDetailToolbar.height - projectDetailTitleLocationArray[1]
            if (crossY < 0) {
                crossY = 0
            }
            val alpha = crossY.toFloat() / binding.projectDetailToolbar.height
            binding.projectDetailToolbarTitle.alpha = alpha
            //设置toolbar的位置
            if (projectDetailTitleLocationArray[1] < projectDetailToolbarLocationArray[1] && binding.projectDetailToolbar.translationY + binding.projectDetailToolbar.height >= 0 && scrollDy > 0) {
                binding.projectDetailToolbar.translationY = binding.projectDetailToolbar.translationY - scrollDy
            } else if (scrollDy < 0 && binding.projectDetailToolbar.translationY < 0) {
                binding.projectDetailToolbar.translationY = binding.projectDetailToolbar.translationY - scrollDy
                if (binding.projectDetailToolbar.translationY > 0) {
                    binding.projectDetailToolbar.translationY = 0f
                }
            }
        }
    }


    private fun setDatatoView(projectDetail: ProjectDetailResponse) {
        changeWidgeTheme()
        initView(projectDetail)
        binding.projectDesc.text = projectDetail.text
        binding.projectDetailTopGridView.setData(projectDetail.desc)
        if (!projectDetail.inheritate.isNullOrEmpty()) {
            binding.activityProjectDetailInheritateLayout.visibility = View.VISIBLE
            binding.activityProjectDetailInheritateLayout.setData(projectDetail.inheritate)
        }

        if (!projectDetail.ralevant.isNullOrEmpty()) {
            binding.activityProjectDetailRalevantProject.visibility = View.VISIBLE
            binding.activityProjectDetailRalevantProject.setData(projectDetail.ralevant)
        }
    }
}
