package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.entity.ProjectStatisticsViewModel
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment

class ProjectStatisticsFragment:BaseGlideFragment() {
    val viewModel by lazy { ViewModelProvider(requireActivity()).get(ProjectStatisticsViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.apply {
            setContent {
                Text("compose view text")
            }
        }
        return view
    }
}