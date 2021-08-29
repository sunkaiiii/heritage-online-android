package com.example.sunkai.heritage.fragment

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.databinding.FragmentProjectBinding
import com.example.sunkai.heritage.dialog.FragmentProjectContentDialog
import com.example.sunkai.heritage.entity.ProjectPageViewModel
import com.example.sunkai.heritage.entity.response.ProjectBasicInformation
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.tools.*
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProjectFragment : BaseViewBindingFragment<FragmentProjectBinding>() {

    private val projectViewModel by lazy { ViewModelProvider(requireActivity()).get(ProjectPageViewModel::class.java) }

    override fun getBindingClass(): Class<FragmentProjectBinding> =
        FragmentProjectBinding::class.java


    override fun initView() {

        projectViewModel.projectBasicInformation.observe(viewLifecycleOwner, {
            fillProjectBasicDataIntoView(it)
        })
        val adapter = ProjectInformationAdapter()
        adapter.setProjectItemClickListener { _, projectListInformation ->
            findNavController().navigate(
                R.id.project_list_to_project_detail,
                bundleOf(DATA to projectListInformation.link)
            )
        }
        binding.projectInformationList.adapter = adapter

        projectViewModel.projectList.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })
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