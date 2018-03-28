package com.example.sunkai.heritage.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSetting
import com.example.sunkai.heritage.ConnectWebService.BaseSetting.Companion.ERROR
import com.example.sunkai.heritage.ConnectWebService.BaseSetting.Companion.SUCCESS
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.CommentReplyInformation
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.Dialog.AddUserCommentBottomDialog
import com.example.sunkai.heritage.Interface.AddUserReplyDialog
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.MY_FOCUS_COMMENT
import com.makeramen.roundedimageview.RoundedImageView


/**
 * 发现页第一屏recyclerView的adapter
 * Created by sunkai on 2017/12/22.
 */

class FindFragmentRecyclerViewAdapter(private val context: Activity, datas: List<UserCommentData>, private var what: Int) : BaseRecyclerAdapter<FindFragmentRecyclerViewAdapter.ViewHolder, UserCommentData>(datas) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //仿照Instagram的正方形照片，我也不知道这样好不好
        val img: ImageView
        val like: ImageView
        val dislike: ImageView
        val comment: ImageView
        val addfocusText: TextView
        val cancelFocusText: TextView
        val name_text: TextView
        val userImage: RoundedImageView
        val likeCount: TextView
        val miniReplys: LinearLayout

        init {
            img = view.findViewById(R.id.fragment_find_litview_img)
            comment = view.findViewById(R.id.imageview_comment)
            like = view.findViewById(R.id.imageView_like)
            dislike = view.findViewById(R.id.imageView_dislike)
            addfocusText = view.findViewById(R.id.add_focus_text)
            name_text = view.findViewById(R.id.name_text)
            userImage = view.findViewById(R.id.user_list_image)
            cancelFocusText = view.findViewById(R.id.cancel_focus_text)
            likeCount = view.findViewById(R.id.user_comment_like_number_textview)
            miniReplys = view.findViewById(R.id.user_comment_mini_replys)
        }
    }

    class MiniReplyHolder(view: View) {
        val userName: TextView
        val replyContent: TextView
        val splitLine: View

        init {
            userName = view.findViewById(R.id.reply_name)
            replyContent = view.findViewById(R.id.reply_content)
            splitLine = view.findViewById(R.id.user_comment_reply_split_line)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_find_listview_layout, parent, false)
        val holder = ViewHolder(view)
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = datas[position]
        setHolderData(holder, data)
        GetCommentImage(holder, data)
        GetUserImage(holder, data)
        setHolderLikeState(holder, data)
        setLikeClick(holder, data, position)
        setAddReplyClick(holder, data)
        setHolderFocusState(holder, data)
        setFocusClick(holder, data)
        showMiniReply(holder, data)
    }

    private fun setHolderData(holder: ViewHolder, data: UserCommentData) {
        holder.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
        holder.name_text.text = data.userName
    }

    private fun setHolderLikeState(holder: ViewHolder, data: UserCommentData) {
        holder.like.isEnabled=!data.isLike()
        holder.dislike.isEnabled=data.isLike()
        if (data.isLike()) {
            SetLike(holder, data.likeNum)
        } else {
            CancelLike(holder, data.likeNum)
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

    private fun setAddReplyClick(holder: ViewHolder, data: UserCommentData) {
        holder.comment.setOnClickListener {
            val dialog = AddUserCommentBottomDialog(context, data.id,data)
            //设置当回复成功的时候，刷新显示的回复内容
            dialog.setOnAddUserReplyListener(object : AddUserReplyDialog {
                override fun onAddUserReplySuccess(data: CommentReplyInformation) {
                    if (holder.miniReplys.childCount > 2) {
                        holder.miniReplys.removeViewAt(0)
                    }
                    val view = LayoutInflater.from(context).inflate(R.layout.user_comment_reply_information, holder.miniReplys, false)
                    val miniReplyHolder = MiniReplyHolder(view)
                    setDataInMiniReply(miniReplyHolder, data)
                    holder.miniReplys.addView(view)
                }

            })
            dialog.show()
        }
    }

    private fun handleLikeClick(holder: ViewHolder, divide: Int, data: UserCommentData, position: Int) {
        holder.like.isEnabled = false
        holder.dislike.isEnabled = false
        val isLike = divide == LIKE
        ThreadPool.execute {
            val success = when (divide) {
                LIKE -> HandleFind.SetUserLike(LoginActivity.userID, data.id)
                DISLIKE -> HandleFind.CancelUserLike(LoginActivity.userID, data.id)
                else -> false
            }
            context.runOnUiThread {
                when(divide){
                    LIKE->holder.dislike.isEnabled = true
                    DISLIKE->holder.like.isEnabled=true
                }
                if (success) {
                    changeLikeDataState(isLike, position)
                    when (divide) {
                        LIKE ->SetLike(holder, data.likeNum)
                        DISLIKE ->CancelLike(holder, data.likeNum)
                    }
                } else {
                    toast("出现错误")
                }
            }
        }
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
        ThreadPool.execute {
            val isFocus = divide == ADD_FOCUS
            val success = when (divide) {
                ADD_FOCUS -> HandlePerson.AddFocus(LoginActivity.userID, data.userID)
                CANCEL_FOCUS -> HandlePerson.CancelFocus(LoginActivity.userID, data.userID)
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
        }
    }


    private fun changeLikeDataState(isLike: Boolean, position: Int) {
        var likeNumber = datas[position].likeNum
        likeNumber = if (isLike) likeNumber + 1 else likeNumber - 1
        datas[position].likeNum = likeNumber
        datas[position].isLike=if(isLike)"SUCCESS" else "ERROR"
    }

    private fun changeFocusDataState(isFocus: Boolean, userID: Int) {
        val isFollow = if (isFocus) SUCCESS else ERROR
        datas.filter { it.userID == userID }.forEach { it.isFollow = isFollow }
    }

    @SuppressLint("SetTextI18n")
    private fun SetLike(holder: ViewHolder, count: Int): Boolean {
        holder.likeCount.text = "$count 次赞"
        val imageAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
        holder.dislike.visibility = View.VISIBLE
        holder.like.visibility = View.GONE
        holder.dislike.startAnimation(imageAnimation)
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun CancelLike(holder: ViewHolder, count: Int): Boolean {
        holder.likeCount.text = "$count 次赞"
        val imageAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
        holder.like.visibility = View.VISIBLE
        holder.dislike.visibility = View.GONE
        holder.like.startAnimation(imageAnimation)
        return true
    }

    private fun GetCommentImage(holder: ViewHolder, data: UserCommentData) {
        val requestOption = RequestOptions().placeholder(R.color.lightGrey).error(R.color.lightGrey)
        Glide.with(context).load(BaseSetting.URL + data.imageUrl).apply(requestOption).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img)
    }

    private fun GetUserImage(holder: ViewHolder, data: UserCommentData) {
        val requestOptions = RequestOptions().error(R.drawable.ic_assignment_ind_deep_orange_200_48dp).fallback(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
        ThreadPool.execute {
            val userImageURL = HandlePerson.GetUserImageURL(data.userID)
            context.runOnUiThread {
                Glide.with(context).load(userImageURL).apply(requestOptions).into(holder.userImage)
            }
        }
    }

    private fun showMiniReply(holder: ViewHolder, data: UserCommentData) {
        holder.miniReplys.removeAllViews()
        for (reply in data.miniReplys) {
            //因为返回的有可能是个内容为空的类，过滤掉空数据使其不显示
            if (!reply.userName.isEmpty()) {
                val view = LayoutInflater.from(context).inflate(R.layout.user_comment_reply_information, holder.miniReplys, false)
                val miniReplyHolder = MiniReplyHolder(view)
                setDataInMiniReply(miniReplyHolder, reply)
                holder.miniReplys.addView(view)
            }
        }
    }

    private fun setDataInMiniReply(miniReplyHolder: MiniReplyHolder, reply: CommentReplyInformation) {
        //因为复用的是详情页的回帖item，需要对间距进行调整
        //日后会考虑新加一个item
        miniReplyHolder.userName.text = reply.userName
        //字体加粗
        miniReplyHolder.userName.paint.isFakeBoldText = true
        //调整间距
        val namelayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        namelayoutParams.setMargins(0, 0, 0, 0)
        miniReplyHolder.userName.layoutParams = namelayoutParams
        val contentlayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        contentlayoutParams.setMargins(12, 0, 0, 0)
        miniReplyHolder.replyContent.layoutParams = contentlayoutParams
        miniReplyHolder.replyContent.text = reply.replyContent
        miniReplyHolder.replyContent.maxLines = 1
        miniReplyHolder.replyContent.setTextColor(Color.BLACK)
        miniReplyHolder.splitLine.visibility = View.GONE
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
