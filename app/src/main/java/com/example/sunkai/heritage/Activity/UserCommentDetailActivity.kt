package com.example.sunkai.heritage.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.LoginActivity.Companion.userID
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.ConnectWebService.HandleFindNew
import com.example.sunkai.heritage.Data.*
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.SoftInputTools.hideKeyboard
import com.example.sunkai.heritage.value.UPDATE_SUCCESS
import com.example.sunkai.heritage.value.UPDATE_USER_COMMENT
import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

/**
 * 此类用于处理用户发帖详细信息页面
 */
class UserCommentDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var isReply = false
    private lateinit var information_img: ImageView
    private lateinit var information_title: TextView
    private lateinit var information_time: TextView
    private lateinit var information_content: TextView
    private lateinit var information_username: TextView
    private lateinit var reverse: TextView
    private lateinit var information_reply_num: TextView
    private lateinit var linearLayout4: LinearLayout
    private lateinit var LinearLayout_reply: LinearLayout
    private lateinit var replyEdit: EditText
    private lateinit var replyBtn: Button
    private lateinit var progressBar: ProgressBar
    internal var data: UserCommentData? = null
    internal var datas: MutableList<CommentReplyData>? = null
    private var actionBack: ActionBar? = null

    private var isReverse = false

    /**
     * 记录传入进来的帖子在原帖的位置和ID
     */
    private var commentID: Int = 0
    private var inListPosition: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_comment_detail)
        initView()
        getData()

    }

    private fun getData() {
        val bundle = intent.extras
        if (bundle != null && bundle.getSerializable("data") is UserCommentData) {
            data = bundle.getSerializable("data") as UserCommentData
            val imageByte = intent.getByteArrayExtra("bitmap")
            commentID = data?.id?:0
            inListPosition = bundle.getInt("position")
            data?.let{
                setUserCommentView(data!!,imageByte)
            }
            Thread(getReply).start()
        } else {
            val id = intent.getIntExtra("id", 0)
            Log.d(TAG, "onCreate: getID:" + id)
            if (id == 0)
                return
            getCommentInfo(id, this).execute()
        }
    }

    internal var getReply: Runnable = Runnable {
        val getdatas = HandleFind.Get_User_Comment_Reply(data!!.id)
        if (getdatas != null) {
            datas = getdatas.toMutableList()
            runOnUiThread {
                hideKeyboard(currentFocus)
                for (data in datas!!) {
                    setView(data)
                }
            }
        }
}


    private fun initView() {
        information_img = findViewById(R.id.fragment_find_litview_img)
        information_title = findViewById(R.id.information_title)
        information_time = findViewById(R.id.information_time)
        information_content = findViewById(R.id.information_content)
        information_username = findViewById(R.id.information_username)
        information_reply_num = findViewById(R.id.information_reply_num)
        reverse = findViewById(R.id.user_comment_detail_reverse)
        linearLayout4 = findViewById(R.id.linearLayout4)
        LinearLayout_reply = findViewById(R.id.LinearLayout_reply)
        replyEdit = findViewById(R.id.reply_edittext)
        replyBtn = findViewById(R.id.reply_button)
        progressBar = findViewById(R.id.user_comment_detail_progressbar)

        replyBtn.setOnClickListener(this)
        reverse.setOnClickListener(this)
        actionBack = supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)

        information_img.isDrawingCacheEnabled=true

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.reply_button -> submit()
            R.id.user_comment_detail_reverse -> changeList()
        }
    }

    private fun submit() {
        if (TextUtils.isEmpty(replyEdit.text.toString().trim { it <= ' ' })) {
            Toast.makeText(this@UserCommentDetailActivity, "回复不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        if (userID == 0) {
            Toast.makeText(this, "没有登录", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("isInto", 1)
            startActivityForResult(intent, 1)
            return
        }
        val content = replyEdit.text.toString()
        replyBtn.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        /*
         * 将回复的内容传给恢复类
         */
        val handleReply = HandleReply(content)
        Thread(handleReply.addReply).start()
    }

    private fun changeList() {
        if (isReverse) {
            backupReverse()
            setTextViewBackup()
        } else {
            setReverse()
            setTextViewReverse()
        }
        isReverse = !isReverse
    }

    private fun setTextViewReverse() {
        reverse.setText(R.string.reverse_look)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reverse.setTextColor(getColor(R.color.colorPrimary))
        } else {
            reverse.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_arrow_upward_black_24dp), null)
        } else {
            reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.ic_arrow_upward_black_24dp), null)
        }
    }

    private fun setTextViewBackup() {
        reverse.setText(R.string.non_reverse_look)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reverse.setTextColor(getColor(R.color.black))
        } else {
            reverse.setTextColor(ContextCompat.getColor(this,R.color.black))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_arrow_downward_black_24dp), null)
        } else {
            reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.ic_arrow_downward_black_24dp), null)
        }
    }

    private fun setReverse() {
        LinearLayout_reply.removeAllViews()
        val iterator = datas!!.listIterator(datas!!.size)
        hideKeyboard(currentFocus)
        while (iterator.hasPrevious()) {
            val data = iterator.previous()
            setView(data)
        }
    }

    private fun backupReverse() {
        LinearLayout_reply.removeAllViews()
        for (data in datas!!) {
            setView(data)
        }
    }
    private fun setUserCommentView(data:UserCommentData,image:ByteArray?){
        information_title.text = data.commentTitle
        information_time.text = data.commentTime
        information_content.text = data.commentContent
        information_reply_num.text = data.replyNum.toString()
        title = data.userName
        if(image!=null) {
            information_img.setImageBitmap(HandlePic.handlePic(image))
        }else{
            Glide.with(this).load(BaseSettingNew.URL+data.imageUrl).into(information_img)
        }
    }
    private fun setView(data: CommentReplyData) {
        val inflater = layoutInflater
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.user_comment_reply_information, null)
        val vh = Holder()
        vh.name = view.findViewById(R.id.reply_name)
        vh.time = view.findViewById(R.id.reply_time)
        vh.content = view.findViewById(R.id.reply_content)
        vh.name.text = data.userName
        vh.time.text = data.replyTime
        vh.content.text = data.replyContent
        LinearLayout_reply.addView(view)
    }

    private fun deleteComment() {
        AlertDialog.Builder(this).setTitle("是否删除帖子?").setPositiveButton("删除") { _, _ ->
            @SuppressLint("InflateParams")
            val ad = AlertDialog.Builder(this).setView(LayoutInflater.from(this).inflate(R.layout.progress_view, null)).create()
            ad.show()
            Thread {
                val result = HandleFindNew.DeleteUserCommentByID(data!!.id)
                runOnUiThread {
                    if (ad.isShowing) {
                        ad.dismiss()
                    }
                    if (result) {
                        MakeToast.MakeText(resources.getString(R.string.delete_success))
                    } else {
                        MakeToast.MakeText(resources.getString(R.string.has_problem))
                    }
                    setResult(DELETE_COMMENT, intent)
                    onBackPressed()
                }
            }.start()
        }.setNegativeButton("取消") { _, _ -> }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.user_comment_detail_item_delete -> deleteComment()
            R.id.user_comment_detail_item_edit->{
                data?.let {
                    val intent = Intent(this@UserCommentDetailActivity, ModifyUsercommentActivity::class.java)
                    val data: UserCommentData = data!!
                    intent.putExtra("data",data)
                    startActivityForResult(intent, UPDATE_USER_COMMENT)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (data != null && data!!.userID == userID) {
            menuInflater.inflate(R.menu.user_comment_detail_menu, menu)
        }
        return true
    }

    internal class getCommentInfo(private val id: Int, userCommentDetail: UserCommentDetailActivity) : AsyncTask<Void, Void, FindActivityAllData>() {
        private val userCommentDetailWeakReference: WeakReference<UserCommentDetailActivity>

        init {
            userCommentDetailWeakReference = WeakReference(userCommentDetail)
        }

        override fun doInBackground(vararg voids: Void): FindActivityAllData? {

            return HandleFind.Get_All_User_Coment_Info_By_ID(LoginActivity.userID, id)
        }

        override fun onPostExecute(findActivityAllData: FindActivityAllData?) {
            val userCommentDetail = userCommentDetailWeakReference.get()
            if (userCommentDetail == null || findActivityAllData == null)
                return
            userCommentDetail.allDataSetView(findActivityAllData)
            Thread(userCommentDetail.getReply).start()
        }
    }

    private fun allDataSetView(data: FindActivityAllData) {
        if (data.imgCode != null) {
            val imageByte = org.kobjects.base64.Base64.decode(data.imgCode!!)
            val bitmap = HandlePic.handlePic(ByteArrayInputStream(imageByte), 0)
            information_img.setImageBitmap(bitmap)
        }
        commentID = data.id
        information_title.text = data.comment_title
        information_time.text = data.coment_time
        information_content.text = data.comment_content
        information_reply_num.text = data.replyCount
        title = data.userName
    }

    internal inner class HandleReply(var content: String) {
        var userID: Int = 0
        var userName: String=""
        var replyTime: String=""

        var addReply: Runnable = Runnable {
            val intent = Intent(GlobalContext.instance, UserCommentDetailActivity::class.java)
            intent.putExtra("id", commentID)
            val uriString = intent.toUri(Intent.URI_INTENT_SCHEME)
            Log.d(TAG, "uriString: " + uriString)
            val result = HandleFind.Add_User_Comment_Reply(userID, commentID, content, uriString)
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            replyTime = df.format(Date())
            runOnUiThread {
                if (result > 0) {
                    val data = addDataToList(result)
                    setView(data)
                    resetWidge()
                    isReply = true
                    MakeToast.MakeText("回复成功")
                    hideKeyboard(currentFocus)
                } else {
                    MakeToast.MakeText("发生错误，请稍后再试")
                }
            }
        }

        init {
            this.userID = LoginActivity.userID
            this.userName = LoginActivity.userName.orEmpty()
        }

        private fun addDataToList(result: Int): CommentReplyData {
            val data = CommentReplyData()
            data.replyContent = content
            data.userName = userName
            data.replyTime = replyTime
            data.replyId = result
            datas?.add(data)
            return data
        }

        private fun resetWidge() {
            progressBar.visibility = View.GONE
            replyBtn.visibility = View.VISIBLE
            information_reply_num.text = (Integer.parseInt(information_reply_num.text.toString()) + 1).toString()
            replyEdit.setText("")
        }
    }

    internal inner class Holder {
        lateinit var name: TextView
        lateinit var time: TextView
        lateinit var content: TextView
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isReply) {
            val bundle = Bundle()
            bundle.putInt("commentID", commentID)
            bundle.putInt("position", inListPosition)
            val backIntent = Intent()
            backIntent.putExtras(bundle)
            setResult(ADD_COMMENT, backIntent)
            finish()
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            UPDATE_USER_COMMENT->{
                if(resultCode== UPDATE_SUCCESS) {
                    data?.let {
                        if (data.getSerializableExtra("data") is UserCommentData) {
                            setUserCommentView(data.getSerializableExtra("data") as UserCommentData,data.getByteArrayExtra("image"))
                        }
                    }
                }
            }
        }
    }
    companion object {
        const val ADD_COMMENT = 1
        const val DELETE_COMMENT = 2

        private const val TAG = "UserCommentDetail"
    }
}
