package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import com.example.sunkai.heritage.Activity.BaseActivity.BaseTakeCameraActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.tools.toByteArray
import kotlinx.android.synthetic.main.activity_add_find_comment.*

/**
 * 此类是处理发帖活动的类
 */

class AddFindCommentActivity : BaseTakeCameraActivity(), View.OnClickListener {


    private var isSavePicture = false//图片上传状态
    private var isHadImage = false//用户是否添加图片
    private var item: MenuItem? = null
    private var imageBitmap: Bitmap? = null
    private var firstIn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_find_comment)
        add_comment_image.imageTintList = ColorStateList.valueOf(getThemeColor())
    }

    override fun onStart() {
        super.onStart()
        if (firstIn) {
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

    companion object {
        const val ADD_OK = 1
    }
}
