package com.example.sunkai.heritage.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.sunkai.heritage.Activity.AllFolkInfoActivity
import com.example.sunkai.heritage.Adapter.FolkRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseAsyncTask
import com.example.sunkai.heritage.tools.MakeToast.toast
import kotlinx.android.synthetic.main.fragment_folk.*
import java.util.*


/**
 * 民间页的类
 */
class FolkFragment : BaseLazyLoadFragment(),View.OnClickListener {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_folk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        testView_test.setOnClickListener(this)
    }

    override fun startLoadInformation() {

    }





    override fun onClick(v: View) {
        when(v.id){
            R.id.testView_test->startActivity(Intent(activity,AllFolkInfoActivity::class.java))
        }
    }



}
