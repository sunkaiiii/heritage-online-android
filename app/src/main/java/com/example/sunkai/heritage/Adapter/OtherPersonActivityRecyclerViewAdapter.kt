package com.example.sunkai.heritage.Adapter

import android.app.Activity
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.ERROR
import com.example.sunkai.heritage.value.RESULT_NULL
import com.example.sunkai.heritage.value.RESULT_OK
import java.lang.ref.WeakReference

/*
 * Created by sunkai on 2018/1/2.
 */
class OtherPersonActivityRecyclerViewAdapter(val activity:Activity,val userID: Int,datas:List<Int>) : BaseRecyclerAdapter<OtherPersonActivityRecyclerViewAdapter.ViewHolder,Int>(datas) {


    init {
        getUserIdInfo(userID)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView

        init {
            imageView = view.findViewById(R.id.iv_other_person_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(GlobalContext.instance).inflate(R.layout.other_person_view, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position)
        holder?.let {
            getImage(datas[position], holder.imageView)
        }

    }


    private fun getUserIdInfo(userID: Int) {
        GetUserInfoTask(userID, this).execute()
    }

    internal class GetUserInfoTask(val userID: Int, adapter: OtherPersonActivityRecyclerViewAdapter) : AsyncTask<Void, Void, Int>() {
        val weakRefrece: WeakReference<OtherPersonActivityRecyclerViewAdapter>

        init {
            weakRefrece = WeakReference(adapter)
        }

        override fun doInBackground(vararg params: Void?): Int {
            val adapter = weakRefrece.get()
            adapter?.let {
                val getDatas = HandleFind.GetUserCommentIdByUser(userID)
                adapter.datas = getDatas.toList()
                return RESULT_OK
            }
            return RESULT_NULL
        }

        override fun onPostExecute(result: Int) {
            val adapter = weakRefrece.get()
            when (result) {
                RESULT_OK -> adapter?.notifyDataSetChanged()
            }
        }
    }


    private fun getImage(imageID: Int, imageview: ImageView) {
        ThreadPool.execute{
            val url=HandleFind.GetUserCommentImageUrl(imageID)
            if(!TextUtils.isEmpty(url)&&url!= ERROR){
                activity.runOnUiThread {
                    Glide.with(activity).load(url).into(imageview)
                }
            }
        }
    }

}