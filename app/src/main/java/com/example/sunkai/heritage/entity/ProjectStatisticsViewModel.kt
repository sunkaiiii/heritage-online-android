package com.example.sunkai.heritage.entity

import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectStatisticsViewModel @Inject constructor(val repository: Repository):ViewModel() {
    val projectStatistics = repository.getProjectStatistics()
}