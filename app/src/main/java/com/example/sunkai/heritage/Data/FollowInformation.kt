package com.example.sunkai.heritage.Data

/**搜索用户的类
 * Created by sunkai on 2018/2/22.
 */

//因为后端没有适配otherper进入的关注列表，所以focusFocusID被迫使用var
class FollowInformation(val focusFansID:Int,
                        var focusFocusID:Int,
                        val userName:String,
                        var followEachother:Boolean=false,
                        var checked:Boolean=false)