package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(val repository: Repository) :ViewModel() {
    private val newsDetailLink = MutableLiveData<String>()
    val newsDetail = Transformations.switchMap(newsDetailLink){link->
        repository.getNewsDetail(link)
    }

    fun loadNewsDetail(link:String){
        newsDetailLink.value=link
    }
}