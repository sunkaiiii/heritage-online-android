package com.example.sunkai.heritage.value

import android.content.res.Configuration
import com.example.sunkai.heritage.tools.GlobalContext

/*
 * Created by 70472 on 2018/1/1.
 */

const val NO_USER = "noUser"
const val NO_USERID = -1
const val RESULT_OK = 800
const val RESULT_NULL = -1
const val FOLLOW = 1
const val FANS = 2
const val OTHER_FOLLOW = 3
const val OTHER_FANS = 4
const val UPDATE_USER_COMMENT = 12
const val UPDATE_SUCCESS = 4
const val CHOOSE_PHOTO = 2
const val TAKE_PHOTO = 1
const val DENIALD = -1
const val ONLYFOCUS = 0
const val ALL = 1
const val ALL_COMMENT = 0
const val MY_FOCUS_COMMENT = 1
const val MINI_REPLY = -1
const val COMMENT_REPLY = 0
const val SIGN_OUT = 11
const val TYPE_TEXT = "text"
const val CATEGORY = "category"
const val STATE_CHANGE = 1
const val LOG_OUT = 2
const val IS_FOCUS = "已关注"
const val UNFOCUS = "加关注"
const val FOLLOW_EACHOTHER = "互相关注"
const val MODIFY_USER_COMMENT=1
const val TYPE_MAIN = "首页新闻"
const val TYPE_FOCUS_HERITAGE = "聚焦非遗"
const val TYPE_FOLK = "民间"
const val TYPE_FIND = "发现"

//根据屏幕大小不同，Recycler的网格视图显示的效果不一样
//判断初始横竖屏，防止初始化值错误
val GRID_LAYOUT_DESTINY = if (GlobalContext.instance.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
    Math.round((GlobalContext.instance.resources.displayMetrics.widthPixels.toFloat() / GlobalContext.instance.resources.displayMetrics.densityDpi) - 1)
else
    Math.round((GlobalContext.instance.resources.displayMetrics.heightPixels.toFloat() / GlobalContext.instance.resources.displayMetrics.densityDpi) - 1)

//const val BaiduIPLocationUrl = "https://api.map.baidu.com/location/ip?ak=aXgRqP49PFjpWTdqwFEYmtxpzVsHHNwW&coor=bd09ll"

const val SUCCESS = "SUCCESS"
const val ERROR = "ERROR"

const val HOST = "http://sunkai.xyz:8080"
const val HOST_IP="sunkai.xyz"
const val PUSH_PORT=8088

const val ALL_FOLK_INFO_ACTIVITY = "AllFolkInfoActivity"
const val ACTIVITY_FRAGMENT = "ActivityFragment"
const val SETTING_ACTIVITY = 100

val CLASIIFY_DIVIDE = arrayOf("表演艺术", "杂技与竞技", "文学与美术", "传统民俗")
val CLASSIFY_DIVIDE_TABVIEWSHOW= arrayOf("表演艺术", "杂技竞技", "文学美术", "传统民俗")
val CATEGORIES = arrayListOf("要闻", "中国特色", "传统村落", "特色小镇", "魅力中国", "非遗中国", "时代影像", "发现之旅", "一带一路", "民风民俗")