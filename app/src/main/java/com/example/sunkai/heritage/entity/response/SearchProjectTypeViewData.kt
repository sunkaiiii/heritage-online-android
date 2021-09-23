package com.example.sunkai.heritage.entity.response

import java.util.*
import kotlin.collections.ArrayList

data class SearchProjectTypeViewData(val projectTypes: List<String>) {
    private val _projectYear: List<String>
    val projectYear get() = _projectYear
    init {
        val list = ArrayList<String>()
        for (index in 2000..Calendar.getInstance().get(Calendar.YEAR)) {
            list.add(index.toString())
        }
        _projectYear = list
    }
}

data class SearchProjectTypeResponse(val projectTypes: List<String>)

