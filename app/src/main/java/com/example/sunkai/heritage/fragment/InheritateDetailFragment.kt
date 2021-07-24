package com.example.sunkai.heritage.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.databinding.FragmentInheritateDetailBinding
import com.example.sunkai.heritage.entity.InheritateDetailViewModel
import com.example.sunkai.heritage.entity.response.InheritateDetailResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InheritateDetailFragment : BaseViewBindingFragment<FragmentInheritateDetailBinding>() {
    private val viewModel by lazy { ViewModelProvider(this).get(InheritateDetailViewModel::class.java) }

    override fun getBindingClass(): Class<FragmentInheritateDetailBinding> =
        FragmentInheritateDetailBinding::class.java


    override fun initView() {
        binding.toolbar.title = getString(R.string.inheritors)
        viewModel.inheritateDetail.observe(viewLifecycleOwner, {
            setDataToView(it)
        })
        val link = arguments?.getString(DATA) ?: return
        viewModel.setInheritateDetailLink(link)
    }

    private fun setDataToView(inheritateData: InheritateDetailResponse) {
        binding.progressBar.visibility = View.GONE
        binding.containerView.visibility = View.VISIBLE
        binding.inheritateTitle.text = inheritateData.title
        binding.inheritateDetailDesc.text = inheritateData.text
        binding.inheritateDetailTopGridLayout.setData(inheritateData.desc)
        inheritateData.inheritate?.let {
            if (it.isNotEmpty()) {
                binding.inheritateOthersView.visibility = View.VISIBLE
                binding.inheritateOthersView.setData(it)
            }
        }

    }


}
