package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.adapter.MainPageViewPagerAdapter
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainPageViewModel @Inject constructor(val repository: Repository): ViewModel(),CollaborativeViewModel {
    val banner = repository.getBanner()
    val offsetPercentage = MutableLiveData<Float>()
    override val viewTranslationDistance: MutableLiveData<Float> = MutableLiveData()
    override val initialActionDownTranslationY: MutableLiveData<Float> = MutableLiveData()
}