package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.animation.AnimationUtils
import androidx.core.transition.doOnEnd
import androidx.core.widget.toast
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.BaseActivity.BaseHandleCollectActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity.Companion.userID
import com.example.sunkai.heritage.Adapter.UserCommentReplyRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSetting
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Data.CommentReplyInformation
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.Dialog.AddUserCommentBottomDialog
import com.example.sunkai.heritage.Interface.AddUserReplyDialog
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.activity_user_comment_detail.*

/**
 * 此类用于处理用户发帖详细信息页面
 */
class UserCommentDetailActivity : BaseHandleCollectActivity(), View.OnClickListener, OnPageLoaded {

    private var isReply = false
    internal var data: UserCommentData? = null

    private var isReverse = false

    //用于收藏的id
    private var commentID: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_comment_detail)
        initView()
        getData()
        //从发现页点进来的时候执行动画，其他页面点进来不执行动画
        //当动画完成后，再显示帖子标题栏
        //防止在动画行进的时候，标题栏遮挡图片的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && intent.getIntExtra(FROM, DEFAULT_FROM) != FROM_COLLECTION) {
            window.sharedElementEnterTransition.doOnEnd {
                getReplysInfo(commentID)
                showBackLinear()
            }
        } else {
            getReplysInfo(commentID)
            showBackLinear()
        }
    }

    private fun getData() {
        if (intent.getSerializableExtra(DATA) is UserCommentData) {
            data = intent.getSerializableExtra(DATA) as UserCommentData
            val data = data
            commentID = data?.id ?: 0
            data?.let {
                setUserCommentView(data, null)
            }

        } else {
            //从我的消息页面进入的时候，只会传来帖子id，这时候要获取全部的内容
            val id=intent.getIntExtra(ID,0)
            commentID=id
            ThreadPool.execute {
                if(LoginActivity.userID==0||id==0) finish()
                val userCommentData=HandleFind.GetAllUserCommentInfoByID(LoginActivity.userID,id)?:return@execute
                runOnUiThread {
                    setUserCommentView(userCommentData,null)
                    showBackLinear()
                    getReplysInfo(commentID)
                }
            }
        }
    }


    private fun initView() {
        userCommentAddReplyBtn.setOnClickListener(this)
        userCommentDetailReverse.setOnClickListener(this)
        setSupportActionBar(userCommentDetailToolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        userCommentSwipeRefresh.setOnRefreshListener {
            if (commentID != 0) {
                getReplysInfo(commentID)
            }
        }
    }

    private fun showBackLinear() {
        usercommentInformationLinear.visibility = View.VISIBLE
        val option = intent.getIntExtra(OPTION, COMMON_SHOW)
        if (option == ANIMATION_SHOW) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_quick)
            usercommentInformationLinear.startAnimation(animation)
        }
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
        userCommentDetailReverse.setText(R.string.reverse_look)
        userCommentDetailReverse.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        userCommentDetailReverse.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_arrow_upward_black_24dp), null)
    }

    private fun setTextViewBackup() {
        userCommentDetailReverse.setText(R.string.non_reverse_look)
        userCommentDetailReverse.setTextColor(ContextCompat.getColor(this, R.color.black))
        userCommentDetailReverse.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_arrow_downward_black_24dp), null)
    }

    private fun reverseData() {
        val adapter = userCommentReplyRecyclerView.adapter
        if (adapter is UserCommentReplyRecyclerAdapter) {
            adapter.reverseData()
        }
    }

    private fun setUserCommentView(data: UserCommentData, image: ByteArray?) {
        this.data=data
        informationTitle.text = data.commentTitle
        informationContent.text = data.commentContent
        informationReplyNum.text = data.replyNum.toString()
        title = data.userName
        if (image != null) {
            userCommentImage.setImageBitmap(HandlePic.handlePic(image))
        } else {
            Glide.with(this).load(BaseSetting.URL + data.imageUrl).into(userCommentImage)
        }
    }

    private fun deleteComment() {
        AlertDialog.Builder(this).setTitle("是否删除帖子?").setPositiveButton("删除") { _, _ ->
            val ad = AlertDialog.Builder(this).setView(LayoutInflater.from(this).inflate(R.layout.progress_view, userCommentReplyAppbar, false)).create()
            ad.show()
            ThreadPool.execute {
                val result = HandleFind.DeleteUserCommentByID(data!!.id)
                runOnUiThread {
                    if (ad.isShowing) {
                        ad.dismiss()
                    }
                    if (result) {
                        toast(resources.getString(R.string.delete_success))
                    } else {
                        toast(resources.getString(R.string.has_problem))
                    }
                    setResult(DELETE_COMMENT)
                    onBackPressed()
                }
            }
        }.setNegativeButton("取消") { _, _ -> }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.user_comment_detail_item_delete -> deleteComment()
            R.id.user_comment_detail_item_edit -> {
                val data=data
                data?.let {
                    val intent = Intent(this@UserCommentDetailActivity, ModifyUsercommentActivity::class.java)
                    intent.putExtra(DATA, data)
                    startActivityForResult(intent, UPDATE_USER_COMMENT)
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //如果登陆用户是作者，则显示编辑和删除帖子的menu，否则，显示收藏menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val comemntData = data
        if (comemntData != null && comemntData.userID == userID) {
            menuInflater.inflate(R.menu.user_comment_detail_menu, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun getType(): String {
        return TYPE_FIND
    }

    override fun getID(): Int? {
        return if (commentID == 0) null else commentID
    }

    private fun getReplysInfo(commentID: Int) {
        if (commentID == 0) return
        onPreLoad()
        ThreadPool.execute {
            val datas = HandleFind.GetUserCommentReply(commentID)
            runOnUiThread {
                onPostLoad()
                val adapter = UserCommentReplyRecyclerAdapter(this, datas)
                userCommentReplyRecyclerView.adapter = adapter
            }
        }
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

    private fun generateDialog(commentID: Int): AddUserCommentBottomDialog? {
        val dialog = AddUserCommentBottomDialog(this, commentID,data?:return null)
        dialog.setOnAddUserReplyListener(object : AddUserReplyDialog {
            override fun onAddUserReplySuccess(data: CommentReplyInformation) {
                val adapter = userCommentReplyRecyclerView.adapter
                if (adapter is UserCommentReplyRecyclerAdapter) {
                    adapter.addData(data)
                    userCommentReplyRecyclerView.smoothScrollToPosition(adapter.itemCount)
                    informationReplyNum.text = (informationReplyNum.text.toString().toInt() + 1).toString()
                }
            }
        })
        return dialog
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.userCommentDetailReverse -> changeList()
            R.id.userCommentAddReplyBtn -> generateDialog(commentID)?.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //清除adapter的内容，防止返回时候的界面显示的问题
        this.commentID = 0
        userCommentReplyRecyclerView.adapter = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            UPDATE_USER_COMMENT -> {
                if (resultCode == UPDATE_SUCCESS) {
                    data?.let {
                        if (data.getSerializableExtra(DATA) is UserCommentData) {
                            setUserCommentView(data.getSerializableExtra(DATA) as UserCommentData, data.getByteArrayExtra("image"))
                        }
                    }
                }
            }
        }
    }

    override fun onPreLoad() {
        userCommentReplyRecyclerView.adapter = null
        userCommentSwipeRefresh.isRefreshing = true
    }

    override fun onPostLoad() {
        userCommentSwipeRefresh.isRefreshing = false
    }

    companion object {
        const val ADD_COMMENT = 1
        const val DELETE_COMMENT = 2
        const val COMMON_SHOW = 3
        const val ANIMATION_SHOW = 4
        const val FROM_COLLECTION = 0
        const val DEFAULT_FROM = -1
    }
}