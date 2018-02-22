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
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew.Companion.ERROR
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew.Companion.SUCCESS
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.ConnectWebService.HandleFindNew
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.ConnectWebService.HandleUserNew
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


/*
 * Created by sunkai on 2017/12/22.
 */

class FindFragmentRecyclerViewAdapter(private val context: Activity, datas: List<UserCommentData>, private var what: Int) : BaseRecyclerAdapter<FindFragmentRecyclerViewAdapter.ViewHolder, UserCommentData>(datas) {
    private var recyclerView: RecyclerView? = null
    internal var lruCache: LruCache<Int, Bitmap>

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
            GetCommentImage(data, holder.img)
            GetUserImage(data, holder.userImage)
            setHolderLikeState(holder, data)
            setLikeClick(holder, data, position)
            setHolderFocusState(holder, data)
            setFocusClick(holder, data)
        }
    }

    private fun setHolderData(holder: ViewHolder, data: UserCommentData) {
        holder.img.setImageResource(R.drawable.backgound_grey)
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

    private fun GetCommentImage(data: UserCommentData, imageView: ImageView) {
        val bitmap = lruCache.get(data.id)
        if (null != bitmap) {
            imageView.setImageBitmap(bitmap)
        } else {
            GetCommentImage(data.id, data.commentTime, this, imageView).execute()
        }
    }

    private fun GetUserImage(data: UserCommentData, imageView: ImageView) {
        GetUserImage(data.userID, this, imageView).execute()
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


    fun getReplyCount(commentID: Int, position: Int) {
        GetReplyCount(commentID, position, this).execute()
    }


    internal class GetReplyCount internal constructor(private val commentID: Int, var position: Int, adapter: FindFragmentRecyclerViewAdapter) : AsyncTask<Void, Void, Int>() {
        private val findFragmentAdapterWeakReference: WeakReference<FindFragmentRecyclerViewAdapter>

        init {
            findFragmentAdapterWeakReference = WeakReference(adapter)
        }

        override fun doInBackground(vararg voids: Void): Int {
            return HandleFind.Get_User_Comment_Count(commentID)
        }

        override fun onPostExecute(count: Int) {
            val adapter = findFragmentAdapterWeakReference.get() ?: return
            adapter.datas[position].replyNum = count
            adapter.notifyDataSetChanged()
        }
    }

    private class GetCommentImage internal constructor(val id: Int, val commentTime: String, findFragmentAdapter: FindFragmentRecyclerViewAdapter, imageView: ImageView) : AsyncTask<Void, Void, Bitmap>() {
        var imageViewWeakReference: WeakReference<ImageView>
        var findFragmentAdapterWeakReference: WeakReference<FindFragmentRecyclerViewAdapter>

        init {
            findFragmentAdapterWeakReference = WeakReference(findFragmentAdapter)
            imageViewWeakReference = WeakReference(imageView)
        }

        override fun doInBackground(vararg voids: Void): Bitmap? {
            val findFragmentAdapter = findFragmentAdapterWeakReference.get() ?: return null
            var db = MySqliteHandler.GetReadableDatabase()
            val table = "find_comment_image"
            val selection = "imageID=?"
            val cursor: Cursor
            val selectionArgs = arrayOf(id.toString())
            var update = false
            cursor = db.query(table, null, selection, selectionArgs, null, null, null)
            cursor.moveToFirst()
            if (!cursor.isAfterLast) {
                val commentTimeIndex = cursor.getColumnIndex("comment_time")
                if (commentTimeIndex >= 0) {
                    val sqlCommentTime = cursor.getString(commentTimeIndex)
                    if (sqlCommentTime == commentTime) {
                        val imageIndex = cursor.getColumnIndex("image")
                        val img = cursor.getBlob(imageIndex)
                        val `in` = ByteArrayInputStream(img)
                        val bitmap = HandlePic.handlePic(`in`, 0)
                        findFragmentAdapter.lruCache.put(id, bitmap)
                        cursor.close()
                        return bitmap
                    }
                    update = true
                }
            }
            val bytes = HandleFind.Get_User_Comment_Image(id) ?: return null
            val contentValues = ContentValues()
            contentValues.put("imageID", id)
            contentValues.put("comment_time", commentTime)
            contentValues.put("image", bytes)
            db = MySqliteHandler.GetWritableDatabase()
            if (update) {
                db.update(table, contentValues, "imageID=?", arrayOf(id.toString()))
            } else {
                db.insert(table, null, contentValues)
            }
            val bitmap = HandlePic.handlePic(ByteArrayInputStream(bytes), 0)
            findFragmentAdapter.lruCache.put(id, bitmap)
            return bitmap
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            val getImageView = imageViewWeakReference.get()
            if (getImageView != null && bitmap != null) {
                getImageView.run {
                    val imageAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
                    setImageBitmap(bitmap)
                    startAnimation(imageAnimation)
                }
            }

        }
    }

    internal class GetUserImage internal constructor(var id: Int, adapter: FindFragmentRecyclerViewAdapter, imageView: ImageView) : AsyncTask<Void, Void, Bitmap>() {
        private val findFragmentAdapterWeakReference: WeakReference<FindFragmentRecyclerViewAdapter>
        private val imageViewWeakReference: WeakReference<ImageView>

        init {
            imageViewWeakReference = WeakReference(imageView)
            findFragmentAdapterWeakReference = WeakReference(adapter)
        }

        override fun doInBackground(vararg voids: Void): Bitmap? {
            findFragmentAdapterWeakReference.get() ?: return null
            var db = MySqliteHandler.GetReadableDatabase()
            val cursor: Cursor
            val table = "person_image"
            val selection = "imageID=?"
            val selectionArgs = arrayOf(id.toString())
            cursor = db.query(table, null, selection, selectionArgs, null, null, null)
            val result: String?
            cursor.moveToFirst()
            if (!cursor.isAfterLast) {
                val imageIndex = cursor.getColumnIndex("image")
                val image = cursor.getBlob(imageIndex)
                cursor.close()
                return HandlePic.handlePic(ByteArrayInputStream(image), 0)

            }
            val image: ByteArray
            result = HandlePerson.Get_User_Image(id)
            if (result == null)
                return null
            image = Base64.decode(result)
            val contentValues = ContentValues()
            contentValues.put("imageID", id)
            contentValues.put("image", image)
            db = MySqliteHandler.GetWritableDatabase()
            db.insert(table, null, contentValues)
            return HandlePic.handlePic(ByteArrayInputStream(image), 0)
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            val adapter = findFragmentAdapterWeakReference.get()
            if (bitmap == null || adapter == null)
                return
            val imageView = imageViewWeakReference.get()
            if (imageView != null) {
                val imageAnimation: Animation = AnimationUtils.loadAnimation(adapter.context, R.anim.image_apear)
                imageView.setImageBitmap(bitmap)
                imageView.startAnimation(imageAnimation)
            }
        }
    }

    companion object {
        const val LIKE = 1
        const val DISLIKE = 2
        const val ADD_FOCUS = 3
        const val CANCEL_FOCUS = 4
    }
}
