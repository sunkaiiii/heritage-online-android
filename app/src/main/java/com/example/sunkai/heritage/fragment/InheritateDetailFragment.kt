package com.example.sunkai.heritage.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.databinding.FragmentInheritateDetailBinding
import com.example.sunkai.heritage.entity.InheritateDetailViewModel
import com.example.sunkai.heritage.entity.response.InheritateDetailResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.PROJECT_TITLE
import com.example.sunkai.heritage.views.ProjectDetaiInheritateViewCompose
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InheritateDetailFragment : BaseViewBindingFragment<FragmentInheritateDetailBinding>() {
    private val viewModel by lazy { ViewModelProvider(this).get(InheritateDetailViewModel::class.java) }

    override fun getBindingClass(): Class<FragmentInheritateDetailBinding> =
        FragmentInheritateDetailBinding::class.java


    override fun initView() {
        viewModel.inheritateDetail.observe(viewLifecycleOwner, {
            setDataToView(it)
        })
        val link = arguments?.getString(DATA) ?: return
        viewModel.projectTitle.value = arguments?.getString(PROJECT_TITLE) ?: ""
        viewModel.setInheritateDetailLink(link)
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToView(inheritateData: InheritateDetailResponse) {
        binding.progressBar.visibility = View.GONE
        binding.containerView.visibility = View.VISIBLE
        binding.inheritateTitle.text = inheritateData.title
        binding.inheritateDetailDesc.text = inheritateData.text
        binding.inheritateDetailTopGridLayout.setData(inheritateData.desc)
        binding.inheritateSubTitle.text = "${viewModel.projectTitle.value}传承人"
        inheritateData.inheritate?.let { inheritatePeople ->
            if (inheritatePeople.isNotEmpty()) {
                binding.inheritateOthersView.visibility = View.VISIBLE
                binding.inheritateOthersView.setContent {
                    val projectName = viewModel.projectTitle.value ?: ""
                    ProjectDetaiInheritateViewCompose(
                        projectName = projectName,
                        datas = inheritatePeople,
                        Modifier.fillMaxWidth()
                    ) {
                        findNavController().navigate(
                            R.id.inheritate_detail_to_inheritate_detail,
                            bundleOf(DATA to it, PROJECT_TITLE to projectName)
                        )
                    }
                }
            }
        }

    }


}
