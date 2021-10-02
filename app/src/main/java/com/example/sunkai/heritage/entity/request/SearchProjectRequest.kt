package com.example.sunkai.heritage.entity.request

data class SearchProjectRequest(
    val keywords: String? = null,
    val year: Int? = null,
    val type: String? = null
)