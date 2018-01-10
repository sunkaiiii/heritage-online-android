package com.example.sunkai.heritage.tools

import android.view.animation.AlphaAnimation

/**
 * Created by sunkai on 2018/1/10.
 */

val inAnimation by lazy {
    AlphaAnimation(0f, 1f)
}
val outAnimation by lazy {
    AlphaAnimation(1f, 0f)
}