package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData

interface CollaborativeViewModel {
    val viewTranslationDistance: MutableLiveData<Float>
        get() = MutableLiveData()
    val initialActionDownTranslationY: MutableLiveData<Float>
        get() = MutableLiveData()
}