package com.example.sunkai.heritage.entity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class PeoplePageViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    fun peopleList() = repository.fetchPeopleListPageData().cachedIn(viewModelScope)

    val peopleTopBanner = repository.getPeopleTopBanner()
}