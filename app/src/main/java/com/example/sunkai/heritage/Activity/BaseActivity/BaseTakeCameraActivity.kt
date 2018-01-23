package com.example.sunkai.heritage.Activity.BaseActivity

import android.Manifest
import android.R.layout.simple_expandable_list_item_1
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.value.CHOOSE_PHOTO
import com.example.sunkai.heritage.value.TAKE_PHOTO
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * 当某个页面需要打开或者拍摄图片时候所用的基类
 * Created by sunkai on 2018/1/4.
 */
abstract class BaseTakeCameraActivity :AppCompatActivity(){

    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    private lateinit var imageUri:Uri
    protected lateinit var chooseAlertDialog:AlertDialog
    private lateinit var waitForCompressDialog:AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val choiceLitView = ListView(this)
        val arrayAdapter = ArrayAdapter<String>(this, simple_expandable_list_item_1)
        arrayAdapter.add("拍照")
        arrayAdapter.add("从图库中选择")
        choiceLitView.adapter = arrayAdapter
        choiceLitView.setOnItemClickListener { _, _, position, _ ->
            when(position){
                0->{
                    chooseAlertDialog.dismiss()
                    takePhoto()
                }
                1->{
                    chooseAlertDialog.dismiss()
                    choosePhoto()
                }
            }
        }

        chooseAlertDialog=AlertDialog.Builder(this).setView(choiceLitView).create()

        val waitLinearLayout=LinearLayout(this)
        waitLinearLayout.orientation=LinearLayout.HORIZONTAL
        waitLinearLayout.addView(ProgressBar(this))
        waitForCompressDialog=AlertDialog.Builder(this).setView(waitLinearLayout).create()
    }

    protected fun takePhoto(){
        val outputImage = File(externalCacheDir, "output_image.jpg")
        try {
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        imageUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(this@BaseTakeCameraActivity, "com.example.sunkai.heritage.fileprovider", outputImage)
        } else {
            Uri.fromFile(outputImage)
        }
        //启动相机程序
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,Array(1){Manifest.permission.CAMERA}, TAKE_PHOTO)
        }else {
            startActivityForResult(intent, TAKE_PHOTO)
        }
    }

    protected fun choosePhoto(){
        if (ContextCompat.checkSelfPermission(this@BaseTakeCameraActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@BaseTakeCameraActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), CHOOSE_PHOTO)
        } else {
            openAlbum()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show()
        }
        when (requestCode) {
            CHOOSE_PHOTO -> {
                openAlbum()
            }
            TAKE_PHOTO->{
                takePhoto()
            }
        }
    }

    protected fun handleImage(file:String){
        compressImage(file)
    }
    protected abstract fun setImageToImageView(bitmap: Bitmap)
    protected fun compressImage(file:String){
        Luban.with(this)
                .load(file)
                .setCompressListener(object :OnCompressListener{
                    override fun onStart() {
                        if(!waitForCompressDialog.isShowing)
                            waitForCompressDialog.show()
                    }

                    override fun onSuccess(file: File?) {
                        if(waitForCompressDialog.isShowing){
                            waitForCompressDialog.dismiss()
                        }
                        val bitmap = BitmapFactory.decodeFile(file?.path)
                        setImageToImageView(bitmap)
                    }

                    override fun onError(e: Throwable?) {
                        MakeToast.MakeText("出现问题")
                    }
                }).launch()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                try {
                    //将拍摄的照片显示出来
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    setImageToImageView(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
            CHOOSE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                //判断手机版本号
                if (Build.VERSION.SDK_INT >= 19)
                    handleImageOnKitkat(data!!) //4.4及以上系统用这个方法
                else {
                    handleImageBeforeKitkat(data!!)
                }
            }
            else -> {
            }
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent) {
        var imagePath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri则通过document id处理
            val docID = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] //解析出数字格式的ID
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docID)!!)
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            //如果content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            //如果是file类型的uri，则直接获取文件路径
            imagePath = uri.path
        }
        displayImage(imagePath)
    }

    private fun handleImageBeforeKitkat(data: Intent) {
        val uri = data.data
        val imagePath = getImagePath(uri, null)
        displayImage(imagePath)
    }

    private fun getImagePath(uri: Uri?, selection: String?): String? {
        var path: String? = null
        //通过Uri和Selection来获取真实的图片路径
        val cursor = contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            handleImage(imagePath)
        } else {
            Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }
}