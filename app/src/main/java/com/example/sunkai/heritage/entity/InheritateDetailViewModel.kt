package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InheritateDetailViewModel @Inject constructor(val repository: Repository):ViewModel() {
    private val link = MutableLiveData<String>()
    var projectTitle = MutableLiveData<String>()
    val inheritateDetail = projectTitle.switchMap{ link ->
        repository.getInheritanceDetail(link)
    }

    fun setInheritateDetailLink(link:String){
        this.link.value = link
    }
}