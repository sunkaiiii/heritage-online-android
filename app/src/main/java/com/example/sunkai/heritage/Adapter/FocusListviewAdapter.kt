package com.example.sunkai.heritage.Adapter

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.FollowInformation
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.FOLLOW_EACHOTHER
import com.example.sunkai.heritage.value.IS_FOCUS
import com.example.sunkai.heritage.value.NO_USERID
import com.example.sunkai.heritage.value.UNFOCUS
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
(private val context: Activity, private var what: Int, datas: List<FollowInformation>) : BaseRecyclerAdapter<FocusListviewAdapter.Holder, FollowInformation>(datas) {

    private var onFocuschangeListener: OnFocusChangeListener? = null

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        private val userIntrodeuce: TextView
        val userImage: RoundedImageView
        val focusBtn: TextView
        val focusBtnLayout: LinearLayout
        val focusBtnImg: ImageView

        init {
            userName = view.findViewById(R.id.user_name)
            userIntrodeuce = view.findViewById(R.id.user_introduce)
            userImage = view.findViewById(R.id.user_head_image)
            focusBtn = view.findViewById(R.id.focus_btn)
            focusBtnLayout = view.findViewById(R.id.ll_focus_btn)
            focusBtnImg = view.findViewById(R.id.iv_focus_btn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.focus_listview_layout, parent, false)
        view.setOnClickListener(this)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        setDatas(holder, data)
        getUserImage(holder, data)
        setClick(holder, position, data)
    }

    private fun setDatas(holder: Holder, data: FollowInformation) {
        holder.userName.text = data.userName
        holder.focusBtnImg.setImageResource(if (data.checked) R.drawable.ic_remove_circle_outline_grey_500_24dp else R.drawable.ic_add_black_24dp)
        holder.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
        holder.focusBtnLayout.setBackgroundResource(if (data.checked) R.drawable.shape_button_already_focus else R.drawable.shape_button_unfocus)
        holder.focusBtn.setTextColor(ContextCompat.getColor(context, if (data.checked) R.color.midGrey else R.color.colorPrimary))
        if (data.followEachother) {
            holder.focusBtn.text = FOLLOW_EACHOTHER
        } else {
            if (data.checked) {
                holder.focusBtn.text = IS_FOCUS
            } else {
                holder.focusBtn.text = UNFOCUS
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
        holder.focusBtnLayout.setOnClickListener {
            val handleFocus = handleFocus(data, position, holder)
            holder.focusBtn.text = "操作中"
            holder.focusBtnLayout.isEnabled = false
            if (data.checked) {
                handleFocus.CancelFollow()
            } else {
                handleFocus.AddFollow()
            }
        }
    }


    internal inner class handleFocus(private var data: FollowInformation, private var position: Int, private var holder: Holder) {
        fun AddFollow() {
            ThreadPool.execute {
                val result = HandlePerson.AddFocus(data.focusFocusID, data.focusFansID)
                //因为是关注用户，重新运行查看互关进程，判断是否为互相关注
                val followEachOther = HandlePerson.CheckFollowEachother(data.focusFocusID, data.focusFansID)
                context.runOnUiThread {
                    if (result) {
                        //关注完成则执行回调，使其重新加载粉丝、关注数据
                        setItemState(position, holder, true, followEachOther)
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
                        setItemState(position, holder, false, false)
                    } else {
                        toast("操作失败，请稍后再试")
                    }
                }
            }
        }

        private fun setItemState(position: Int, holder: Holder, check: Boolean, followEachOther: Boolean) {
            onFocuschangeListener?.onFocusChange()
            val animation = AnimationUtils.loadAnimation(context, R.anim.focus_btn_animation)
            holder.focusBtnImg.startAnimation(animation)
            holder.focusBtnImg.setImageResource(if (check) R.drawable.ic_remove_circle_outline_grey_500_24dp else R.drawable.ic_add_black_24dp)
            holder.focusBtnLayout.setBackgroundResource(if (check) R.drawable.shape_button_already_focus else R.drawable.shape_button_unfocus)
            holder.focusBtnLayout.isEnabled = true
            holder.focusBtn.setTextColor(ContextCompat.getColor(context, if (check) R.color.midGrey else R.color.colorPrimary))
            holder.focusBtn.text = if (check) {
                if (followEachOther) FOLLOW_EACHOTHER else IS_FOCUS
            } else UNFOCUS
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
