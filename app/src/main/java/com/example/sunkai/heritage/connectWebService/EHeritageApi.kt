package com.example.sunkai.heritage.connectWebService

import com.example.sunkai.heritage.interfaces.MyEHeritageApi
import java.io.Serializable

enum class EHeritageApi constructor(val _name: String, val _url: String, val _type: RequestType) : MyEHeritageApi, Serializable {
    GetBanner("banner", "api/banner", RequestType.GET),
    GetProjectBasicInformation("projectInformaton", "api/HeritageProject/GetMainPage", RequestType.GET),
    GetHeritageProjectList("GetHeritageProjectList", "/api/HeritageProject/GetHeritageProjectList", RequestType.GET),
    GetProjectDetail("GetProjectDetail", "/api/HeritageProject/GetHeritageDetail", RequestType.GET),
    GetInheritateDetail("GetInheritateDetail", "/api/HeritageProject/GetInheritatePeople", RequestType.GET),
    GetPeopleMainPage("getPeopleMainPage", "/api/People/GetPeopleMainPage", RequestType.GET),
    GetPeopleList("peopleList", "/api/People/PeopleList", RequestType.GET),
    SearchProject("searchProject", "/api/HeritageProject/SearchHeritageProject", RequestType.GET),
    GetSearchCategory("searchCategory","/api/HeritageProject/GetSearchCategories",RequestType.GET),
    GetPeopleDetail("peopleDetail", "/api/People/GetPeopleDetail", RequestType.GET);

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
