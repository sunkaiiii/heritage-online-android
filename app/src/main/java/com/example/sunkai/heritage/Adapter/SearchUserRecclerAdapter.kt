package com.example.sunkai.heritage.Adapter

import android.content.Context
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.tools.Views.FollowThemeEdgeRecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.SearchUserInfo
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.HandleAdapterItemClickClickUtils
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.ERROR
import com.example.sunkai.heritage.value.IS_FOCUS
import com.example.sunkai.heritage.value.UNFOCUS

/**
 * 搜索用户的RecyclerView的adapter
 * Created by sunkai on 2018/3/6.
 */
class SearchUserRecclerAdapter(context: Context, datas: List<SearchUserInfo>, glide: RequestManager) : BaseRecyclerAdapter<SearchUserRecclerAdapter.Holder, SearchUserInfo>(context, datas, glide) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.user_name)
        val userImage: ImageView = view.findViewById(R.id.user_head_image)
        val focusBtn: TextView = view.findViewById(R.id.focus_btn)
        val focusBtnLayout: LinearLayout = view.findViewById(R.id.ll_focus_btn)
        val focusBtnImg: ImageView = view.findViewById(R.id.iv_focus_btn)

    }

    private var onFocusChangeListener: OnFocusChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.focus_listview_layout, parent, false)
        view.setOnClickListener(this)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        setData(holder, data)
        getUserImage(holder, data, position)
        setBtnClick(holder, data)
    }

    private fun setData(holder: Holder, data: SearchUserInfo) {
        holder.focusBtnLayout.visibility = if (data.id == LoginActivity.userID) View.GONE else View.VISIBLE
        holder.userName.text = data.userName
        holder.focusBtnImg.setImageResource(if (data.checked) R.drawable.ic_remove_circle_outline_grey_500_24dp else R.drawable.ic_add_black_24dp)
        holder.focusBtnLayout.setBackgroundResource(if (data.checked) R.drawable.shape_button_already_focus else R.drawable.shape_button_unfocus)
        holder.focusBtn.setTextColor(ContextCompat.getColor(context, if (data.checked) R.color.midGrey else R.color.colorPrimary))
        holder.focusBtn.text = if (data.checked) IS_FOCUS else UNFOCUS
    }

    private fun getUserImage(holder: Holder, data: SearchUserInfo, position: Int) {
        ThreadPool.execute {
            val url = HandlePerson.GetUserImageURL(data.id) ?: return@execute
            if (!TextUtils.isEmpty(url) && url != ERROR) {
                runOnUiThread {
                    data.imageUrl = url
                    datas[position] = data
                    glide.load(url).into(holder.userImage)
                }
            }
        }
    }

    private fun setBtnClick(holder: Holder, data: SearchUserInfo) {
        holder.focusBtnLayout.setOnClickListener {
            holder.focusBtnLayout.isEnabled = false
            when (holder.focusBtn.text) {
                IS_FOCUS -> cancelFocus(holder, data)
                UNFOCUS -> addFocus(holder, data)
            }
        }
    }

    private fun cancelFocus(holder: Holder, data: SearchUserInfo) {
        ThreadPool.execute {
            val result = HandlePerson.CancelFocus(LoginActivity.userID, data.id)
            runOnUiThread {
                holder.focusBtnLayout.isEnabled = true
                if (result) {
                    setBtnState(holder, data, false)
                } else {
                    toast("出现问题，请稍后再试")
                }
            }
        }
    }

    private fun addFocus(holder: Holder, data: SearchUserInfo) {
        ThreadPool.execute {
            val result = HandlePerson.AddFocus(LoginActivity.userID, data.id)
            runOnUiThread {
                holder.focusBtnLayout.isEnabled = true
                if (result) {
                    setBtnState(holder, data, true)
                } else {
                    toast("出现问题，请稍后再试")
                }
            }
        }
    }

    private fun setBtnState(holder: Holder, data: SearchUserInfo, success: Boolean) {
        data.checked = success
        val animation = AnimationUtils.loadAnimation(context, R.anim.float_btn_translate)
        holder.focusBtnImg.startAnimation(animation)
        holder.focusBtnImg.setImageResource(if (success) R.drawable.ic_remove_circle_outline_grey_500_24dp else R.drawable.ic_add_black_24dp)
        holder.focusBtnLayout.setBackgroundResource(if (data.checked) R.drawable.shape_button_already_focus else R.drawable.shape_button_unfocus)
        holder.focusBtn.setTextColor(ContextCompat.getColor(context, if (data.checked) R.color.midGrey else R.color.colorPrimary))
        holder.focusBtn.text = if (success) IS_FOCUS else UNFOCUS
        val toastText = (if (success) IS_FOCUS else UNFOCUS) + "成功"
        toast(toastText)
        onFocusChangeListener?.onFocusChange()
    }

    fun setOnFocusChangeListener(listner: OnFocusChangeListener) {
        this.onFocusChangeListener = listner
    }

    override fun setItemClick() {
        HandleAdapterItemClickClickUtils.handlePersonAdapterItemClick(context, this)
    }

}