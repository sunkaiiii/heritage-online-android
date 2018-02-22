package com.example.sunkai.heritage.Adapter

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.*
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew.Companion.ERROR
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew.Companion.SUCCESS
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.MY_FOCUS_COMMENT
import com.makeramen.roundedimageview.RoundedImageView
import org.kobjects.base64.Base64
import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference


/**
 * 发现页第一屏recyclerView的adapter
 * Created by sunkai on 2017/12/22.
 */

class FindFragmentRecyclerViewAdapter(private val context: Activity, datas: List<UserCommentData>, private var what: Int) : BaseRecyclerAdapter<FindFragmentRecyclerViewAdapter.ViewHolder, UserCommentData>(datas) {
    private var recyclerView: RecyclerView? = null
    private var lruCache: LruCache<Int, Bitmap>

    class ViewHolder internal constructor(var view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView
        val like: TextView
        val dislike: TextView
        val comment: TextView
        val addfocusText: TextView
        val cancelFocusText: TextView
        val name_text: TextView
        val userImage: RoundedImageView

        init {
            img = view.findViewById(R.id.fragment_find_litview_img)
            comment = view.findViewById(R.id.testview_comment)
            like = view.findViewById(R.id.textview_like)
            dislike = view.findViewById(R.id.textview_dislike)
            addfocusText = view.findViewById(R.id.add_focus_text)
            name_text = view.findViewById(R.id.name_text)
            userImage = view.findViewById(R.id.user_list_image)
            cancelFocusText = view.findViewById(R.id.cancel_focus_text)
        }
    }

    init {
        val avilableMemory = Runtime.getRuntime().maxMemory().toInt() / 8
        val cacheSzie = avilableMemory / 4
        lruCache = object : LruCache<Int, Bitmap>(cacheSzie) {
            override fun sizeOf(key: Int?, value: Bitmap): Int {
                return value.byteCount
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (recyclerView == null)
            recyclerView = parent as RecyclerView
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_find_listview_layout, parent, false)
        val holder = ViewHolder(view)
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position)
        holder?.let {
            val data = datas[position]
            setHolderData(holder, data)
            GetCommentImage(holder,data)
            GetUserImage(holder,data)
            setHolderLikeState(holder, data)
            setLikeClick(holder, data, position)
            setHolderFocusState(holder, data)
            setFocusClick(holder, data)
        }
    }

    private fun setHolderData(holder: ViewHolder, data: UserCommentData) {
        holder.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
        holder.comment.text = data.replyNum.toString()
        holder.name_text.text = data.userName
    }

    private fun setHolderLikeState(holder: ViewHolder, data: UserCommentData) {
        if (data.isLike()) {
            SetLike(holder.like, holder.dislike, data.likeNum)
        } else {
            CancelLike(holder.like, holder.dislike, data.likeNum)
        }
    }

    private fun setHolderFocusState(holder: ViewHolder, data: UserCommentData) {
        if (what == MY_FOCUS_COMMENT) {
            holder.addfocusText.visibility = View.GONE
        }
        if (what == 3) {
            hideSomeElement(holder, data)
        }
        if (data.userID == LoginActivity.userID) {
            holder.addfocusText.visibility = View.GONE
        } else {
            if (data.isFollow()) {
                holder.cancelFocusText.visibility = View.VISIBLE
                holder.addfocusText.visibility = View.GONE
            } else {
                holder.cancelFocusText.visibility = View.GONE
                holder.addfocusText.visibility = View.VISIBLE
            }
        }
    }


    private fun setLikeClick(holder: ViewHolder, data: UserCommentData, position: Int) {
        holder.like.setOnClickListener {
            if (checkUserLogin()) {
                handleLikeClick(holder, LIKE, data, position)
            }
        }
        holder.dislike.setOnClickListener {
            if (checkUserLogin()) {
                handleLikeClick(holder, DISLIKE, data, position)
            }
        }
    }

