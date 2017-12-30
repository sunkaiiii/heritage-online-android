package com.example.sunkai.heritage.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.sunkai.heritage.Activity.LoginActivity.Companion.userID

import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Data.FindActivityAllData
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.CommentReplyData
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast

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
            val bitmap = HandlePic.handlePic(this, ByteArrayInputStream(imageByte), 0)
            information_img.setImageBitmap(bitmap)
            commentID = data?.id?:0
            inListPosition = bundle.getInt("position")
            information_title.text = data?.commentTitle
            information_time.text = data?.commentTime
            information_content.text = data?.commentContent
            information_reply_num.text = data?.commentReplyNum
            title = data?.userName
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
            datas = getdatas
            runOnUiThread {
                hideKeyboard()
                for (data in datas!!) {
                    setView(data)
                }
            }
        }
    }


    private fun initView() {
        information_img = findViewById(R.id.information_img)
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
        replyBtn.setOnClickListener(this)
        reverse.setOnClickListener(this)
        progressBar = findViewById(R.id.user_comment_detail_progressbar)
        actionBack = supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)

    }


    //隐藏键盘
    private fun hideKeyboard() {
        val view = currentFocus
        view?.let {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        }
    }

    //显示键盘
    private fun showKeyboard() {
        val view = currentFocus
        view?.let {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, 1)
        }
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
            reverse.setTextColor(resources.getColor(R.color.colorPrimary))
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
            reverse.setTextColor(resources.getColor(R.color.black))
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
        hideKeyboard()
        while (iterator.hasPrevious()) {
            val data = iterator.previous()
            setView(data)
        }
    }

    private fun backupReverse() {
        LinearLayout_reply!!.removeAllViews()
        for (data in datas!!) {
            setView(data)
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
        AlertDialog.Builder(this).setTitle("是否删除帖子?").setPositiveButton("删除") { dialog, which ->
            @SuppressLint("InflateParams")
            val ad = AlertDialog.Builder(this).setView(LayoutInflater.from(this).inflate(R.layout.progress_view, null)).create()
            ad.show()
            Thread {
                val result = HandleFind.Delete_User_Comment_By_ID(data!!.id)
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
                    finish()
                }
            }.start()
        }.setNegativeButton("取消") { _, _ -> }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.user_comment_detail_item_delete -> deleteComment()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (data != null && data!!.user_id == userID) {
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
            val bitmap = HandlePic.handlePic(this, ByteArrayInputStream(imageByte), 0)
            information_img.setImageBitmap(bitmap)
        }
        commentID = data.id
        information_title.text = data.comment_title
        information_time.text = data.coment_time
        information_content.text = data.comment_content
        information_reply_num.text = data.replyCount
        title = data.userName
        //和老版本做一下兼容，复用代码
        this.data = UserCommentData()
        this.data!!.userName = data.userName
        this.data!!.commentReplyNum = data.replyCount
        this.data!!.id = data.id
        this.data!!.commentContent = data.comment_content
        this.data!!.user_id = data.userID
        this.data!!.commentTime = data.coment_time
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
                    hideKeyboard()
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

    companion object {
        val ADD_COMMENT = 1
        val DELETE_COMMENT = 2

        private val TAG = "UserCommentDetail"
    }
}
