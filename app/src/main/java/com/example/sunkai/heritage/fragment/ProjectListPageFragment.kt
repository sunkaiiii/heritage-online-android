package com.example.sunkai.heritage.fragment

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.databinding.FragmentProjectListPageBinding
import com.example.sunkai.heritage.entity.ProjectListViewModel
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.value.DATA
import kotlinx.coroutines.launch

class ProjectListPageFragment:BaseViewBindingFragment<FragmentProjectListPageBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(ProjectListViewModel::class.java) }
    override fun getBindingClass(): Class<FragmentProjectListPageBinding> = FragmentProjectListPageBinding::class.java

    override fun initView() {
        val adapter = ProjectInformationAdapter()
        adapter.setProjectItemClickListener { _, projectListInformation ->
            findNavController().navigate(
                R.id.project_list_to_project_detail,
                bundleOf(DATA to projectListInformation.link)
            )
        }
        binding.projectInformationList.adapter = adapter

        viewModel.projectList.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })
    }
}