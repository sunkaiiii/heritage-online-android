package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(val repository: Repository):ViewModel() {

    private val projectDetailLink = MutableLiveData<String>()

    val projectDetail=projectDetailLink.switchMap{link->
        repository.getProjectDetail(link)
    }

    fun loadProjectDetail(link:String){
        if(projectDetailLink.value == link){
            return
        }
        projectDetailLink.value = link
    }
}