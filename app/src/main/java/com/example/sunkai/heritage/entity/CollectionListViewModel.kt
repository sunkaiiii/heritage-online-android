package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.database.entities.Collection
import com.example.sunkai.heritage.logic.CollectionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectionListViewModel @Inject constructor(collectionHandler: CollectionHandler) :
    ViewModel() {
    val selectedIndex = MutableLiveData(0)
    val collectionListData = Transformations.switchMap(selectedIndex) { index ->
        when (index) {
            0 -> collectionHandler.getALlCollection()
            else -> collectionHandler.getCollectionByType(Collection.CollectionType.values()[index])
        }
    }
}