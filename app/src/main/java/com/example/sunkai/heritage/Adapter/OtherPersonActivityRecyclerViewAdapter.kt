package com.example.sunkai.heritage.Adapter

import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseAsyncTask
import com.example.sunkai.heritage.value.RESULT_NULL
import com.example.sunkai.heritage.value.RESULT_OK
import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference

/*
 * Created by sunkai on 2018/1/2.
 */
class OtherPersonActivityRecyclerViewAdapter(val userID: Int) : BaseRecyclerAdapter() {

    var datas: List<Int>

    init {
        datas = IntArray(0).toList()
        getUserIdInfo(userID)
    }

    internal class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView

        init {
            imageView = view.findViewById(R.id.iv_other_person_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(GlobalContext.instance).inflate(R.layout.other_person_view, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            getImage(datas[position], holder.imageView)
        }
    }


    internal fun getUserIdInfo(userID: Int) {
        GetUserInfoTask(userID, this).execute()
    }

    internal class GetUserInfoTask(val userID: Int, adapter: OtherPersonActivityRecyclerViewAdapter) : AsyncTask<Void, Void, Int>() {
        val weakRefrece: WeakReference<OtherPersonActivityRecyclerViewAdapter>

        init {
            weakRefrece = WeakReference(adapter)
        }

        override fun doInBackground(vararg params: Void?): Int {
            val adapter = weakRefrece.get()
            adapter?.let {
                val getDatas = HandleFind.Get_User_Comment_ID_By_User(userID)
                getDatas?.let {
                    adapter.datas = getDatas
                    return RESULT_OK
                }
            }
            return RESULT_NULL
        }

        override fun onPostExecute(result: Int) {
            val adapter = weakRefrece.get()
            when (result) {
                RESULT_OK -> adapter?.notifyDataSetChanged()
            }
        }
    }

    internal class GetUserImageTask(val imageID: Int, imageview: ImageView) : BaseAsyncTask<Void, Void, Bitmap,ImageView>(imageview) {
        override fun doInBackground(vararg params: Void?): Bitmap? {
            var bitmap = findImageInSql()
            bitmap?.let {
                return bitmap
            }
            val imageData = HandleFind.Get_User_Comment_Image(imageID)
            imageData?.let {
                addImageToSql(imageData)
                bitmap = HandlePic.handlePic(ByteArrayInputStream(imageData), 0)
                return bitmap
            }
            return null
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            bitmap?.let {
                val imageview = weakRefrece.get()
                if (imageview is ImageView) {
                    imageview.setImageBitmap(bitmap)
                }
            }
        }

        private fun findImageInSql(): Bitmap? {
            val db = MySqliteHandler.GetReadableDatabase()
            val table = "find_comment_image"
            val selection = "imageID=?"
            val cursor: Cursor
            val selectionArgs = arrayOf(imageID.toString())
            cursor = db.query(table, null, selection, selectionArgs, null, null, null)
            cursor.moveToFirst()
            if (!cursor.isAfterLast) {
                val imageIndex = cursor.getColumnIndex("image")
                val img = cursor.getBlob(imageIndex)
                cursor.close()
                return HandlePic.handlePic(ByteArrayInputStream(img), 0)
            }
            cursor.close()
            return null
        }

        private fun addImageToSql(imageData: ByteArray) {
            val table = "find_comment_image"
            val contentValues = ContentValues()
            contentValues.put("imageID", imageID)
            contentValues.put("image", imageData)
            val db = MySqliteHandler.GetWritableDatabase()
            db.insert(table, null, contentValues)
        }
    }

    internal fun getImage(imageID: Int, imageview: ImageView) {
        GetUserImageTask(imageID, imageview).execute()
    }

    override fun getItemCount(): Int {
        return datas.size
    }
    override fun getItem(position: Int): Any {
        return datas[position]
    }
}