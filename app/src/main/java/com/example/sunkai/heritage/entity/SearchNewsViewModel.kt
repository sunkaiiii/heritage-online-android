package com.example.sunkai.heritage.entity

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.sunkai.heritage.entity.request.SearchNewsRequest
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SearchNewsViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    private val searchParameter = MutableLiveData<SearchNewsRequest>()
    val searchNewsResult = Transformations.switchMap(searchParameter) { request ->
        repository.fetchSearchProjectData(request).cachedIn(viewModelScope)
            .asLiveData(Dispatchers.Main)
    }

    fun startSearchNews(keywords: String, year: String? = null) {
        val currentValue = searchParameter.value
        val newValue = SearchNewsRequest(keywords, year)
        currentValue?.let {
            if (it == newValue) {
                return
            }
        }
        searchParameter.value = newValue
    }
}