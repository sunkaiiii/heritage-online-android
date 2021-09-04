package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.entity.ProjectStatisticsViewModel
import com.example.sunkai.heritage.entity.response.HeritageProjectStatisticsResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.views.ProjectStatisticsByTime

class ProjectStatisticsFragment : BaseGlideFragment() {
    val viewModel by lazy { ViewModelProvider(requireActivity()).get(ProjectStatisticsViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        viewModel.projectStatistics.observe(viewLifecycleOwner) {
            view.apply {
                setContent {
                    StatisticsViews(it)
                }
            }
        }
        return view
    }

    @Composable
    fun StatisticsViews(list: HeritageProjectStatisticsResponse) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(12.dp)) {
            ProjectStatisticsByTime(list.statisticsByTime)
        }
    }
}