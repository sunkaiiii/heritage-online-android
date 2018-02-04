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
import com.example.sunkai.heritage.Adapter.FolkFragmentAllInfoAdapter
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
    private var folkAllInfors:List<FolkDataLite>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_folk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_folk_see_all.setOnClickListener(this)
    }

    override fun startLoadInformation() {
        getFolkChanelInformation.start()
    }

    private val getFolkChanelInformation= Thread {
        folkAllInfors=HandleFolk.GetFolkInforMation()
        activity?.runOnUiThread {
            val datas=folkAllInfors
            datas?.let {
                val adpater = FolkFragmentAllInfoAdapter(datas,activity!!)
                fragment_folk_allinfo_viewpager.adapter=adpater
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.fragment_folk_see_all->startActivity(Intent(activity,AllFolkInfoActivity::class.java))
        }
    }



}
