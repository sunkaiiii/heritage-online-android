package com.example.sunkai.heritage.entity

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class PeoplePageViewModel @Inject constructor(val repository: Repository) : ViewModel(),CollaborativeViewModel {
    val peopleList = repository.fetchPeopleListPageData().cachedIn(viewModelScope).asLiveData(Dispatchers.Main)

    val peopleTopBanner = repository.getPeopleTopBanner()

    override val viewTranslationDistance: MutableLiveData<Float> = MutableLiveData()

    override val initialActionDownTranslationY: MutableLiveData<Float> = MutableLiveData()
}