package com.example.sunkai.heritage.entity

import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.logic.Repository
import retrofit2.Call
import kotlin.reflect.KFunction1

class SpecialTopicViewModel(repository: Repository,listCaller: KFunction1<Int, Call<List<NewsListResponse>>>) : NewsListViewModel(repository,listCaller) {
}