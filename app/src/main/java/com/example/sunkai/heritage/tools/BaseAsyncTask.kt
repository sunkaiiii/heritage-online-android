package com.example.sunkai.heritage.tools

import android.os.AsyncTask
import java.lang.ref.WeakReference

/*
 * Created by sunkai on 2018/1/2.
 */
abstract class BaseAsyncTask<T, U, V>(obj: Any) : AsyncTask<T, U, V>() {
    val weakRefrece: WeakReference<Any>

    init {
        weakRefrece = WeakReference(obj)
    }
}