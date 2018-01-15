package com.example.sunkai.heritage.tools

import android.os.AsyncTask
import java.lang.ref.WeakReference

/*
 * Created by sunkai on 2018/1/2.
 */
abstract class BaseAsyncTask<T, U, V,W>(obj: W) : AsyncTask<T, U, V>() {
    val weakRefrece: WeakReference<W>

    init {
        weakRefrece = WeakReference(obj)
    }
}