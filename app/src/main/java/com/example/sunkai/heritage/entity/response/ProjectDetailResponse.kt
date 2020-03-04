package com.example.sunkai.heritage.entity.response

class ProjectDetailResponse(val title: String,
                            val link: String,
                            val text: String,
                            val desc: List<String>,
                            val ralevant: List<RelevantProject>,
                            val inheritate: List<InheritatePeople>) {
    class InheritatePeople(val link: String,
                           val content: List<ProjectDetailContent>)

    class RelevantProject(val link: String, val content: List<ProjectDetailContent>)

    class ProjectDetailContent(val key: String, val value: String)
}