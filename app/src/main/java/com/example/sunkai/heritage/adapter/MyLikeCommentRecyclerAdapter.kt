package com.example.sunkai.heritage.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.activity.OtherUsersActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.connectWebService.HandlePerson
import com.example.sunkai.heritage.entity.UserCommentData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.HandleAdapterItemClickClickUtils
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.generateDarkColor
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.ERROR
import com.example.sunkai.heritage.value.SUCCESS
import com.example.sunkai.heritage.value.TYPE_FIND

/**
 * 个人中心我的赞的recyclerview的adapter
 * Created by sunkai on 2018/3/12.
 */
class MyLikeCommentRecyclerAdapter(context: Activity, datas: List<UserCommentData>,glide: RequestManager) : BaseRecyclerAdapter<MyLikeCommentRecyclerAdapter.Holder, UserCommentData>(context,datas,glide) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val userName: TextView
        val userImage: ImageView
        val commentImage: ImageView
        val likeButton: ImageView
        val collectButton: ImageView
        val infoBackground: LinearLayout

        init {
            title = view.findViewById(R.id.comment_title)
            userName = view.findViewById(R.id.user_name)
            userImage = view.findViewById(R.id.user_image)
            commentImage = view.findViewById(R.id.coment_image)
            likeButton = view.findViewById(R.id.set_like)
            collectButton = view.findViewById(R.id.set_colelct)
            infoBackground = view.findViewById(R.id.info_background)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_like_comment_recyclerview_item, parent, false)
        view.setOnClickListener(this)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        setDataToView(holder, data)
        getImages(holder, data)
        setButtonClick(holder, data)
    }


    private fun setDataToView(holder: Holder, data: UserCommentData) {
        holder.userName.text = data.userName
        holder.title.text = data.commentTitle
        holder.likeButton.setImageResource(if (data.isLike()) R.drawable.ic_favorite_orange_a700_24dp else R.drawable.ic_favorite_border_grey_700_24dp)
    }

    private fun getImages(holder: Holder, data: UserCommentData) {
        holder.commentImage.setImageDrawable(null)
        val imageUrl = BaseSetting.URL + data.imageUrl
        val simpleTarget = object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                val color = resource.generateDarkColor()
                holder.infoBackground.setBackgroundColor(color)
                holder.commentImage.setImageDrawable(resource)
                holder.likeButton.setColorFilter(color)
                holder.collectButton.setImageResource(if (data.isCollect()) R.drawable.ic_bookmark_black_24dp else R.drawable.ic_bookmark_border_white_24dp)
                holder.collectButton.setColorFilter(color)
            }
        }
        glide.load(imageUrl).into(simpleTarget)
        ThreadPool.execute {
            val url = HandlePerson.GetUserImageURL(data.userID) ?: return@execute
            runOnUiThread(Runnable {
                glide.load(url).into(holder.userImage)
            })
        }
    }

    private fun setButtonClick(holder: Holder, data: UserCommentData) {
        holder.userImage.setOnClickListener {
            gotoUserPage(data)
        }
        holder.likeButton.setOnClickListener {
            handleLikeBtnClick(holder, data)
        }
        holder.collectButton.setOnClickListener {
            handleCollecctBtnClick(holder, data)
        }
    }

    private fun handleCollecctBtnClick(holder: Holder, data: UserCommentData) {
        holder.collectButton.isEnabled = false
        setCollectBtnState(holder,data)
        ThreadPool.execute {
            val result = if (data.isCollect())
                HandlePerson.CancelUserCollect(LoginActivity.userID, TYPE_FIND, data.id)
            else
                HandlePerson.AddUserCollection(LoginActivity.userID, TYPE_FIND, data.id)
            runOnUiThread(Runnable {
                holder.collectButton.isEnabled = true
                if (!result) {
                    toast("出现问题，请稍后再试")
                } else {
                    setCollectDataState(data)
                }
            })
        }
    }


    private fun gotoUserPage(data: UserCommentData) {
        val intent = Intent(context, OtherUsersActivity::class.java)
        intent.putExtra("userID", data.userID)
        context.startActivity(intent)
    }

    private fun handleLikeBtnClick(holder: Holder, data: UserCommentData) {
        holder.likeButton.isEnabled = false
        setLikeBtnState(holder, data)
        ThreadPool.execute {
            val result = if (data.isLike())
                HandleFind.CancelUserLike(LoginActivity.userID, data.id)
            else
                HandleFind.SetUserLike(LoginActivity.userID, data.id)
            runOnUiThread(Runnable {
                holder.likeButton.isEnabled = true
                if (!result) {
                    toast("出现问题，请稍后再试")
                    setLikeBtnState(holder, data) //出现问题，还原button的状态
                } else {
                    setDataState(data)
                }
            })
        }
    }

    private fun setLikeBtnState(holder: Holder, data: UserCommentData) {
        holder.likeButton.setImageResource(if (data.isLike()) R.drawable.ic_favorite_border_grey_700_24dp else R.drawable.ic_favorite_orange_a700_24dp)
    }

    private fun setCollectBtnState(holder: Holder,data: UserCommentData){
        holder.collectButton.setImageResource(if (data.isCollect()) R.drawable.ic_bookmark_border_white_24dp else R.drawable.ic_bookmark_black_24dp)
    }

    private fun setDataState(data: UserCommentData) {
        data.isLike = if (data.isLike()) ERROR else SUCCESS
    }

    private fun setCollectDataState(data: UserCommentData) {
        data.isCollect = if (data.isCollect()) ERROR else SUCCESS
    }

    override fun setItemClick() {
        HandleAdapterItemClickClickUtils.handleMyLikeCommentRecyclerviewItemClick(context,this)
    }
}