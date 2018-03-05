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
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.BaseActivity.BaseTakeCameraActivity
import com.example.sunkai.heritage.ConnectWebService.BaseSetting
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.value.ERROR
import com.example.sunkai.heritage.value.UPDATE_SUCCESS
import org.kobjects.base64.Base64

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
            Glide.with(this).load(BaseSetting.URL+data!!.imageUrl).into(edit_comment_image)
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
        }
    }
    private fun getBytes():ByteArray?{
        val drawable = edit_comment_image.drawable
        return HandlePic.drawableToByteArray(drawable)
    }

    private fun updateUserCommentData(item: MenuItem) {
        val data=data
        data?.let {
            setViewsUnable()
            Thread {
                setDatas()
                val bytes= getBytes() ?: return@Thread
                val result = HandleFind.UpdateUserCommentInformaiton(data.id,data.commentTitle,data.commentContent,Base64.encode(bytes))
                runOnUiThread {
                    if (result!= ERROR) {
                        MakeToast.MakeText(getString(R.string.update_success))
                        data.imageUrl=result
                        val intent = Intent()
                        intent.putExtra("data", data)
                        intent.putExtra("image",bytes)
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
