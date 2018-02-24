package com.example.sunkai.heritage.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.LoginActivity.Companion.userID
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseBottomDialog
import com.example.sunkai.heritage.Adapter.UserCommentReplyRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew
import com.example.sunkai.heritage.ConnectWebService.HandleFindNew
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.value.UPDATE_SUCCESS
import com.example.sunkai.heritage.value.UPDATE_USER_COMMENT
import kotlinx.android.synthetic.main.activity_user_comment_detail.*

/**
 * 此类用于处理用户发帖详细信息页面
 */
class UserCommentDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var isReply = false
    private lateinit var information_img: ImageView
    private lateinit var information_title: TextView
    private lateinit var information_content: TextView
    private lateinit var reverse: TextView
    private lateinit var information_reply_num: TextView
    private lateinit var linearLayout4: LinearLayout
    internal var data: UserCommentData? = null

    private var isReverse = false

    /**
     * 记录传入进来的帖子在原帖的位置和ID
     */
    private var commentID: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_comment_detail)
        initView()
        getData()
        if (commentID != 0) {
            getReplysInfo(commentID)
        }
    }

    private fun getData() {
        val bundle = intent.extras
        if (bundle != null && bundle.getSerializable("data") is UserCommentData) {
            data = bundle.getSerializable("data") as UserCommentData
            val imageByte = intent.getByteArrayExtra("bitmap")
            commentID = data?.id ?: 0
            data?.let {
                setUserCommentView(data!!, imageByte)
            }
        } else {
            val id = intent.getIntExtra("id", 0)
            Log.d(TAG, "onCreate: getID:" + id)
            if (id == 0)
                return
        }
    }


    private fun initView() {
        information_img = findViewById(R.id.fragment_find_litview_img)
        information_title = findViewById(R.id.information_title)
        information_content = findViewById(R.id.information_content)
        information_reply_num = findViewById(R.id.information_reply_num)
        reverse = findViewById(R.id.user_comment_detail_reverse)
        linearLayout4 = findViewById(R.id.linearLayout4)
        userCommentAddReplyBtn.setOnClickListener(this)
        reverse.setOnClickListener(this)
        setSupportActionBar(userCommentDetailToolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun changeList() {
        reverseData()
        if (isReverse) {
            setTextViewBackup()
        } else {
            setTextViewReverse()
        }
        isReverse = !isReverse
    }

    private fun setTextViewReverse() {
        reverse.setText(R.string.reverse_look)
        reverse.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_arrow_upward_black_24dp), null)
    }

    private fun setTextViewBackup() {
        reverse.setText(R.string.non_reverse_look)
        reverse.setTextColor(ContextCompat.getColor(this, R.color.black))
        reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_arrow_downward_black_24dp), null)
    }

    private fun reverseData() {
        val adapter = userCommentReplyRecyclerView.adapter
        if (adapter is UserCommentReplyRecyclerAdapter) {
            adapter.reverseData()
        }
    }

    private fun setUserCommentView(data: UserCommentData, image: ByteArray?) {
        information_title.text = data.commentTitle
        information_content.text = data.commentContent
        information_reply_num.text = data.replyNum.toString()
        title = data.userName
        if (image != null) {
            information_img.setImageBitmap(HandlePic.handlePic(image))
        } else {
            Glide.with(this).load(BaseSettingNew.URL + data.imageUrl).into(information_img)
        }
        usercommentInformationLinear.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_quick)
        usercommentInformationLinear.startAnimation(animation)
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
            R.id.user_comment_detail_item_edit -> {
                data?.let {
                    val intent = Intent(this@UserCommentDetailActivity, ModifyUsercommentActivity::class.java)
                    val data: UserCommentData = data!!
                    intent.putExtra("data", data)
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

    private fun getReplysInfo(commentID: Int) {
        Thread {
            val datas = HandleFindNew.GetUserCommentReply(commentID)
            runOnUiThread {
                val adapter = UserCommentReplyRecyclerAdapter(this, datas)
                userCommentReplyRecyclerView.adapter = adapter
            }
        }.start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isReply) {
            val bundle = Bundle()
            val backIntent = Intent()
            backIntent.putExtras(bundle)
            setResult(ADD_COMMENT, backIntent)
            finish()
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.user_comment_detail_reverse -> changeList()
            R.id.userCommentAddReplyBtn -> {
                val dialog= BaseBottomDialog(this)
                dialog.setContentView(R.layout.add_usercomment_reply_dialog)
                dialog.show()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            UPDATE_USER_COMMENT -> {
                if (resultCode == UPDATE_SUCCESS) {
                    data?.let {
                        if (data.getSerializableExtra("data") is UserCommentData) {
                            setUserCommentView(data.getSerializableExtra("data") as UserCommentData, data.getByteArrayExtra("image"))
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

    //    internal inner class HandleReply(var content: String) {
//        var userID: Int = 0
//        var userName: String=""
//        var replyTime: String=""
//
//        var addReply: Runnable = Runnable {
//            val intent = Intent(GlobalContext.instance, UserCommentDetailActivity::class.java)
//            intent.putExtra("id", commentID)
//            val uriString = intent.toUri(Intent.URI_INTENT_SCHEME)
//            Log.d(TAG, "uriString: " + uriString)
//            val result = HandleFind.Add_User_Comment_Reply(userID, commentID, content, uriString)
//            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
//            replyTime = df.format(Date())
//            runOnUiThread {
//                if (result > 0) {
//                    val data = addDataToList(result)
//                    setView(data)
////                    resetWidge()
//                    isReply = true
//                    MakeToast.MakeText("回复成功")
//                    hideKeyboard(currentFocus)
//                } else {
//                    MakeToast.MakeText("发生错误，请稍后再试")
//                }
//            }
//        }
//
//        init {
//            this.userID = LoginActivity.userID
//            this.userName = LoginActivity.userName.orEmpty()
//        }
//
//        private fun addDataToList(result: Int): CommentReplyData {
//            val data = CommentReplyData()
//            data.replyContent = content
//            data.userName = userName
//            data.replyTime = replyTime
//            data.replyId = result
//            datas?.add(data)
//            return data
//        }

//        private fun resetWidge() {
//            progressBar.visibility = View.GONE
//            replyBtn.visibility = View.VISIBLE
//            information_reply_num.text = (Integer.parseInt(information_reply_num.text.toString()) + 1).toString()
//            replyEdit.setText("")
//        }
//    }
    //    private fun submit() {
//        if (TextUtils.isEmpty(replyEdit.text.toString().trim { it <= ' ' })) {
//            Toast.makeText(this@UserCommentDetailActivity, "回复不能为空", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (userID == 0) {
//            Toast.makeText(this, "没有登录", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.putExtra("isInto", 1)
//            startActivityForResult(intent, 1)
//            return
//        }
//        val content = replyEdit.text.toString()
//        replyBtn.visibility = View.GONE
//        progressBar.visibility = View.VISIBLE
//        /*
//         * 将回复的内容传给恢复类
//         */
//        val handleReply = HandleReply(content)
//        Thread(handleReply.addReply).start()
//    }
}
