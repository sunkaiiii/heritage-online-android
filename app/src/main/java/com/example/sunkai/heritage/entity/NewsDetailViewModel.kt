package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.logic.Repository

class NewsDetailViewModel:ViewModel() {
    private val newsDetailLink = MutableLiveData<String>()
    val newsDetail = Transformations.switchMap(newsDetailLink){link->
        Repository.getNewsDetail(link)
    }

    fun loadNewsDetail(link:String){
        newsDetailLink.value=link
    }
}