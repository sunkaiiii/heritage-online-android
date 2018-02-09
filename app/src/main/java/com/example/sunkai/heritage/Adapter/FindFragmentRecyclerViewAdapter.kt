package com.example.sunkai.heritage.Adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
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
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.R
import com.makeramen.roundedimageview.RoundedImageView

import org.kobjects.base64.Base64

import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference
import java.util.ArrayList


/*
 * Created by sunkai on 2017/12/22.
 */

class FindFragmentRecyclerViewAdapter(private val context: Context, internal var what: Int) : BaseRecyclerAdapter<FindFragmentRecyclerViewAdapter.ViewHolder,UserCommentData>(arrayListOf()) {
    private var recyclerView: RecyclerView? = null
    internal var lruCache: LruCache<Int, Bitmap>

    class ViewHolder internal constructor(var view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView
        val like: TextView
        val comment: TextView
        val addfocusText: TextView
        val name_text: TextView
        val likeImage: ImageView
        val commentImage: ImageView
        val addfocusImage: ImageView
        val userImage: RoundedImageView

        init {
            img = view.findViewById(R.id.fragment_find_litview_img)
            comment = view.findViewById(R.id.testview_comment)
            like = view.findViewById(R.id.textview_like)
            likeImage = view.findViewById(R.id.imageView4)
            commentImage = view.findViewById(R.id.fragment_find_comment)
            addfocusImage = view.findViewById(R.id.add_focus_img)
            addfocusText = view.findViewById(R.id.add_focus_text)
            name_text = view.findViewById(R.id.name_text)
            userImage = view.findViewById(R.id.user_list_image)
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
        GetInformation(this).execute()
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
        holder?.let{
            val data = datas!![position]
            holder.img.setImageResource(R.drawable.backgound_grey)
            holder.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
            GetCommentImage(data, holder.img)
            GetUserImage(data, holder.userImage)
            if (data.getUserLike()) {
                SetLike(holder.like, holder.likeImage, Integer.parseInt(data.commentLikeNum))
            } else {
                CancelLike(holder.like, holder.likeImage, Integer.parseInt(data.commentLikeNum))
            }
            holder.comment.text = data.commentReplyNum
            holder.name_text.text = data.userName
            if (what == 2) {
                holder.addfocusImage.visibility = View.GONE
                holder.addfocusText.visibility = View.GONE
            }
            if (what == 3) {
                hideSomeElement(holder, data)
            }
            val likeClick = imageButtonclick(data.id, position, holder.likeImage, holder.like)
            holder.likeImage.setOnClickListener(likeClick)
            holder.like.setOnClickListener(likeClick)
            if (data.user_id == LoginActivity.userID) {
                holder.addfocusText.visibility = View.INVISIBLE
                holder.addfocusImage.visibility = View.INVISIBLE
            } else {
                holder.addfocusText.visibility = View.VISIBLE
                holder.addfocusImage.visibility = View.VISIBLE
                val addFocusButtonClick = addFocusButtonClick(position)
                holder.addfocusText.setOnClickListener(addFocusButtonClick)
                holder.addfocusImage.setOnClickListener(addFocusButtonClick)
                if (data.getUserFocusUser()) {
                    holder.addfocusText.text = "已关注"
                    holder.addfocusText.setTextColor(Color.rgb(184, 184, 184))
                    holder.addfocusImage.setImageResource(R.drawable.ic_remove_circle_grey_400_24dp)
                } else {

                    holder.addfocusText.text = "加关注"
                    holder.addfocusImage.setImageResource(R.drawable.ic_add_circle_black_24dp)
                }
            }
        }
    }

    private fun changeLikeImageState(isLike: Boolean, imageView: ImageView?, textView: TextView?, position: Int): Boolean {
        var likeNumber = Integer.parseInt(datas!![position].commentLikeNum)
        likeNumber = if (isLike) likeNumber + 1 else likeNumber - 1
        datas!![position].commentLikeNum = likeNumber.toString()
        return if (isLike) SetLike(textView, imageView, likeNumber) else CancelLike(textView, imageView, likeNumber)
    }

    private fun SetLike(textView: TextView?, imageView: ImageView?, count: Int): Boolean {
        textView!!.text = count.toString()
        textView.setTextColor(Color.rgb(172, 70, 46))
        val imageAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
        imageView!!.setImageResource(R.drawable.like_islike)
        imageView.startAnimation(imageAnimation)
        return true
    }

    private fun CancelLike(textView: TextView?, imageView: ImageView?, count: Int): Boolean {
        textView!!.text = count.toString()
        textView.setTextColor(Color.DKGRAY)
        val imageAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
        imageView!!.setImageResource(R.drawable.good_unpress)
        imageView.startAnimation(imageAnimation)
        return true
    }

    private fun GetCommentImage(data: UserCommentData, imageView: ImageView) {
        val bitmap = lruCache.get(data.id)
        if (null != bitmap) {
            imageView.setImageBitmap(bitmap)
        } else {
            GetCommentImage(data.id, data.commentTime,this, imageView).execute()
        }
    }

    private fun GetUserImage(data: UserCommentData, imageView: ImageView) {
        GetUserImage(data.user_id, this, imageView).execute()
    }

    private fun hideSomeElement(vh: ViewHolder, data: UserCommentData) {
        vh.like.visibility = View.GONE
        vh.comment.visibility = View.INVISIBLE
        vh.commentImage.visibility = View.INVISIBLE
        vh.likeImage.visibility = View.INVISIBLE
        vh.likeImage.visibility = View.INVISIBLE
        vh.name_text.text = data.commentTitle
    }

    private fun login() {
        Toast.makeText(context, "没有登录", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra("isInto", 1)
        context.startActivity(intent)
    }

    internal inner class imageButtonclick internal constructor(var commentID: Int, var position: Int, imageView: ImageView, textView: TextView) : View.OnClickListener {
        val imageViewWeakReference: WeakReference<ImageView> = WeakReference(imageView)
        val textViewWeakReference: WeakReference<TextView> = WeakReference(textView)
        private val LIKE = 1
        private val CANCEL = 2
        private val ERROR = 0
        var setLike: Runnable = Runnable {
            val result = HandleFind.Set_User_Like(LoginActivity.userID, commentID)
            if (result) {
                SetOrCancelLikeHandler.sendEmptyMessage(LIKE)
            } else {
                SetOrCancelLikeHandler.sendEmptyMessage(ERROR)
            }
        }
        var cancelLike: Runnable = Runnable {
            val result = HandleFind.Cancel_User_Like(LoginActivity.userID, commentID)
            if (result) {
                SetOrCancelLikeHandler.sendEmptyMessage(CANCEL)
            } else {
                SetOrCancelLikeHandler.sendEmptyMessage(ERROR)
            }
        }

        var SetOrCancelLikeHandler: Handler = object : Handler(context.mainLooper) {
            override fun handleMessage(msg: Message) {
                val getImageView = imageViewWeakReference.get()
                val getTextView = textViewWeakReference.get()
                if (msg.what != ERROR && getImageView != null && getTextView != null) {
                    datas!![position].isUserLike = LIKE == msg.what
                    if (!changeLikeImageState(datas!![position].getUserLike(), imageView, textView, position)) {
                        GetInformation(this@FindFragmentRecyclerViewAdapter).execute()
                    }
                } else {
                    Toast.makeText(context, "出现错误，请稍后再试", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.textview_like, R.id.imageView4 -> SetOrCancelLike()
                else -> {
                }
            }
        }

        private fun SetOrCancelLike() {
            if (LoginActivity.userID == 0) {
                login()
                return
            }
            if (datas!![position].getUserLike()) {
                Thread(cancelLike).start()
            } else {
                Thread(setLike).start()
            }
        }
    }

    internal inner class addFocusButtonClick(var position: Int) : View.OnClickListener {
        var data: UserCommentData= datas!![position]
        var addOrCancelFocus: Runnable = Runnable {
            val result = if (data.getUserFocusUser()) HandlePerson.Cancel_Focus(LoginActivity.userID, data.user_id) else HandlePerson.Add_Focus(LoginActivity.userID, data.user_id)
            setDataState(result)
            handler.sendEmptyMessage(if (data.getUserFocusUser()) 1 else 2)
        }

        var handler: Handler = object : Handler(context.mainLooper) {
            override fun handleMessage(msg: Message) {
                notifyDataSetChanged()
                if (msg.what == 1) {
                    Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show()
                } else if (msg.what == 2) {
                    Toast.makeText(context, "取消关注成功", Toast.LENGTH_SHORT).show()
                }
            }
        }

        init {
            this.data = datas!![position]
        }

        override fun onClick(v: View) {
            if (LoginActivity.userID == 0) {
                login()
                return
            }
            Thread(addOrCancelFocus).start()
        }

        private fun setDataState(result: Boolean) {
            if (result) {
                datas!![position].isUserFocusUser = !datas!![position].getUserFocusUser()
                data.isUserFocusUser = !data.getUserFocusUser()
            }
            datas!!.indices
                    .filter { datas!![it].user_id == data.user_id }
                    .forEach { datas!![it].isUserFocusUser = !datas!![it].getUserFocusUser() }
        }
    }

    fun getReplyCount(commentID: Int, position: Int) {
        GetReplyCount(commentID, position, this).execute()
    }

    fun reFreshList() {
        GetInformation(this).execute()
    }

    internal class GetReplyCount internal constructor(var commentID: Int, var position: Int, adapter: FindFragmentRecyclerViewAdapter) : AsyncTask<Void, Void, Int>() {
        var findFragmentAdapterWeakReference: WeakReference<FindFragmentRecyclerViewAdapter>

        init {
            findFragmentAdapterWeakReference = WeakReference(adapter)
        }

        override fun doInBackground(vararg voids: Void): Int? {
            return HandleFind.Get_User_Comment_Count(commentID)
        }

        override fun onPostExecute(count: Int?) {
            val adapter = findFragmentAdapterWeakReference.get() ?: return
            adapter.datas!![position].commentReplyNum = count.toString()
            adapter.notifyDataSetChanged()
        }
    }

    internal class GetInformation internal constructor(adapter: FindFragmentRecyclerViewAdapter) : AsyncTask<Void, Void, Void>() {

        var findFragmentAdapterWeakReference: WeakReference<FindFragmentRecyclerViewAdapter>

        init {
            findFragmentAdapterWeakReference = WeakReference(adapter)
        }

        override fun doInBackground(vararg voids: Void): Void? {
            val adapter = findFragmentAdapterWeakReference.get() ?: return null
            val getdatas: List<UserCommentData>?
            getdatas = when {
                adapter.what == 1 -> HandleFind.Get_User_Comment_Information(LoginActivity.userID)
                adapter.what == 2 -> HandleFind.Get_User_Comment_Information_By_User(LoginActivity.userID)
                adapter.what == 3 -> HandleFind.Get_User_Comment_Information_By_Own(LoginActivity.userID)
                else -> ArrayList()
            }
            getdatas?.let {
                adapter.datas = getdatas
            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            val adapter = findFragmentAdapterWeakReference.get() ?: return
            adapter.notifyDataSetChanged()
            val intent = Intent("android.intent.action.animationStop")
            adapter.context.sendBroadcast(intent)
        }
    }

    internal class GetCommentImage internal constructor(val id: Int,val commentTime:String, findFragmentAdapter: FindFragmentRecyclerViewAdapter, imageView: ImageView) : AsyncTask<Void, Void, Bitmap>() {
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
            var update=false
            cursor = db.query(table, null, selection, selectionArgs, null, null, null)
            cursor.moveToFirst()
            if (!cursor.isAfterLast) {
                val commentTimeIndex=cursor.getColumnIndex("comment_time")
                if(commentTimeIndex>=0) {
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
            contentValues.put("comment_time",commentTime)
            contentValues.put("image", bytes)
            db = MySqliteHandler.GetWritableDatabase()
            if(update){
                db.update(table,contentValues,"imageID=?", arrayOf(id.toString()))
            }else {
                db.insert(table, null, contentValues)
            }
            val bitmap = HandlePic.handlePic(ByteArrayInputStream(bytes), 0)
            findFragmentAdapter.lruCache.put(id, bitmap)
            return bitmap
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            val findFragmentAdapter = findFragmentAdapterWeakReference.get() ?: return
            val getImageView = imageViewWeakReference.get()
            if (getImageView != null&&bitmap!=null) {
                getImageView.run {
                    val imageAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
                    setImageBitmap(bitmap)
                    startAnimation(imageAnimation)
                }
            }

        }
    }

    internal class GetUserImage internal constructor(var id: Int, adapter: FindFragmentRecyclerViewAdapter, imageView: ImageView) : AsyncTask<Void, Void, Bitmap>() {
        var findFragmentAdapterWeakReference: WeakReference<FindFragmentRecyclerViewAdapter>
        var imageViewWeakReference: WeakReference<ImageView>

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

}
