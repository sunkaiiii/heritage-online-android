package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(val repository: Repository):ViewModel() {

    private val projectDetailLink = MutableLiveData<String>()

    val projectDetail=Transformations.switchMap(projectDetailLink){link->
        repository.getProjectDetail(link)
    }

    fun loadProjectDetail(link:String){
        if(projectDetailLink.value == link){
            return
        }
        projectDetailLink.value = link
    }
}