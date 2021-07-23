package com.example.sunkai.heritage.entity.request

class SearchRequest(var title: String? = null) : BaseQueryRequest() {
    var num: String? = null

    var type: String? = null
    var rx_time: String? = null
    var cate: String? = null
    var province: String? = null
    var unit: String? = null
    var page: Int = 1
}