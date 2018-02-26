package com.example.sunkai.heritage.Fragment

import android.Manifest
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
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.CHOOSE_PHOTO
import com.example.sunkai.heritage.value.TAKE_PHOTO
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * 用于Fragment的基类
 * Created by sunkai on 2018/2/26.
 */
abstract class BaseTakePhotoLazyLoadFragment:BaseLazyLoadFragment() {
    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    private lateinit var imageUri: Uri
    protected lateinit var chooseAlertDialog: AlertDialog
    private lateinit var waitForCompressDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val choiceLitView = ListView(activity)
        val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_expandable_list_item_1)
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

        chooseAlertDialog= AlertDialog.Builder(activity).setView(choiceLitView).create()
        val waitLinearLayout= LinearLayout(activity)
        waitLinearLayout.orientation= LinearLayout.HORIZONTAL
        waitLinearLayout.addView(ProgressBar(activity))
        waitForCompressDialog= AlertDialog.Builder(activity).setView(waitLinearLayout).create()
    }

    private fun takePhoto(){
        val activity= activity ?: return
        val outputImage = File(activity.externalCacheDir, "output_image.jpg")
        try {
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        imageUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(activity, "com.example.sunkai.heritage.fileprovider", outputImage)
        } else {
            Uri.fromFile(outputImage)
        }
        //启动相机程序
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,Array(1){ Manifest.permission.CAMERA}, TAKE_PHOTO)
        }else {
            startActivityForResult(intent, TAKE_PHOTO)
        }
    }

    private fun choosePhoto(){
        val activity=activity?:return
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), CHOOSE_PHOTO)
        } else {
            openAlbum()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            toast("您拒绝了权限")
        }
        when (requestCode) {
            CHOOSE_PHOTO -> {
                openAlbum()
            }
            TAKE_PHOTO ->{
                takePhoto()
            }
        }
    }

    private fun handleImage(file:String){
        compressImage(file)
    }
    protected abstract fun setImageToImageView(bitmap: Bitmap)
    private fun compressImage(file:String){
        Luban.with(activity)
                .load(file)
                .setCompressListener(object : OnCompressListener {
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
        val activity=activity?:return
        when (requestCode) {
            TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                try {
                    //将拍摄的照片显示出来
                    val bitmap = BitmapFactory.decodeStream(activity.contentResolver.openInputStream(imageUri))
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
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent) {
        val activity=activity?:return
        var imagePath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(activity, uri)) {
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
//        cropPhoto()
        displayImage(imagePath)
    }

    private fun handleImageBeforeKitkat(data: Intent) {
        val uri = data.data
        val imagePath = getImagePath(uri, null)
//        cropPhoto()
        displayImage(imagePath)
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

    private fun getImagePath(uri: Uri?, selection: String?): String? {
        val activity=activity?:return  null
        var path: String? = null
        //通过Uri和Selection来获取真实的图片路径
        val cursor = activity.contentResolver.query(uri!!, null, selection, null, null)
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
            toast( "获取图片失败")
        }
    }
}