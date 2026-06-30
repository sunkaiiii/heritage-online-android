package com.example.sunkai.heritage.entity

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.sunkai.heritage.database.entities.SearchHistory
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SearchProjectViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    val searchCategory = repository.getSearchCategory()


    private val searchQuery = MutableLiveData<SearchRequest>()


    fun searchProject(queryString: String) {
        if (queryString == this.searchQuery.value?.title) {
            return
        }
        val searchQuery = SearchRequest(queryString)
        this.searchQuery.value = searchQuery
    }

    fun searchProject(query: SearchRequest) {
        this.searchQuery.value = query
    }

    val searchHistory = repository.getSearchHistory()

    fun addSearchResult(searchHistory: SearchHistory) {
        repository.addSearchHistory(searchHistory)
    }

    fun removeSearchResult(searchHistory: SearchHistory) {
        repository.removeSearchHistory(searchHistory)
    }

    val searchResult = searchQuery.switchMap { query ->
        repository.getSearchResult(query).cachedIn(viewModelScope)
            .asLiveData(Dispatchers.Main)
    }
}