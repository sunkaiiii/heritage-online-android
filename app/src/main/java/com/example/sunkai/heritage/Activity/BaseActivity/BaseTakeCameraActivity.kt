package com.example.sunkai.heritage.Activity.BaseActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.tools.HandleImage
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.CHOOSE_PHOTO

/**
 * 当某个页面需要打开或者拍摄图片时候所用的基类
 * Created by sunkai on 2018/1/4.
 */
abstract class BaseTakeCameraActivity : BaseAutoLoginActivity(), OnPageLoaded {

    private lateinit var waitForCompressDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val waitLinearLayout = LinearLayout(this)
        waitLinearLayout.orientation = LinearLayout.HORIZONTAL
        waitLinearLayout.addView(ProgressBar(this))
        waitForCompressDialog = AlertDialog.Builder(this).setView(waitLinearLayout).create()
    }

    override fun onStart() {
        super.onStart()
        setNeedOpenChoseViewClickListner()
    }


    abstract fun getNeedOpenChooseImageView(): Array<View>


    private fun setNeedOpenChoseViewClickListner() {
        val views = getNeedOpenChooseImageView()
        views.forEach { view ->
            view.setOnClickListener {
                choosePhoto()
            }
        }
    }

    private fun choosePhoto() {
        openAlbum()
    }

    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO)
    }


    protected abstract fun setImageToImageView(bitmap: Bitmap)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data ?: return
        when (requestCode) {
            CHOOSE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                handleUri(data.data)
            }
        }
    }

    private fun handleUri(data: Uri?) {
        data ?: return
        onPreLoad()
        ThreadPool.execute {
            val bitmap = HandleImage(data)
            if (bitmap == null) {
                onPostLoad()
                return@execute
            }
            runOnUiThread {
                Log.d("test", bitmap.width.toString() + " " + bitmap.height)
                onHandleUriDataSuccess(data)
                setImageToImageView(bitmap)
                onPostLoad()
            }
        }
    }

    open fun onHandleUriDataSuccess(data: Uri) {}

    override fun onPreLoad() {
        waitForCompressDialog.show()
    }

    override fun onPostLoad() {
        if (waitForCompressDialog.isShowing)
            waitForCompressDialog.dismiss()
    }

}