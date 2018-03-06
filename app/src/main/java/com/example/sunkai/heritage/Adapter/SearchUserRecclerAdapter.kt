package com.example.sunkai.heritage.Adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.SearchUserInfo
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.ERROR
import com.example.sunkai.heritage.value.IS_FOCUS
import com.example.sunkai.heritage.value.UNFOCUS
import com.makeramen.roundedimageview.RoundedImageView

/**
 * 搜索用户的RecyclerView的adapter
 * Created by sunkai on 2018/3/6.
 */
class SearchUserRecclerAdapter(val context: Activity, datas: List<SearchUserInfo>) : BaseRecyclerAdapter<SearchUserRecclerAdapter.Holder, SearchUserInfo>(datas) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val userImage: RoundedImageView
        val focusBtn: Button

        init {
            userName = view.findViewById(R.id.user_name)
            userImage = view.findViewById(R.id.user_head_image)
            focusBtn = view.findViewById(R.id.focus_btn)
        }
    }

    private var onFocusChangeListener:OnFocusChangeListener?=null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.focus_listview_layout, parent, false)
        view.setOnClickListener(this)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        super.onBindViewHolder(holder, position)
        holder?.let {
            val data = getItem(position)
            setData(holder, data)
            getUserImage(holder, data)
            setBtnClick(holder,data)
        }
    }

    private fun setData(holder: Holder, data: SearchUserInfo) {
        holder.userName.text = data.userName
        holder.focusBtn.text = if (data.checked) IS_FOCUS else UNFOCUS
    }

    private fun getUserImage(holder: Holder, data: SearchUserInfo) {
        ThreadPool.execute {
            val url = HandlePerson.GetUserImageURL(data.id) ?: return@execute
            if (!TextUtils.isEmpty(url) && url != ERROR) {
                context.runOnUiThread {
                    Glide.with(context).load(url).into(holder.userImage)
                }
            }
        }
    }

    private fun setBtnClick(holder: Holder,data: SearchUserInfo){
        holder.focusBtn.setOnClickListener {
            holder.focusBtn.isEnabled=false
            when(holder.focusBtn.text){
                IS_FOCUS->cancelFocus(holder,data)
                UNFOCUS->addFocus(holder,data)
            }
        }
    }

    private fun cancelFocus(holder: Holder,data: SearchUserInfo){
        ThreadPool.execute {
            val result=HandlePerson.CancelFocus(LoginActivity.userID,data.id)
            context.runOnUiThread {
                holder.focusBtn.isEnabled=true
                if (result){
                    setBtnState(holder,data,false)
                }else{
                    toast("出现问题，请稍后再试")
                }
            }
        }
    }

    private fun addFocus(holder: Holder,data: SearchUserInfo){
        ThreadPool.execute {
            val result=HandlePerson.AddFocus(LoginActivity.userID,data.id)
            context.runOnUiThread {
                holder.focusBtn.isEnabled=true
                if(result){
                    setBtnState(holder,data,true)
                }
            }
        }
    }

    private fun setBtnState(holder: Holder,data: SearchUserInfo,success:Boolean){
        data.checked=success
        holder.focusBtn.text=if(success) IS_FOCUS else UNFOCUS
        onFocusChangeListener?.onFocusChange()
    }

    fun setOnFocusChangeListener(listner:OnFocusChangeListener){
        this.onFocusChangeListener=listner
    }

}