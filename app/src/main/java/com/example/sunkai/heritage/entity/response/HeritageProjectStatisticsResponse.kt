package com.example.sunkai.heritage.entity.response

data class HeritageProjectStatisticsResponse(
    val statisticsByRegion: List<HeritageProjectStatisticsItem>,
    val statisticsByTime: List<HeritageProjectStatisticsItem>,
    val statisticsByType: List<HeritageProjectStatisticsItem>
)

data class HeritageProjectStatisticsItem(
    val name: String,
    val value: Long
)
