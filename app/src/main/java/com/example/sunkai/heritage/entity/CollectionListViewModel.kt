package com.example.sunkai.heritage.entity

import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.logic.CollectionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectionListViewModel @Inject constructor(collectionHandler: CollectionHandler) :ViewModel() {
    val collectionListData = collectionHandler.getALlCollection()
}