package com.example.sunkai.heritage.Adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.OtherUsersActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.FollowInformation
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.NO_USERID
import com.makeramen.roundedimageview.RoundedImageView

/*
 * Created by sunkai on 2018-3-1.
 */

class FocusListviewAdapter
/**
 *
 * @param datas 关注、粉丝的数据
 * @param what  1为关注，2为粉丝3为查询页面
 */
(private val context: Activity, var what: Int, datas: List<FollowInformation>) : BaseRecyclerAdapter<FocusListviewAdapter.Holder, FollowInformation>(datas) {

    private var onFocuschangeListener: OnFocusChangeListener? = null

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val rl_focus_listview_layout: RelativeLayout
        val userName: TextView
        private val userIntrodeuce: TextView
        val userImage: RoundedImageView
        val focusBtn: Button

        init {
            userName = view.findViewById(R.id.user_name)
            userIntrodeuce = view.findViewById(R.id.user_introduce)
            userImage = view.findViewById(R.id.user_head_image)
            focusBtn = view.findViewById(R.id.focus_btn)
            rl_focus_listview_layout = view.findViewById(R.id.rl_focus_listview_layout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.focus_listview_layout, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        super.onBindViewHolder(holder, position)
        holder?.let {
            val data = getItem(position)
            setDatas(holder, data)
            getUserImage(holder, data)
            setClick(holder, position, data)
        }
    }

    private fun setDatas(holder: Holder, data: FollowInformation) {
        holder.userName.text = data.userName
        holder.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
        if (data.followEachother) {
            holder.focusBtn.text = "互相关注"
        } else {
            if (data.checked) {
                holder.focusBtn.text = "已关注"
            } else {
                holder.focusBtn.text = "未关注"
            }
        }
    }

    private fun getUserImage(holder: Holder, data: FollowInformation) {
        ThreadPool.execute {
            val id = data.focusFansID
            if (id != NO_USERID) {
                val url = HandlePerson.GetUserImageURL(id)
                url?.let {
                    context.runOnUiThread {
                        Glide.with(context).load(url).into(holder.userImage)
                    }
                }
            }
        }
    }

    private fun setClick(holder: Holder, position: Int, data: FollowInformation) {
//        在点击关注、取关的时候，页面文字改变，提示用户正在响应，并禁止按钮点击以防止错误的发生
        holder.focusBtn.setOnClickListener { _ ->
            val handleFocus = handleFocus(data, position, holder.focusBtn)
            holder.focusBtn.text = "操作中"
            holder.focusBtn.isEnabled = false
            if (data.checked) {
                handleFocus.CancelFollow()
            } else {
                handleFocus.AddFollow()
            }
        }
        holder.rl_focus_listview_layout.setOnClickListener({
            val intent = Intent(context, OtherUsersActivity::class.java)
            intent.putExtra("userID", data.focusFansID)
            context.startActivity(intent)
        })
    }


    internal inner class handleFocus(private var data: FollowInformation, private var position: Int, private var btn: Button) {
        fun AddFollow() {
            ThreadPool.execute {
                val result = HandlePerson.AddFocus(data.focusFocusID, data.focusFansID)
                //因为是关注用户，重新运行查看互关进程，判断是否为互相关注
                val followEachOther = HandlePerson.CheckFollowEachother(data.focusFocusID, data.focusFansID)
                context.runOnUiThread {
                    if (result) {
                        //关注完成则执行回调，使其重新加载粉丝、关注数据
                        setItemState(position, btn, true, followEachOther)
                    } else {
                        Toast.makeText(context, "操作失败，请稍后再试", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        fun CancelFollow() {
            ThreadPool.execute {
                val result = HandlePerson.CancelFocus(data.focusFocusID, data.focusFansID)
                context.runOnUiThread {
                    if (result) {
                        setItemState(position, btn, false, false)
                    } else {
                        toast("操作失败，请稍后再试")
                    }
                }
            }
        }

        private fun setItemState(position: Int, button: Button, check: Boolean, followEachOther: Boolean) {
            onFocuschangeListener?.onFocusChange()
            button.isEnabled = true
            button.text = if (check) {
                if (followEachOther) "互相关注" else "已关注"
            } else "未关注"
            datas[position].checked = check
            datas[position].followEachother = followEachOther
            val text = if (check) "关注成功" else "取消关注成功"
            toast(text)
        }
    }

    fun setOnFocusChangeListener(listener: OnFocusChangeListener) {
        this.onFocuschangeListener = listener
    }
}
