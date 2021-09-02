package com.example.sunkai.heritage.fragment

import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.databinding.FragmentProjectStatisticsBinding
import com.example.sunkai.heritage.entity.ProjectStatisticsViewModel
import com.example.sunkai.heritage.entity.response.HeritageProjectStatisticsResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment

class ProjectStatisticsFragment:BaseViewBindingFragment<FragmentProjectStatisticsBinding>() {
    val viewModel by lazy { ViewModelProvider(this).get(ProjectStatisticsViewModel::class.java) }
    override fun getBindingClass(): Class<FragmentProjectStatisticsBinding> = FragmentProjectStatisticsBinding::class.java

    override fun initView() {
        viewModel.projectStatistics.observe(viewLifecycleOwner){
            setStatisticsDataIntoView(it)
        }
    }

    private fun setStatisticsDataIntoView(statisticsResponse: HeritageProjectStatisticsResponse?) {

    }
}