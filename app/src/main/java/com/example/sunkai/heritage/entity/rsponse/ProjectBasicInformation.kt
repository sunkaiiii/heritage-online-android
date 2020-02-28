package com.example.sunkai.heritage.entity.rsponse

class ProjectBasicInformation(val title:String,
                              val content:String,
                              val numItem:List<ProjectBasicInfomationItem>
                              )
{
    class ProjectBasicInfomationItem(val num:String,val desc:String)
}