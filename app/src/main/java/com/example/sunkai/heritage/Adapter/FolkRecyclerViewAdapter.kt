package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseAsyncTask
import com.example.sunkai.heritage.value.HOST

/**
 * 民间页recyclerview的adapter
 * Created by sunkai on 2018/1/19.
 */
class FolkRecyclerViewAdapter(context: Context, datas: List<FolkDataLite>,glide: RequestManager) : BaseRecyclerAdapter<FolkRecyclerViewAdapter.Holder, FolkDataLite>(context,datas,glide) {


    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val listImage: ImageView
        val listTitle: TextView
        val listLocation: TextView
        val listDivide: TextView

        init {
            listImage = view.findViewById(R.id.list_img)
            listTitle = view.findViewById(R.id.list_title)
            listLocation = view.findViewById(R.id.list_location)
            listDivide = view.findViewById(R.id.list_divide)
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.folk_listview_layout, parent, false)
        val holder = Holder(view)
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = datas[position]
        setDataToView(data, holder)
    }


    private fun setDataToView(data: FolkDataLite, holder: Holder) {
        holder.listTitle.text = data.title
        holder.listDivide.text = data.divide
        holder.listLocation.text = data.category
        glide.load(HOST + data.img).into(holder.listImage)
    }

    fun startGetInformation() {
        //获取folk的文本信息
        datas = ArrayList()
        onPreLoad()
        getInformation(this).execute()
    }

    fun setNewDatas(setDatas: List<FolkDataLite>) {
        datas = setDatas
        notifyDataSetChanged()
    }

    fun getListDatas(): List<FolkDataLite> {
        return datas
    }

    fun setOnPageLoadListener(listener: OnPageLoaded) {
        this.mOnPagedListener = listener
    }


    private class getInformation(adapter: FolkRecyclerViewAdapter) : BaseAsyncTask<Void, Void, Void, FolkRecyclerViewAdapter>(adapter) {
        override fun doInBackground(vararg params: Void?): Void? {
            val getDatas = HandleFolk.GetFolkInforMation()
            getDatas?.let {
                val adapter = weakRefrece.get()
                adapter?.datas = getDatas
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            weakRefrece.get()?.onPostLoad()
            weakRefrece.get()?.notifyDataSetChanged()
        }
    }
}