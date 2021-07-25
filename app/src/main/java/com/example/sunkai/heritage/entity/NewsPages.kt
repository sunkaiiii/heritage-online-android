package com.example.sunkai.heritage.entity

import com.example.sunkai.heritage.database.NewsDatabase
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.network.EHeritageApi
import retrofit2.Call
import java.io.Serializable
import kotlin.reflect.KFunction1

enum class NewsPages(val _name: String, val reqeustApi: KFunction1<Int, Call<List<NewsListResponse>>>, val detailApi: KFunction1<String, Call<NewsDetail>>, val newsListDaoName: NewsDatabase.NewsListDaoName) :
    Serializable {
    NewsPage("newsPage", EHeritageApi::getNewsList, EHeritageApi::getNewsDetail,
        NewsDatabase.NewsListDaoName.NEWS_LIST
    ),
    ForumsPage("forumsPage", EHeritageApi::getForumsList, EHeritageApi::getForumsDetail,
        NewsDatabase.NewsListDaoName.FORUMS_LIST
    ),
    SpecialTopicPage("specialTopicPage", EHeritageApi::getSpecialTopicList, EHeritageApi::getSpecialTopicDetail,
        NewsDatabase.NewsListDaoName.SPECIAL_TOPIC_LIST
    ),
}