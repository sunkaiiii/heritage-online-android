package com.example.sunkai.heritage.entity.response

class ProjectBasicInformation(val title:String,
                              val content:String,
                              val numItem:List<ProjectBasicInfomationItem>
                              )
{
    class ProjectBasicInfomationItem(val num:String,val desc:String)
}