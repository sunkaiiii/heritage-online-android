package com.example.sunkai.heritage.Activity

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

import com.example.sunkai.heritage.Activity.BaseActivity.BaseTakeCameraActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import kotlinx.android.synthetic.main.activity_add_find_comment.*
import org.kobjects.base64.Base64

import java.io.ByteArrayOutputStream

/**
 * 此类是处理发帖活动的类
 */

class AddFindCommentActivity : BaseTakeCameraActivity(), View.OnClickListener {



    private var isSavePicture = false//图片上传状态


    private var isHadImage = false//用户是否添加图片

    private var item:MenuItem?=null

    private var addCommentInformation: Runnable = Runnable {
        val title = add_comment_title.text.toString().trim()
        val content = add_comment_content.text.toString().trim()
        /*
        * 从Imageview中获取Bitmap
        */
        add_comment_image.isDrawingCacheEnabled = true
        val bitmap = add_comment_image.drawingCache
        val baos = ByteArrayOutputStream()
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val imgbyte = baos.toByteArray()
        val imageCode = Base64.encode(imgbyte)
        val result = HandleFind.Add_User_Comment_Information(LoginActivity.userID, title, content, imageCode)
        if (result) {
            addCommentInformationHandler.sendEmptyMessage(1)
        } else {
            addCommentInformationHandler.sendEmptyMessage(0)
        }
    }

    private var addCommentInformationHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            item?.actionView=null
            if (msg.what == 1) {
                isSavePicture = false
                Toast.makeText(this@AddFindCommentActivity, "添加成功", Toast.LENGTH_SHORT).show()
                setResult(1, intent)
                finish()
            } else {
                isSavePicture = false
                item?.isEnabled=true
                Toast.makeText(this@AddFindCommentActivity, "出现错误，请稍后再试", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_find_comment)
        initView()
    }

    private fun initView() {
        val actionBack = supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)
        add_comment_image.setOnClickListener(this)
        /*
         * 标志位0，防止返回find时刷新页面
         */
        setResult(0, intent)
    }

    private fun submit() {
        // validate
        val title = add_comment_title.text.toString().trim()
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        val content = add_comment_content.text.toString().trim()
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isHadImage) {
            Toast.makeText(this, "请添加图片", Toast.LENGTH_SHORT).show()
            return
        }

        /*
         * 判断是否正在上传图片，防止重复上传
         */
        if (isSavePicture) {
            return
        }

        isSavePicture = true
        item?.actionView = ProgressBar(this)
        item?.isEnabled=false
        ThreadPool.execute(addCommentInformation)

    }

    override fun setImageToImageView(bitmap: Bitmap) {
        add_comment_image.setImageBitmap(bitmap)
        isHadImage = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save_comment -> submit()
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_comment_image -> chooseAlertDialog.show()
        }
    }


    //重写onCreateOptionsMenu方法，在顶部的bar中显示菜单
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_coment_menu, menu)
        item=menu.findItem(R.id.action_save_comment)
        return true
    }


}
