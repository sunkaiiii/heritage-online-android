package com.example.sunkai.heritage.entity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.logic.Repository
import retrofit2.Call
import kotlin.reflect.KFunction1

class NewsListFactory(private val repository: Repository,private val listCaller: KFunction1<Int, Call<List<NewsListResponse>>>):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.constructors[0].newInstance(repository,listCaller) as T
    }
}