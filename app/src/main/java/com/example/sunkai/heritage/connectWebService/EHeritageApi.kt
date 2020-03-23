package com.example.sunkai.heritage.connectWebService

import com.example.sunkai.heritage.interfaces.MyEHeritageApi
import java.io.Serializable

//enum class Code private constructor(private val percentage: Int) {
//    NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
//
//    private val random = Random(System.currentTimeMillis())
//}

enum class EHeritageApi constructor(val _name: String, val _url: String, val _type: RequestType) : MyEHeritageApi,Serializable {
    GetNewsList("newsList", "api/NewsList", RequestType.GET),
    GetBanner("banner", "api/banner", RequestType.GET),
    GetNewsDetail("newsDetail","api/NewsDetail",RequestType.GET),
    GetProjectBasicInformation("projectInformaton","api/HeritageProject/GetMainPage",RequestType.GET),
    GetHeritageProjectList("GetHeritageProjectList","/api/HeritageProject/GetHeritageProjectList",RequestType.GET),
    GetProjectDetail("GetProjectDetail","/api/HeritageProject/GetHeritageDetail",RequestType.GET),
    GetInheritateDetail("GetInheritateDetail","/api/HeritageProject/GetInheritatePeople",RequestType.GET),
    GetForumsList("forumsList","api/Forums/ForumsList",RequestType.GET),
    GetForumsDetail("forumsDetail","/api/Forums/GetForumsDetail",RequestType.GET),
    GetSpecialTopic("specialTopic","/api/SpecialTopic/GetSpecialTopicList",RequestType.GET),
    GetSpecialTopicDetail("specialTopicDetail","/api/SpecialTopic/GetSpecialTopicDetail",RequestType.GET),
    GetPeopleMainPage("getPeopleMainPage","/api/People/GetPeopleMainPage",RequestType.GET),
    GetPeopleList("peopleList","/api/People/PeopleList",RequestType.GET),
    GetPeopleDetail("peopleDetail","/api/People/GetPeopleDetail",RequestType.GET);
    override fun getRequestName(): String {
        return _name
    }

    override fun getUrl(): String {
        return _url
    }

    override fun getRequestType(): RequestType {
        return _type
    }

}
