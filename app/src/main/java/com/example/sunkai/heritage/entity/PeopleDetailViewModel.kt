package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PeopleDetailViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    private val link = MutableLiveData<String>()

    val peopleDetail = Transformations.switchMap(link) { link ->
        repository.getPeopleDetail(link)
    }

    fun setLink(link: String) {
        this.link.value = link
    }
}