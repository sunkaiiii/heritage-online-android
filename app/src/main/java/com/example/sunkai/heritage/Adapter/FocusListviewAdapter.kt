package com.example.sunkai.heritage.Adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sunkai.heritage.Activity.OtherUsersActivity

import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.FocusData
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.R
import com.makeramen.roundedimageview.RoundedImageView

import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference

/*
 * Created by sunkai on 2017-5-2.
 */

class FocusListviewAdapter
/**
 *
 * @param datas 关注、粉丝的数据
 * @param what  1为关注，2为粉丝3为查询页面
 */
(private val context: Context, var datas: List<FocusData>, var what: Int) : BaseAdapter() {
    private val checkFolloweachOther = Runnable {
        for (i in datas.indices) {
            val data = datas[i]
            datas[i].followeachother = HandlePerson.Check_Follow_Eachohter(data.focusUserid, data.focusFansID)
            if (datas[i].followeachother && (what == 2 || what == 3)) {
                datas[i].isCheck = true
            }
            checkFolloweachotherHandler.sendEmptyMessage(1)
        }
    }
    private val checkFolloweachotherHandler = object : Handler(GlobalContext.instance.mainLooper) {
        override fun handleMessage(msg: Message) {
            notifyDataSetChanged()
            if (what == 3) {
                Thread(isUserFollow).start()
            }
        }
    }

    private val isUserFollow = Runnable {
        for (i in datas.indices) {
            val data = datas[i]
            datas[i].isCheck = HandlePerson.is_User_Follow(data.focusUserid, data.focusFansID)
        }
        isUserFollowHandler.sendEmptyMessage(1)
    }
    private val isUserFollowHandler = object : Handler(GlobalContext.instance.mainLooper) {
        override fun handleMessage(msg: Message) {
            notifyDataSetChanged()
        }
    }

    init {
        if (what == 2) {
            for (i in datas.indices) {
                datas[i].isCheck = false
            }
        }
        Thread(checkFolloweachOther).start()
    }

    override fun getCount(): Int {
        return datas.size
    }

    override fun getItem(position: Int): Any {
        return datas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view:View
        val vh: Holder
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.focus_listview_layout, null)
            vh = Holder()
            vh.userName = view.findViewById(R.id.user_name)
            vh.userIntrodeuce = view.findViewById(R.id.user_introduce)
            vh.userImage = view.findViewById(R.id.user_head_image)
            vh.focusBtn = view.findViewById(R.id.focus_btn)
            vh.rl_focus_listview_layout=view.findViewById(R.id.rl_focus_listview_layout)
            view.tag = vh
        } else {
            view=convertView
            vh = convertView.tag as Holder
        }
        val data = datas[position]
        val userName = data.name
        vh.userName.text = userName
        vh.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
        if (data.followeachother) {
            vh.focusBtn.text = "互相关注"
        } else {
            if (data.getCheck()) {
                vh.focusBtn.text = "已关注"
            } else {
                vh.focusBtn.text = "未关注"
            }
        }
        val btn = vh.focusBtn
        /*
         * 在点击关注、取关的时候，页面文字改变，提示用户正在响应，并禁止按钮点击以防止错误的发生
         */
        vh.focusBtn.setOnClickListener { _ ->
            val handleFocus = handleFocus(data, position, btn)
            btn.text = "操作中"
            btn.isEnabled = false
            if (data.getCheck()) {
                handleFocus.CancelFollow()
            } else {
                handleFocus.AddFollow()
            }
        }
        if(what!=2)
            getUserImage(data.focusFansID,vh.userImage).execute()
        else
            getUserImage(data.focusUserid,vh.userImage).execute()
        vh.rl_focus_listview_layout.setOnClickListener({
            val intent=Intent(context,OtherUsersActivity::class.java)
            when(what){
                1,3->intent.putExtra("userID",data.focusFansID)
                2->intent.putExtra("userID",data.focusUserid)
            }
            context.startActivity(intent)
        })
        return view
    }

    internal inner class Holder {
        lateinit var rl_focus_listview_layout:RelativeLayout
        lateinit var userName: TextView
        lateinit var userIntrodeuce: TextView
        lateinit var userImage: RoundedImageView
        lateinit var focusBtn: Button
    }


    internal class getUserImage(val userID:Int,val imageview:ImageView):AsyncTask<Void,Void, Bitmap?>(){
        val imageviewWeakRefrence:WeakReference<ImageView>
        init {
            this.imageviewWeakRefrence= WeakReference(imageview)
        }
        override fun doInBackground(vararg params: Void?): Bitmap? {
            val imageView=imageviewWeakRefrence.get()
            imageView.let {
                var db=MySqliteHandler.GetReadableDatabase()
                val tableName="person_image"
                val selection = "imageID=?"
                val selectionArgs = arrayOf(userID.toString())
                val cursor=db.query(tableName,null,selection,selectionArgs,null,null,null)
                cursor.moveToFirst()
                if(!cursor.isAfterLast){
                    val imageIndex = cursor.getColumnIndex("image")
                    val image = cursor.getBlob(imageIndex)
                    cursor.close()
                    return HandlePic.handlePic(ByteArrayInputStream(image),0)
                }
                val image=HandlePerson.Get_User_Image(userID)
                image?.let {
                    val imageByte=org.kobjects.base64.Base64.decode(image)
                    val contentValues = ContentValues()
                    contentValues.put("imageID", userID)
                    contentValues.put("image", imageByte)
                    db = MySqliteHandler.GetWritableDatabase()
                    db.insert(tableName, null, contentValues)
                    return HandlePic.handlePic(ByteArrayInputStream(imageByte),0)
                }
            }
            return null
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            bitmap?.let {
                val imageView=imageviewWeakRefrence.get()
                imageView?.let {
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }
    internal inner class handleFocus(var data: FocusData, var position: Int, var btn: Button) {
        private val AddFollow = Runnable {
            var result = false
            if (what == 1 || what == 3) {
                result = HandlePerson.Add_Focus(data.focusUserid, data.focusFansID)
            } else if (what == 2) {
                result = HandlePerson.Add_Focus(data.focusFansID, data.focusUserid)
            }
            if (result) {
                AddFollowHandler.sendEmptyMessage(1)
            } else {
                AddFollowHandler.sendEmptyMessage(0)
            }
        }
        private val CancelFollow = Runnable {
            var result = false
            if (what == 1 || what == 3) {
                result = HandlePerson.Cancel_Focus(data.focusUserid, data.focusFansID)
            } else if (what == 2) {
                result = HandlePerson.Cancel_Focus(data.focusFansID, data.focusUserid)
            }
            if (result) {
                CancelFollowHandler.sendEmptyMessage(1)
            } else {
                CancelFollowHandler.sendEmptyMessage(0)
            }
        }
        private val CancelFollowHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                btn.isEnabled = true
                if (msg.what == 1) {
                    datas[position].isCheck = false
                    Toast.makeText(context, "取消关注成功", Toast.LENGTH_SHORT).show()
                    notifyDataSetChanged()
                    /*
                     * 用户取关成功，发送广播给personFragment，使其重新加载粉丝、关注数据
                     */
                    datas[position].followeachother = false
                    val intent = Intent("android.intent.action.focusAndFansCountChange")
                    intent.putExtra("message", "change")
                    context.sendBroadcast(intent)
                    //                    new Thread(checkFolloweachOther).start();
                } else {
                    Toast.makeText(context, "操作失败，请稍后再试", Toast.LENGTH_SHORT).show()
                }
            }
        }
        private val AddFollowHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                btn.isEnabled = true
                if (msg.what == 1) {
                    datas[position].isCheck = true
                    Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show()
                    Thread(checkFolloweachOther).start()
                    /*
                     * 用户关注成功，发送广播给personFragment，使其重新加载粉丝、关注数据
                     * 因为是关注用户，重新运行查看互关进程，判断是否为互相关注
                     */
                    notifyDataSetChanged()
                    val intent = Intent("android.intent.action.focusAndFansCountChange")
                    intent.putExtra("message", "change")
                    context.sendBroadcast(intent)
                } else {
                    Toast.makeText(context, "操作失败，请稍后再试", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * 关注、取关
         */
        fun CancelFollow() {
            Thread(CancelFollow).start()
        }

        fun AddFollow() {
            Thread(AddFollow).start()
        }
    }
}
