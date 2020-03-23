package com.example.sunkai.heritage.entity.response

class PeopleMainPageResponse(val table: List<PeopleBannerTableContent>) {
    class PeopleBannerTableContent(val img: String, val desc: String, val link: String)
}