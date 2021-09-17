package com.example.sunkai.heritage.entity.response

import com.example.sunkai.heritage.entity.response.nestedData.InheritatePeople
import com.example.sunkai.heritage.entity.response.nestedData.RelevantProject

data class ProjectDetailResponse(
    val title: String,
    val link: String,
    val text: String,
    val desc: List<String>,
    val ralevant: List<RelevantProject>,
    val inheritate: List<InheritatePeople>,
    val id: String,
    val autoID: String,
    val num: String,
    val type: String,
    val regType: String,
    val rxTime: String,
    val projectNum: String,
    val cate: String,
    val province: String,
    val city: String,
    val content: String,
    val playtype: String,
    val unit: String
)