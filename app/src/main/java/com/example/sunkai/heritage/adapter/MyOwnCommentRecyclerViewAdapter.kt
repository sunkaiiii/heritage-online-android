package com.example.sunkai.heritage.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.activity.ModifyUsercommentActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.entity.UserCommentData
import com.example.sunkai.heritage.dialog.NormalWarningDialog
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseAsyncTask
import com.example.sunkai.heritage.tools.HandleAdapterItemClickClickUtils
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.ERROR
import com.example.sunkai.heritage.value.MODIFY_USER_COMMENT
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 我的帖子的recyclerView的adapter
 * Created by sunkai on 2018/1/15.
 */
class MyOwnCommentRecyclerViewAdapter(context: Context, datas: List<UserCommentData>,glide: RequestManager) : BaseRecyclerAdapter<MyOwnCommentRecyclerViewAdapter.ViewHolder, UserCommentData>(context,datas,glide) {

    private var onDeleteListener:onDeleteSuccessListener?=null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mycomment_item_image: ImageView
        val mycomment_item_title: TextView
        val mycomment_item_content: TextView
        val more_menu: ImageView

        init {
            mycomment_item_image = view.findViewById(R.id.mycomment_item_image)
            mycomment_item_title = view.findViewById(R.id.mycomment_item_title)
            mycomment_item_content = view.findViewById(R.id.mycomment_item_content)
            more_menu = view.findViewById(R.id.item_more)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.mycomment_layout_item, parent, false)
        val holder = ViewHolder(view)
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        setData(holder,data)
        GetCommentImage(holder, data)
        setMoreImageShowPopWindow(holder, data)
    }
    private fun setData(holder: ViewHolder,data: UserCommentData){
        holder.mycomment_item_title.text=data.commentTitle
        holder.mycomment_item_content.text=data.commentContent
    }
    private fun GetCommentImage(holder: ViewHolder, data: UserCommentData) {
        GetCommentImageAsync(data, this, holder).execute()
    }

    private fun setMoreImageShowPopWindow(holder: ViewHolder, data: UserCommentData) {
        holder.more_menu.setOnClickListener { _ ->
            val popMenu = PopupMenu(context, holder.more_menu)
            popMenu.menuInflater.inflate(R.menu.my_own_tiezi_pop_menu, popMenu.menu)
            popMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit_comment -> {
                        val intent = Intent(context, ModifyUsercommentActivity::class.java)
                        intent.putExtra("data", data)
                        if(context is Activity){
                            context.startActivityForResult(intent, MODIFY_USER_COMMENT)
                        }else {
                            context.startActivity(intent)
                        }
                    }
                    R.id.delete_comment -> {
                        NormalWarningDialog()
                                .setTitle(context.getString(R.string.delete_comment_title))
                                .setContent(context.getString(R.string.delete_comment_content))
                                .setOnSubmitClickListener(object : NormalWarningDialog.onSubmitClickListener {
                                    override fun onSubmit(view: View, dialog: NormalWarningDialog) {
                                        deleteComment(data, view, dialog)
                                    }
                                })
                                .show((context as AppCompatActivity).supportFragmentManager, "删除帖子")
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popMenu.show()
        }
    }

    private fun deleteComment(data: UserCommentData, view: View, dialog: NormalWarningDialog) {
        view.isEnabled = false
        GlobalScope.launch {
            val result = HandleFind.DeleteUserCommentByID(data.id)
            runOnUiThread(Runnable {
                val toastText = if (result) "删除帖子成功" else "删除帖子失败"
                if (result) {
                    dialog.dismiss()
                    onDeleteListener?.onDeleteSuccess()
                } else {
                    view.isEnabled = true
                }
                toast(toastText)
            })
        }
    }

    private class GetCommentImageAsync internal constructor(val data: UserCommentData, adpter: MyOwnCommentRecyclerViewAdapter, val holder: ViewHolder) : BaseAsyncTask<Void, Void, String, MyOwnCommentRecyclerViewAdapter>(adpter) {
        override fun doInBackground(vararg params: Void?): String {
            return HandleFind.GetUserCommentImageUrl(data.id)
        }

        override fun onPostExecute(url: String) {
            if (!TextUtils.isEmpty(url) && url != ERROR) {
                val adpter = weakRefrece.get()
                adpter?.let {
                    adpter.glide.load(url).into(simpleTarget(holder, adpter))
                }
            }
        }

        class simpleTarget(val holder: ViewHolder, val adapter: MyOwnCommentRecyclerViewAdapter) : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                holder.mycomment_item_image.setImageDrawable(resource)
                adapter.findDominateColor(resource.toBitmap(), holder)
            }

        }

    }

    private fun findDominateColor(bitmap: Bitmap, holder: ViewHolder) {
        val color = Palette.from(bitmap).generate().getDarkMutedColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        holder.mycomment_item_image.setImageBitmap(bitmap)
        holder.mycomment_item_title.setTextColor(color)
        holder.mycomment_item_content.setTextColor(color)
    }

    fun setOnDeleteSuccessListener(onDeleteSuccessListener: onDeleteSuccessListener){
        this.onDeleteListener=onDeleteSuccessListener
    }

    interface onDeleteSuccessListener{
        fun onDeleteSuccess()
    }

    override fun setItemClick() {
        HandleAdapterItemClickClickUtils.handleMyOwnCommentRecyclerViewItemClick(context,this)
    }
}