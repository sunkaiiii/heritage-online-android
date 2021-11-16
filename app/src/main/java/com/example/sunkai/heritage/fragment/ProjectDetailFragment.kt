package com.example.sunkai.heritage.fragment

import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.databinding.FragmentProjectDetailBinding
import com.example.sunkai.heritage.entity.ProjectDetailViewModel
import com.example.sunkai.heritage.entity.response.ProjectDetailResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.PROJECT_TITLE
import com.example.sunkai.heritage.views.ProjectDetaiInheritateViewCompose
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectDetailFragment : BaseViewBindingFragment<FragmentProjectDetailBinding>() {

    private val viewModel by lazy { ViewModelProvider(this).get(ProjectDetailViewModel::class.java) }

    override fun getBindingClass(): Class<FragmentProjectDetailBinding> =
        FragmentProjectDetailBinding::class.java

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
        binding.toolbar.setNavigationOnClickListener{
            findNavController().popBackStack()
        }
    }


    private fun setDatatoView(projectDetail: ProjectDetailResponse) {
        changeWidgeTheme()
        binding.projectName.text = projectDetail.title
        binding.projectDesc.text = projectDetail.text
        binding.projectDetailTopGridView.setData(projectDetail.desc)
        binding.projectCategory.text = projectDetail.cate
        binding.projectNum.text = projectDetail.num
        if (!projectDetail.inheritate.isNullOrEmpty()) {
            binding.activityProjectDetailInheritateLayout.visibility = View.VISIBLE
            binding.activityProjectDetailInheritateLayout.setContent {
                ProjectDetaiInheritateViewCompose(
                    projectName = projectDetail.title,
                    datas = projectDetail.inheritate,
                    Modifier.fillMaxWidth()
                ) {
                    findNavController().navigate(
                        R.id.project_detail_to_inheritate_detail,
                        bundleOf(DATA to it, PROJECT_TITLE to projectDetail.title)
                    )
                }
            }
        }

        if (!projectDetail.ralevant.isNullOrEmpty()) {
            binding.activityProjectDetailRalevantProject.visibility = View.VISIBLE
            binding.activityProjectDetailRalevantProject.setData(projectDetail.ralevant)
        }
    }
}
