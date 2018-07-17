package com.example.sunkai.heritage.value

import android.content.res.Configuration
import android.util.Pair
import android.view.View
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.CreateMyCollectAdapterUtils.CreateAllNewsCollectAdapter
import com.example.sunkai.heritage.tools.CreateMyCollectAdapterUtils.CreateBottomNewsCollectAdapter
import com.example.sunkai.heritage.tools.CreateMyCollectAdapterUtils.CreateCommentCollectaAdapter
import com.example.sunkai.heritage.tools.CreateMyCollectAdapterUtils.CreateFolkCollectionAdapter
import com.example.sunkai.heritage.tools.CreateSeachActivityAdapterUtils.*
import com.example.sunkai.heritage.tools.GlobalContext

/**
 * 这个文件包含了项目用到的常量
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
const val DENIALD = -1
const val ONLYFOCUS = 0
const val ALL = 1
const val ALL_COMMENT = 0
const val MY_FOCUS_COMMENT = 1
const val SAME_LOCATION=2
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
const val MODIFY_USER_COMMENT = 1
const val TYPE_MAIN = "首页新闻"
const val TYPE_FOCUS_HERITAGE = "聚焦非遗"
const val TYPE_FOLK = "民间"
const val TYPE_FIND = "发现"
val COLLECT_TYPE_MAIN=CreateAllNewsCollectAdapter::class.java.name!!
val COLLECT_TYPE_FOCUS_HERITAGE=CreateBottomNewsCollectAdapter::class.java.name!!
val COLLECT_TYPE_FOLK=CreateFolkCollectionAdapter::class.java.name!!
val COLLECT_TYPE_FIND=CreateCommentCollectaAdapter::class.java.name!!
const val FIRST_OPEN = 0
const val NOT_FIRST_OPEN = 1
const val NOT_LOGIN = 0
const val FROM_WELCOME = 0
const val FROM_REGIST = 0
const val INFORMATION = "information"
const val FROM_FANS = "fans"
const val FROM_FOCUS = "focus"
const val USER_ID = "userID"
const val FROM = "from"
const val DATA = "data"
const val ACTIVITY = "activity"
const val USER_NAME = "userName"
const val PASSWORD = "passWord"
const val PUSH_SWITCH = "pushSwitch"
const val ID = "id"
const val OPTION = "option"
const val SETTING = "setting"
const val START_COUNT = "startCount"
const val SHARE_PREFRENCE_USERNAME = "user_name"
const val SHARE_PREFRENCE_PASSWORD = "user_password"
const val TITLE = "title"
const val IMAGE = "image"
const val IS_INTO = "isInto"
const val READ = "r"
const val SEARCH_TYPE="searchType"
const val THEME_COLOR="theme_color"
val TYPE_NEWS=CreateSearchAllNewsSearchAdapter::class.java.name!!
val TYPE_BOTTOM_NEWS=CreateBottomActivitySearchAdapter::class.java.name!!
val TYPE_COMMENT=CreateSearchCommentAdapter::class.java.name!!
val TYPE_FOLK_HERITAGE=CreateFolkInfoSearchAdapter::class.java.name!!
val TYPE_USER=CreateSearchUserAdapter::class.java.name!!
const val SEARCH_SHAREPREF_NAME="search_info"
const val IMAGE_URL="image_url"
typealias TransitionPair= Pair<View, String>
const val THEME_COLOR_PUEPLE="#850091"
const val THEME_COLOR_GREEN="#26a69a"
const val THEME_COLOR_PINK="#b30062"
const val THEME_SUNLIGHT_ORANGE="#ff7043"
const val THEME_HIGHLEVEL_GREY="#9e9e9e"
const val THEME_PEACE_BLUE_GREY="#607d8b"
const val THEME_WATER_PINK="#f8bbd0"
const val THEME_WATER_ORANGE="#ffab91"
const val THEME_LIGHT_BLUE="#b3e5fc"
const val THEME_ZHIHU_BLUE="#1565c0"
const val THEME_METAL_BLACK="#263238"
const val THEME_ROCK_BLACK="#212121"
val THEME_COLOR_ARRAYS= arrayOf(THEME_COLOR_PUEPLE, THEME_COLOR_GREEN, THEME_COLOR_PINK,THEME_SUNLIGHT_ORANGE
        ,THEME_HIGHLEVEL_GREY,THEME_PEACE_BLUE_GREY,THEME_WATER_PINK,THEME_WATER_ORANGE,THEME_LIGHT_BLUE,THEME_ZHIHU_BLUE
        ,THEME_METAL_BLACK,THEME_ROCK_BLACK)
//根据屏幕大小不同，Recycler的网格视图显示的效果不一样
//判断初始横竖屏，防止初始化值错误
val GRID_LAYOUT_DESTINY = if (GlobalContext.instance.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
    Math.round((GlobalContext.instance.resources.displayMetrics.widthPixels.toFloat() / GlobalContext.instance.resources.displayMetrics.densityDpi) - 1)
else
    Math.round((GlobalContext.instance.resources.displayMetrics.heightPixels.toFloat() / GlobalContext.instance.resources.displayMetrics.densityDpi) - 1)

const val BaiduIPLocationUrl = "https://api.map.baidu.com/location/ip?ak=aXgRqP49PFjpWTdqwFEYmtxpzVsHHNwW&coor=bd09ll"

const val SUCCESS = "SUCCESS"
const val ERROR = "ERROR"

const val HOST = "https://sunkai.xyz:8081"
const val HOST_IP = "sunkai.xyz"
const val PUSH_PORT = 8088

const val ALL_FOLK_INFO_ACTIVITY = "AllFolkInfoActivity"
const val ACTIVITY_FRAGMENT = "ActivityFragment"
const val SETTING_ACTIVITY = 100

val CLASIIFY_DIVIDE = arrayOf("表演艺术", "杂技与竞技", "文学与美术", "传统民俗")
val CLASSIFY_DIVIDE_TABVIEWSHOW = arrayOf("表演艺术", "杂技竞技", "文学美术", "传统民俗")
val CATEGORIES = arrayOf("要闻", "中国特色", "传统村落", "特色小镇", "魅力中国", "非遗中国", "时代影像", "发现之旅", "一带一路", "民风民俗")
val MAIN_PAGE_TABLAYOUT_TEXT = arrayListOf(GlobalContext.instance.getString(R.string.focus_heritage), GlobalContext.instance.getString(R.string.all_news))
val MAIN_PAGE_CATEGORY_NEWS_IMAGE = arrayOf("yao_wen.jpg"
        , "zhongguo_tese.jpg"
        ,"chuantong_cuoluo.jpg"
        , "tese_xiaozhen.jpg"
        , "meili_china.jpg"
        , "feiyi_china.jpg"
        , "shidai_yingxiang.jpg"
        , "faxian_zhilv.jpg"
        , "yidai_yilu.jpg"
        , "min_feng.jpg")
const val GLIDE_LICENCE = "License for everything not in third_party and not otherwise marked:\n" +
        "\n" +
        "Copyright 2014 Google, Inc. All rights reserved.\n" +
        "\n" +
        "Redistribution and use in source and binary forms, with or without modification, are\n" +
        "permitted provided that the following conditions are met:\n" +
        "\n" +
        "   1. Redistributions of source code must retain the above copyright notice, this list of\n" +
        "         conditions and the following disclaimer.\n" +
        "\n" +
        "   2. Redistributions in binary form must reproduce the above copyright notice, this list\n" +
        "         of conditions and the following disclaimer in the documentation and/or other materials\n" +
        "         provided with the distribution.\n" +
        "\n" +
        "THIS SOFTWARE IS PROVIDED BY GOOGLE, INC. ``AS IS'' AND ANY EXPRESS OR IMPLIED\n" +
        "WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND\n" +
        "FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GOOGLE, INC. OR\n" +
        "CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR\n" +
        "CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR\n" +
        "SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON\n" +
        "ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING\n" +
        "NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF\n" +
        "ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n" +
        "\n" +
        "The views and conclusions contained in the software and documentation are those of the\n" +
        "authors and should not be interpreted as representing official policies, either expressed\n" +
        "or implied, of Google, Inc.\n" +
        "---------------------------------------------------------------------------------------------\n" +
        "License for third_party/disklrucache:\n" +
        "\n" +
        "Copyright 2012 Jake Wharton\n" +
        "Copyright 2011 The Android Open Source Project\n" +
        "\n" +
        "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
        "you may not use this file except in compliance with the License.\n" +
        "You may obtain a copy of the License at\n" +
        "\n" +
        "   http://www.apache.org/licenses/LICENSE-2.0\n" +
        "\n" +
        "Unless required by applicable law or agreed to in writing, software\n" +
        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
        "See the License for the specific language governing permissions and\n" +
        "limitations under the License.\n" +
        "---------------------------------------------------------------------------------------------\n" +
        "License for third_party/gif_decoder:\n" +
        "\n" +
        "Copyright (c) 2013 Xcellent Creations, Inc.\n" +
        "\n" +
        "Permission is hereby granted, free of charge, to any person obtaining\n" +
        "a copy of this software and associated documentation files (the\n" +
        "\"Software\"), to deal in the Software without restriction, including\n" +
        "without limitation the rights to use, copy, modify, merge, publish,\n" +
        "distribute, sublicense, and/or sell copies of the Software, and to\n" +
        "permit persons to whom the Software is furnished to do so, subject to\n" +
        "the following conditions:\n" +
        "\n" +
        "The above copyright notice and this permission notice shall be\n" +
        "included in all copies or substantial portions of the Software.\n" +
        "\n" +
        "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND,\n" +
        "EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF\n" +
        "MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND\n" +
        "NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE\n" +
        "LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION\n" +
        "OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION\n" +
        "WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.\n" +
        "---------------------------------------------------------------------------------------------\n" +
        "License for third_party/gif_encoder/AnimatedGifEncoder.java and\n" +
        "third_party/gif_encoder/LZWEncoder.java:\n" +
        "\n" +
        "No copyright asserted on the source code of this class. May be used for any\n" +
        "purpose, however, refer to the Unisys LZW patent for restrictions on use of\n" +
        "the associated LZWEncoder class. Please forward any corrections to\n" +
        "kweiner@fmsware.com.\n" +
        "\n" +
        "-----------------------------------------------------------------------------\n" +
        "License for third_party/gif_encoder/NeuQuant.java\n" +
        "\n" +
        "Copyright (c) 1994 Anthony Dekker\n" +
        "\n" +
        "NEUQUANT Neural-Net quantization algorithm by Anthony Dekker, 1994. See\n" +
        "\"Kohonen neural networks for optimal colour quantization\" in \"Network:\n" +
        "Computation in Neural Systems\" Vol. 5 (1994) pp 351-367. for a discussion of\n" +
        "the algorithm.\n" +
        "\n" +
        "Any party obtaining a copy of these files from the author, directly or\n" +
        "indirectly, is granted, free of charge, a full and unrestricted irrevocable,\n" +
        "world-wide, paid up, royalty-free, nonexclusive right and license to deal in\n" +
        "this software and documentation files (the \"Software\"), including without\n" +
        "limitation the rights to use, copy, modify, merge, publish, distribute,\n" +
        "sublicense, and/or sell copies of the Software, and to permit persons who\n" +
        "receive copies from any such party to do so, with the only requirement being\n" +
        "that this copyright notice remain intact."