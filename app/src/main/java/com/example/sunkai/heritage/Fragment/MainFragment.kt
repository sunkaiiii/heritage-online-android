package com.example.sunkai.heritage.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.R


/**
 * 民间页的类
 */
class MainFragment : BaseLazyLoadFragment(),View.OnClickListener {
    private var folkAllInfors:List<FolkDataLite>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun startLoadInformation() {
    }


    override fun onClick(v: View) {
        when(v.id){
        }
    }



}
