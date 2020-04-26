package com.example.sunkai.heritage.entity.response

import com.example.sunkai.heritage.entity.response.nestedData.InheritatePeople
import com.example.sunkai.heritage.entity.response.nestedData.RelevantProject

data class ProjectDetailResponse(val title: String,
                            val link: String,
                            val text: String,
                            val desc: List<String>,
                            val ralevant: List<RelevantProject>,
                            val inheritate: List<InheritatePeople>)