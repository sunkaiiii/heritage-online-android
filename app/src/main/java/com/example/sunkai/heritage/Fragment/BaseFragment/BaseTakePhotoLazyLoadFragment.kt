package com.example.sunkai.heritage.Fragment.BaseFragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.tools.HandleImage
import com.example.sunkai.heritage.value.CHOOSE_PHOTO

/**
 * 用于Fragment的基类
 * Created by sunkai on 2018/2/26.
 */
abstract class BaseTakePhotoLazyLoadFragment : BaseLazyLoadFragment(), OnPageLoaded {

    private lateinit var waitForCompressDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val waitLinearLayout = LinearLayout(activity)
        waitLinearLayout.orientation = LinearLayout.HORIZONTAL
        waitLinearLayout.addView(ProgressBar(activity))
        waitForCompressDialog = AlertDialog.Builder(activity).setView(waitLinearLayout).create()
    }

    override fun onStart() {
        super.onStart()
        setNeedOpenChoseViewClickListner()
    }

    abstract fun getNeedOpenChooseImageView(): Array<View>

    private fun setNeedOpenChoseViewClickListner() {
        val views = getNeedOpenChooseImageView()
        views.forEach {
            it.setOnClickListener {
                choosePhoto()
            }
        }
    }

    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    private fun choosePhoto() {
        openAlbum()
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
        AsyncTask.SERIAL_EXECUTOR.execute {
            val bitmap = HandleImage(data)
            if (bitmap == null) {
                onPostLoad()
                return@execute
            }
            activity?.runOnUiThread {
                setImageToImageView(bitmap)
                onPostLoad()
            }
        }
    }

    override fun onPreLoad() {
        waitForCompressDialog.show()
    }

    override fun onPostLoad() {
        if (waitForCompressDialog.isShowing)
            waitForCompressDialog.dismiss()
    }


//    private fun cropPhoto() {
//        val file = FileStorage().createCropFile()
//        outputUri = Uri.fromFile(file)//缩略图保存地址
//        val intent = Intent("com.android.camera.action.CROP")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        intent.setDataAndType(imageUri, "image/*")
//        intent.putExtra("crop", "true")
//        intent.putExtra("aspectX", 1)
//        intent.putExtra("aspectY", 1)
//        intent.putExtra("scale", true)
//        intent.putExtra("return-data", false)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)//剪裁输出的uri
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
//        intent.putExtra("noFaceDetection", false)//人脸检测
//        startActivityForResult(intent, PersonFragment.REQUEST_PICTURE_CUT)
//    }


}