package com.example.sunkai.heritage.entity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectStatisticsViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    val projectStatistics = repository.getProjectStatistics()
    private val _isExpandStatisticsByRegion = MutableLiveData(false)
    val isExpandStatisticsByRegion: LiveData<Boolean> = _isExpandStatisticsByRegion

    fun onExpandAreaClick() {
        _isExpandStatisticsByRegion.value = !(_isExpandStatisticsByRegion.value ?: false)
    }
}