package com.example.sunkai.heritage.value

import android.util.Pair
import android.view.View
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.EHeritageApplication
import com.example.sunkai.heritage.tools.Utils
import kotlin.math.roundToInt

const val URL = "url"
const val TYPE_TEXT = "text"
const val DATA = "data"
const val SETTING = "setting"
const val TITLE = "title"
const val THEME_COLOR = "theme_color"
const val IMAGE_URL = "image_url"
const val IMAGE_POSITION = "image_position"
const val IMAGE_COMPRESS_URL = "image_compressed_url"
const val API = "api"
typealias TransitionPair = Pair<View, String>
const val CHANGE_THEME="change_theme"
val VERSION_NAME = {
    val pm = EHeritageApplication.instance.packageManager
    val pi = pm.getPackageInfo(EHeritageApplication.instance.packageName, 0)
    pi.versionName
}


const val THEME_MARSALA = "#964f4c"
const val THEME_CLASSIC_BLUE = "#0f4c81"
const val THEME_LIVING_CORAL = "#FF6F61"
const val THEME_ULTRA_VIOLET = "#5F4B8B"
const val THEME_ROSE_QUARTZ = "#f7cac9"
const val THEME_SERENITY = "#91a5d0"
const val THEME_SAND_DOLLAR = "#d7c3b1"
const val THEME_RADIANT_ORCHID = "#d753b9"
const val THEME_EMERALD = "#009473"
const val THEME_HONEYSUCKLE = "#d94f70"
const val THEME_TURQUOISE = "#45b5aa"
const val THEME_MIMOSA = "#f0c05a"
val THEME_COLOR_ARRAYS = arrayOf(THEME_MARSALA, THEME_CLASSIC_BLUE, THEME_LIVING_CORAL, THEME_ULTRA_VIOLET, THEME_ROSE_QUARTZ
        , THEME_SERENITY, THEME_SAND_DOLLAR, THEME_RADIANT_ORCHID, THEME_EMERALD, THEME_HONEYSUCKLE, THEME_TURQUOISE
        , THEME_MIMOSA)

//根据屏幕大小不同，Recycler的网格视图显示的效果不一样
//判断初始横竖屏，防止初始化值错误
val GRID_LAYOUT_DESTINY = if (Utils.isHorizontalScreenMode())
    ((Utils.getScreenWidth().toFloat() / Utils.getDpi()) - 1).roundToInt()
else
    ((Utils.getScreenHeight().toFloat() / Utils.getDpi()) - 1).roundToInt()

const val BaiduIPLocationUrl = "https://api.map.baidu.com/location/ip?ak=aXgRqP49PFjpWTdqwFEYmtxpzVsHHNwW&coor=bd09ll"

const val ERROR = "ERROR"
const val IHCHINA = "http://www.ihchina.cn"

val MAIN_PAGE_TABLAYOUT_TEXT = arrayListOf(EHeritageApplication.instance.getString(R.string.news_feed), EHeritageApplication.instance.getString(R.string.forums), EHeritageApplication.instance.getString(R.string.special_topic))

val PROJECT_FRAGMENT_TABLAYOUT_TEXT = arrayListOf("名录","统计")

const val NEWS_DETAIL_DATABASE="NewsDetail"


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