package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData

interface CollaborativeViewModel {
    val viewTranslationDistance: MutableLiveData<Float>

    val initialActionDownTranslationY: MutableLiveData<Float>

}