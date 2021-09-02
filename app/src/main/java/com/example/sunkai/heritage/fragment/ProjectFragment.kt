package com.example.sunkai.heritage.fragment

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.ProjectFragmentViewPagerAdapter
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.databinding.FragmentProjectBinding
import com.example.sunkai.heritage.dialog.FragmentProjectContentDialog
import com.example.sunkai.heritage.entity.ProjectPageViewModel
import com.example.sunkai.heritage.entity.response.ProjectBasicInformation
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.tools.*
import com.example.sunkai.heritage.value.DATA
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.sunkai.heritage.value.PROJECT_FRAGMENT_TABLAYOUT_TEXT

@AndroidEntryPoint
class ProjectFragment : BaseViewBindingFragment<FragmentProjectBinding>() {

    private val projectViewModel by lazy { ViewModelProvider(requireActivity()).get(ProjectPageViewModel::class.java) }

    override fun getBindingClass(): Class<FragmentProjectBinding> =
        FragmentProjectBinding::class.java


    override fun initView() {
        projectViewModel.projectBasicInformation.observe(viewLifecycleOwner, {
            fillProjectBasicDataIntoView(it)
        })
        val adapter = ProjectFragmentViewPagerAdapter(this)
        binding.fragmentProjectViewPager.adapter = adapter
        TabLayoutMediator(binding.fragmentProjectTablayout,binding.fragmentProjectViewPager){tab,position ->
            tab.text = PROJECT_FRAGMENT_TABLAYOUT_TEXT[position]
        }.attach()
    }

    private fun fillProjectBasicDataIntoView(projectInformation: ProjectBasicInformation) {
        binding.projectDescLoading.visibility = View.GONE
        binding.projectPageTitle.text = projectInformation.title
        binding.projectFragmentShowContent.setOnClickListener {
            val dialog = FragmentProjectContentDialog(
                context
                    ?: return@setOnClickListener, projectInformation
            )
            dialog.show()
            dialog.window?.setLayout(
                Utils.getScreenWidth(),
                (Utils.getScreenHeight() * 0.7).toInt()
            )
        }
    }
}