package com.example.sunkai.heritage.fragment

import android.view.View
import androidx.lifecycle.ViewModelProvider
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



    private fun setDatatoView(projectDetail: ProjectDetailResponse) {
        changeWidgeTheme()
        binding.projectName.text = projectDetail.title
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
