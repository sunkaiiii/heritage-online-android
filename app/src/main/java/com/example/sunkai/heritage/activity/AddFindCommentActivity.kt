package com.example.sunkai.heritage.activity

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.edit
import com.example.sunkai.heritage.activity.baseActivity.BaseTakeCameraActivity
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.dialog.NormalWarningDialog
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.tools.toBitmap
import com.example.sunkai.heritage.tools.toByteArray
import com.example.sunkai.heritage.value.ADD_COMMENT
import com.example.sunkai.heritage.value.COMMENT_CONTENT
import com.example.sunkai.heritage.value.COMMENT_IMAGE_URI
import com.example.sunkai.heritage.value.COMMENT_TITLE
import kotlinx.android.synthetic.main.activity_add_find_comment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * 此类是处理发帖活动的类
 */

class AddFindCommentActivity : BaseTakeCameraActivity(), View.OnClickListener {


    private var isSavePicture = false//图片上传状态
    private var isHadImage = false//用户是否添加图片
    private var item: MenuItem? = null
    private var imageBitmap: Bitmap? = null
    private var firstIn = true
    private var isRestoneData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_find_comment)
        add_comment_image.imageTintList = ColorStateList.valueOf(getThemeColor())
        restoneData()
    }

    override fun onStart() {
        super.onStart()
        if (firstIn && !isRestoneData) {
            startChoosePhoto()
            firstIn = false
        }
    }

    private fun startChoosePhoto() {
        add_comment_image.performClick()
    }

    private fun submit() {
        //判断是否正在上传图片，防止重复上传
        if (isSavePicture) return
        val title = add_comment_title.text.toString().trim()
        val content = add_comment_content.text.toString().trim()
        if (checkValues(title, content)) {
            isSavePicture = true
            setItemStates(true)
            requestHttp {
                addCommentInformation(title, content)
            }
        }
    }

    private fun setItemStates(isUpLoad: Boolean) {
        item?.actionView = if (isUpLoad) ProgressBar(this) else null
        item?.isEnabled = !isUpLoad
    }

    //参数校验
    private fun checkValues(title: String, content: String): Boolean {
        if (TextUtils.isEmpty(title)) {
            toast("标题不能为空")
            return false
        }
        if (TextUtils.isEmpty(content)) {
            toast("内容不能为空")
            return false
        }
        if (!isHadImage) {
            toast("请添加图片")
            return false
        }
        return true
    }

    private fun addCommentInformation(title: String, content: String) {
        val addImageBitmap = imageBitmap ?: return
        val imageCode = Base64.encodeToString(addImageBitmap.toByteArray(), Base64.DEFAULT)
        val result = HandleFind.Add_User_Comment_Information(LoginActivity.userID, title, content, imageCode)
        if (isDestroy) return
        runOnUiThread {
            setItemStates(false)
            isSavePicture = false
            toast(if (result) "添加成功" else "出现错误，请稍后再试")
            if (result) {
                setResult(ADD_OK, intent)
                finish()
            }
        }
    }

    //BaseTakeCameraActivity图片读取回调
    override fun setImageToImageView(bitmap: Bitmap) {
        imageBitmap = bitmap
        add_comment_image.setImageBitmap(bitmap)
        isHadImage = true
        add_comment_image.imageTintList = null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save_comment -> submit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {}

    override fun getNeedOpenChooseImageView(): Array<View> {
        return arrayOf(add_comment_image)
    }


    //重写onCreateOptionsMenu方法，在顶部的bar中显示菜单
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_coment_menu, menu)
        item = menu.findItem(R.id.action_save_comment)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        //释放bitmap
        imageBitmap = null
    }

    override fun onBackPressed() {
        if (isHadImage) {
            NormalWarningDialog()
                    .setTitle("保留此次编辑?")
                    .setSubmitText("保留")
                    .setCancelText("不保留")
                    .setOnSubmitClickListener(object : NormalWarningDialog.onSubmitClickListener {
                        override fun onSubmit(view: View, dialog: NormalWarningDialog) {
                            saveInstance()
                            isHadImage = false
                            onBackPressed()
                        }
                    }).setOnCancelClickListener(object : NormalWarningDialog.onCancelClickListener {
                        override fun onCanceled(view: View, dialog: NormalWarningDialog) {
                            isHadImage = false
                            getSharedPreferences(ADD_COMMENT, Context.MODE_PRIVATE).edit().clear().apply()
                            onBackPressed()
                        }
                    }).show(supportFragmentManager, "提醒")
        } else {
            super.onBackPressed()
        }
    }

    private fun saveInstance() {
        val title = add_comment_title.text.toString().trim()
        val content = add_comment_content.text.toString().trim()
        getSharedPreferences(ADD_COMMENT, Context.MODE_PRIVATE).edit {
            putString(COMMENT_TITLE, title)
            putString(COMMENT_CONTENT, content)
            val bitmap = imageBitmap
            bitmap?.let {
                putString(COMMENT_IMAGE_URI, Base64.encodeToString(it.toByteArray(), Base64.DEFAULT))
            }
        }
    }

    private fun restoneData() {
        val sharedPreferences = getSharedPreferences(ADD_COMMENT, Context.MODE_PRIVATE)
        if (sharedPreferences.all.isEmpty()) {
            return
        }
        add_comment_title.setText(sharedPreferences.getString(COMMENT_TITLE, ""))
        add_comment_content.setText(sharedPreferences.getString(COMMENT_CONTENT, ""))
        val uriString = sharedPreferences.getString(COMMENT_IMAGE_URI, "")
        if (!uriString.isNullOrEmpty()) {
            GlobalScope.launch {
                val task = GlobalScope.async {
                    Base64.decode(uriString, Base64.DEFAULT).toBitmap()
                }
                val result = task.await()
                setImageToImageView(result ?: return@launch)
            }
        }
        isRestoneData = true
    }

    companion object {
        const val ADD_OK = 1
    }
}
