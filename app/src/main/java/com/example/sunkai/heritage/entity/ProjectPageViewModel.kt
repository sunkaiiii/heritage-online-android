package com.example.sunkai.heritage.entity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectPageViewModel @Inject constructor(val repository: Repository):ViewModel(){
    fun projectList()=repository.fetchProjectListPageData().cachedIn(viewModelScope)
    val projectBasicInformation=repository.getProjectBasicInformation()
}