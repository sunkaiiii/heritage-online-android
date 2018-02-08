package com.example.sunkai.heritage.Fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Adapter.MainNewsAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.MakeToast.toast
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * 民间页的类
 */
class MainFragment : android.support.v4.app.Fragment(),View.OnClickListener {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadSomeMainNews()
    }

    private fun loadSomeMainNews(){
        Thread{
            val news=HandleMainFragment.ReadMainNews()
            val activity=activity
            activity?.let{
                activity.runOnUiThread{
                    val adapter=MainNewsAdapter(activity,news)
                    mainFramgentNewsRecyclerView.layoutManager=LinearLayoutManager(activity)
                    mainFramgentNewsRecyclerView.adapter=adapter
                }
            }
        }.start()
    }


    override fun onClick(v: View) {
        when(v.id){
        }
    }



}
