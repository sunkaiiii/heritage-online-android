package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CollaborativeViewModelImpl:CollaborativeViewModel,ViewModel() {
    override val viewTranslationDistance: MutableLiveData<Float> = MutableLiveData()

    override val initialActionDownTranslationY: MutableLiveData<Float> = MutableLiveData()

}