    private fun handleLikeClick(holder: ViewHolder, divide: Int, data: UserCommentData, position: Int) {
        holder.like.isEnabled = false
        holder.dislike.isEnabled = false
        val isLike = divide == LIKE
        Thread {
            val success = when (divide) {
                LIKE -> HandleFindNew.SetUserLike(LoginActivity.userID, data.id)
                DISLIKE -> HandleFindNew.SetUserLike(LoginActivity.userID, data.id)
                else -> false
            }
            context.runOnUiThread {
                if (success) {
                    changeLikeDataState(isLike, position)
                    when (divide) {
                        LIKE -> {
                            SetLike(holder.like, holder.dislike, data.likeNum)
                            holder.dislike.isEnabled = true
                        }
                        DISLIKE -> {
                            CancelLike(holder.like, holder.dislike, data.likeNum)
                            holder.like.isEnabled = true
                        }
                    }
                } else {
                    toast("出现错误")
                }
            }
        }.start()
    }

    private fun setFocusClick(holder: ViewHolder, data: UserCommentData) {
        holder.addfocusText.setOnClickListener {
            if (checkUserLogin()) {
                handleFocus(holder, data, ADD_FOCUS)
            }
        }
        holder.cancelFocusText.setOnClickListener {
            if (checkUserLogin()) {
                handleFocus(holder, data, CANCEL_FOCUS)
            }
        }
    }

    private fun handleFocus(holder: ViewHolder, data: UserCommentData, divide: Int) {
        holder.addfocusText.isEnabled = false
        holder.cancelFocusText.isEnabled = false
        Thread {
            val isFocus = divide == ADD_FOCUS
            val success = when (divide) {
                ADD_FOCUS -> HandleUserNew.AddFocus(LoginActivity.userID, data.userID)
                CANCEL_FOCUS -> HandleUserNew.CancelFocus(LoginActivity.userID, data.userID)
                else -> false
            }
            context.runOnUiThread {
                if (success) {
                    changeFocusDataState(isFocus, data.userID)
                    val tostText = if (isFocus) "关注成功" else "取消关注成功"
                    toast(tostText)
                    when (divide) {
                        ADD_FOCUS -> holder.cancelFocusText.isEnabled = true
                        CANCEL_FOCUS -> holder.addfocusText.isEnabled = true
                    }
                    notifyDataSetChanged()
                } else {
                    toast("出现错误")
                }
            }
        }.start()
    }


    private fun changeLikeDataState(isLike: Boolean, position: Int) {
        var likeNumber = datas[position].likeNum
        likeNumber = if (isLike) likeNumber + 1 else likeNumber - 1
        datas[position].likeNum = likeNumber
    }

    private fun changeFocusDataState(isFocus: Boolean, userID: Int) {
        val isFollow = if (isFocus) SUCCESS else ERROR
        datas.filter { it.userID == userID }.forEach { it.isFollow = isFollow }
    }

    private fun SetLike(like: TextView, dislike: TextView, count: Int): Boolean {
        dislike.text = count.toString()
        val imageAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
        dislike.visibility = View.VISIBLE
        like.visibility = View.GONE
        dislike.startAnimation(imageAnimation)
        return true
    }

    private fun CancelLike(like: TextView, dislike: TextView, count: Int): Boolean {
        like.text = count.toString()
        val imageAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
        like.visibility = View.VISIBLE
        dislike.visibility = View.GONE
        like.startAnimation(imageAnimation)
        return true
    }

    private fun GetCommentImage(holder: ViewHolder,data: UserCommentData){
        val requestOption=RequestOptions().placeholder(R.drawable.backgound_grey).error(R.drawable.backgound_grey)
        Glide.with(context).load(BaseSettingNew.URL+data.imageUrl).apply(requestOption).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img)
    }

    private fun GetUserImage(holder: ViewHolder,data: UserCommentData){
        val requestOptions=RequestOptions().error(R.drawable.ic_assignment_ind_deep_orange_200_48dp).fallback(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
        Thread{
            val userImageURL=HandleUserNew.GetUserImageURL(data.userID)
            context.runOnUiThread {
                Glide.with(context).load(userImageURL).apply(requestOptions).into(holder.userImage)
            }
        }.start()
    }


    private fun hideSomeElement(vh: ViewHolder, data: UserCommentData) {
        vh.like.visibility = View.GONE
        vh.comment.visibility = View.INVISIBLE
        vh.name_text.text = data.commentTitle
    }

    private fun checkUserLogin(): Boolean {
        if (LoginActivity.userID == 0) {
            Toast.makeText(context, "没有登录", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("isInto", 1)
            context.startActivity(intent)
            return false
        }
        return true
    }

    companion object {
        const val LIKE = 1
        const val DISLIKE = 2
        const val ADD_FOCUS = 3
        const val CANCEL_FOCUS = 4
    }
}
