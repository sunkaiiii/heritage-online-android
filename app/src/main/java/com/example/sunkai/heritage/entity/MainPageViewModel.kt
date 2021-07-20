package com.example.sunkai.heritage.entity

import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainPageViewModel @Inject constructor(val repository: Repository): ViewModel() {
    val banner = repository.getBanner()
}