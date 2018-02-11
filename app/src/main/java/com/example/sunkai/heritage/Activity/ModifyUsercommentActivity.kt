package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.sunkai.heritage.Activity.BaseActivity.BaseTakeCameraActivity
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.value.UPDATE_SUCCESS
import java.io.ByteArrayInputStream

class ModifyUsercommentActivity : BaseTakeCameraActivity(), View.OnClickListener {
    private lateinit var edit_comment_title: EditText
    private lateinit var edit_comment_content: EditText
    private lateinit var edit_comment_image: ImageView

    var data: UserCommentData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_usercomment)
        initview()
        if (intent?.getSerializableExtra("data") is UserCommentData) {
            data = intent?.getSerializableExtra("data") as UserCommentData
            edit_comment_title.setText(data!!.commentTitle)
            edit_comment_content.setText(data!!.commentContent)
            edit_comment_image.setImageBitmap(HandlePic.handlePic(ByteArrayInputStream(data!!.userImage), 0))
        }
    }

    internal fun initview() {
        edit_comment_title = findViewById(R.id.add_comment_title)
        edit_comment_content = findViewById(R.id.add_comment_content)
        edit_comment_image = findViewById(R.id.add_comment_image)

        edit_comment_image.setOnClickListener(this)

        edit_comment_image.isDrawingCacheEnabled = true
    }

    private fun setViewsIsEnable(isEnable: Boolean) {
        edit_comment_title.isEnabled = isEnable
        edit_comment_content.isEnabled = isEnable
        edit_comment_image.isEnabled = isEnable
    }

    private fun setViewsUnable() {
        setViewsIsEnable(false)
    }

    private fun setViewsEnable() {
        setViewsIsEnable(true)
    }

    private fun setDatas() {
        data?.let {
            data!!.commentTitle = edit_comment_title.text.toString()
            data!!.commentContent = edit_comment_content.text.toString()
            val drawable = edit_comment_image.drawable
            val bytes = HandlePic.drawableToByteArray(drawable)
            bytes?.let {
                data!!.userImage = bytes
            }
        }
    }

    private fun updateUserCommentData(item: MenuItem) {
        data?.let {
            setViewsUnable()
            Thread {
                setDatas()
                val result = HandleFind.Update_User_Comment_Informaiton(data!!)
                runOnUiThread {
                    if (result) {
                        MakeToast.MakeText(getString(R.string.update_success))
                        val intent = Intent()
                        intent.putExtra("data", data)
                        setResult(UPDATE_SUCCESS, intent)
                        finish()
                    } else {
                        MakeToast.MakeText(getString(R.string.update_fail))
                        setViewsEnable()
                        item.isEnabled = true
                        item.actionView = null
                    }
                }

            }.start()
        }
    }

    override fun setImageToImageView(bitmap: Bitmap) {
        edit_comment_image.setImageBitmap(bitmap)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_comment_image -> chooseAlertDialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.modify_comment_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.modify_comment_menu_modify -> {
                val progressBar = ProgressBar(this@ModifyUsercommentActivity)
                item.actionView = progressBar
                item.isEnabled = false
                updateUserCommentData(item)
            }
        }
        return true
    }
}
