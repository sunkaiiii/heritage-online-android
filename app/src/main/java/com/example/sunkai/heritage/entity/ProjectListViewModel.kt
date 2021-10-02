package com.example.sunkai.heritage.entity

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.sunkai.heritage.entity.request.SearchProjectRequest
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ProjectListViewModel @Inject constructor(val repository: Repository):ViewModel() {
    private val searchProjectRequest = MutableLiveData<SearchProjectRequest>()
    val projectList=Transformations.switchMap(searchProjectRequest) { request ->
        repository.fetchProjectListPageData(request).cachedIn(viewModelScope).asLiveData(
            Dispatchers.Main
        )
    }
    init {
        searchProject()
    }

    fun searchProject(keywords:String? = null, type:String? =null, year:Int? = null){
        searchProjectRequest.value = SearchProjectRequest(keywords,year,type)
    }

    val allProjectType = repository.getAllProjectType()
}