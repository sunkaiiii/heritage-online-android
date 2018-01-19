package com.example.sunkai.heritage.Fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.example.sunkai.heritage.Activity.FocusInformationActivity
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.Activity.PermissionsActivity
import com.example.sunkai.heritage.Activity.SettingActivity
import com.example.sunkai.heritage.Activity.UserOwnTieziActivity
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.tools.FileStorage
import com.example.sunkai.heritage.tools.PermissionsChecker
import com.example.sunkai.heritage.R
import com.makeramen.roundedimageview.RoundedImageView

import org.kobjects.base64.Base64

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * 此类是用于处理个人中心页面
 */


class PersonFragment : BaseLazyLoadFragment(), View.OnClickListener {
    private lateinit var myOwnTiezi: LinearLayout
    private lateinit var settingLayout: LinearLayout
    private lateinit var userName: TextView
    private lateinit var follow: TextView
    private lateinit var followNumber: TextView
    private lateinit var fans: TextView
    private lateinit var fansNumber: TextView
    private lateinit var userImage: RoundedImageView

    internal lateinit var builder: AlertDialog.Builder
    internal lateinit var ad: AlertDialog

    internal lateinit var outputUri: Uri//剪裁后的图片的uri
    private var mPermissionsChecker: PermissionsChecker? = null // 权限检测器
    private var imageUri: Uri? = null//原图保存地址
    private var isClickCamera: Boolean = false
    private var imagePath: String? = null


    internal var focusAndFansCountChange: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, recieveIntent: Intent) {
            var intent = recieveIntent
            if ("change" == intent.getStringExtra("message")) {
                Thread(getFollowCount).start()
            }
            if ("sigh_out" == intent.getStringExtra("message")) {
                intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    internal var getFollowCount: Runnable = Runnable {
        val count = HandlePerson.Get_Follow_Number(LoginActivity.userID)
        getFollowCountHandler.sendEmptyMessage(count)
    }
    internal var getFollowCountHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            followNumber.text = msg.what.toString()
            Thread(getFansCount).start()
        }
    }
    internal var getFansCount: Runnable = Runnable {
        val count = HandlePerson.Get_Fans_Number(LoginActivity.userID)
        getFanseCountHandler.sendEmptyMessage(count)
    }
    internal var getFanseCountHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            fansNumber.text = msg.what.toString()
            Thread(getUserImage).start()
        }
    }
    internal var getUserImage: Runnable = Runnable {
        val result = HandlePerson.Get_User_Image(LoginActivity.userID)
        if (result == null) {
            getUserImageHandler.sendEmptyMessage(0)
        } else {
            val msg = Message()
            val data = Bundle()
            data.putString("image", result)
            msg.data = data
            msg.what = 1
            getUserImageHandler.sendMessage(msg)
        }
    }
    internal var getUserImageHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                val data = msg.data
                val imageCode = data.getString("image") ?: return
                val imageByte = Base64.decode(imageCode)
                val `in` = ByteArrayInputStream(imageByte)
                val bitmap = HandlePic.handlePic(`in`, 0)
                userImage.setImageBitmap(bitmap)
            }
        }
    }


    internal var updateUserImage: Runnable = Runnable {
        userImage.isDrawingCacheEnabled = true
        val bitmap = userImage.drawingCache
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        val image = baos.toByteArray()
        val imageCode = Base64.encode(image)
        val result = HandlePerson.Update_User_Image(LoginActivity.userID, imageCode)
        if (result) {
            updateUserImageHandler.sendEmptyMessage(1)
        } else {
            updateUserImageHandler.sendEmptyMessage(0)
        }
    }

    internal var updateUserImageHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            ad.dismiss()
            if (msg.what == 1) {
                Toast.makeText(activity, "更新头像成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "出现错误，请稍后再试", Toast.LENGTH_SHORT).show()
                userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
            }
        }
    }
    internal var refreshInfo: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            userName.text = LoginActivity.userName
            GetUserInfo()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_person, container, false)
        initview(view)
        if (LoginActivity.userName == null) {
            userName.text = "没有登录"
        } else {
            userName.text = LoginActivity.userName
        }
        mPermissionsChecker = PermissionsChecker(activity!!)
        var intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.focusAndFansCountChange")
        activity!!.registerReceiver(focusAndFansCountChange, intentFilter)
        intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.refreInfomation")
        activity?.registerReceiver(refreshInfo, intentFilter)
        return view
    }

    private fun initview(view: View){
        myOwnTiezi = view.findViewById(R.id.fragment_person_my_tiezi)
        settingLayout = view.findViewById(R.id.person_fragment_setting)
        userName = view.findViewById(R.id.sign_name_textview)
        follow = view.findViewById(R.id.person_follow)
        followNumber = view.findViewById(R.id.person_follow_number)
        fans = view.findViewById(R.id.person_fans)
        fansNumber = view.findViewById(R.id.person_fans_number)
        userImage = view.findViewById(R.id.sign_in_icon)
        follow.setOnClickListener(this)
        followNumber.setOnClickListener(this)
        fans.setOnClickListener(this)
        fansNumber.setOnClickListener(this)
        userImage.setOnClickListener(this)
        myOwnTiezi.setOnClickListener(this)
        settingLayout.setOnClickListener(this)
    }

    override fun startLoadInformation() {
        GetUserInfo()
    }

    /**
     * 开始获取用户的粉丝和关注的信息
     * 先获取关注信息
     */
    private fun GetUserInfo() {
        Thread(getFollowCount).start()
    }

    private fun toLogin() {
        Toast.makeText(activity, "没有登录", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.putExtra("isInto", 1)
        startActivityForResult(intent, 1)
    }

    override fun onClick(v: View) {
        val intent: Intent
        if (LoginActivity.userID == 0) {
            toLogin()
            return
        }
        when (v.id) {
            R.id.fragment_person_my_tiezi -> {
                intent = Intent(activity, UserOwnTieziActivity::class.java)
                startActivity(intent)
            }
            R.id.person_fragment_setting -> {
                if (LoginActivity.userID == 0) {
                    toLogin()
                    return
                }
                intent = Intent(activity, SettingActivity::class.java)
                userImage.isDrawingCacheEnabled = true
                intent.putExtra("userImage", userImage.drawingCache)
                startActivity(intent)
            }
            R.id.person_follow, R.id.person_follow_number -> {
                intent = Intent(activity, FocusInformationActivity::class.java)
                intent.putExtra("information", "focus")
                startActivity(intent)
            }
            R.id.person_fans, R.id.person_fans_number -> {
                intent = Intent(activity, FocusInformationActivity::class.java)
                intent.putExtra("information", "fans")
                startActivity(intent)
            }
            R.id.sign_in_icon -> {
                builder = AlertDialog.Builder(activity!!).setTitle("请选择")
                val choice = ListView(activity)
                val arrayAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1)
                arrayAdapter.add("拍照")
                arrayAdapter.add("从图库中选择")
                choice.adapter = arrayAdapter
                builder.setView(choice)
                ad = builder.show()

                /**
                 * 在获取图片或者拍照的时候，判断系统版本，如果是安卓6.0以上，要动态申请权限
                 */
                choice.setOnItemClickListener { _, _, position, _ ->
                    if (position == 0) {
                        ad.dismiss()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (mPermissionsChecker!!.lacksPermissions(*PERMISSIONS)) {
                                startPermissionsActivity()
                            } else {
                                openCamera()
                            }
                        } else {
                            openCamera()
                        }
                        isClickCamera = true
                    } else if (position == 1) {
                        ad.dismiss()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (mPermissionsChecker!!.lacksPermissions(*PERMISSIONS)) {
                                startPermissionsActivity()
                            } else {
                                selectFromAlbum()
                            }
                        } else {
                            selectFromAlbum()
                        }
                        isClickCamera = false
                    }
                }
            }
            else -> {
            }
        }
    }

    /**
     * 打开系统相机
     */
    private fun openCamera() {
        val file = FileStorage().createIconFile()
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(activity!!, "com.example.sunkai.heritage.fileprovider", file!!)//通过FileProvider创建一个content类型的Uri
        } else {
            Uri.fromFile(file)
        }
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE)
    }

    /**
     * 从相册选择
     */
    private fun selectFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }


    private fun startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(activity!!, REQUEST_PERMISSION,
                *PERMISSIONS)
    }

    @TargetApi(19)
    private fun handleImageOnKitKat(data: Intent?) {
        imagePath = null
        if (data == null) {
            return
        }
        imageUri = data.data
        if (DocumentsContract.isDocumentUri(activity, imageUri)) {
            //如果是document类型的uri,则通过document id处理
            val docId = DocumentsContract.getDocumentId(imageUri)
            if ("com.android.providers.media.documents" == imageUri!!.authority) {
                val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]//解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.downloads.documents" == imageUri!!.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId)!!)
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(imageUri!!.scheme, ignoreCase = true)) {
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(imageUri, null)
        } else if ("file".equals(imageUri!!.scheme, ignoreCase = true)) {
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = imageUri!!.path
        }

        cropPhoto()
        //        setImage();
    }

    private fun handleImageBeforeKitKat(intent: Intent?) {
        imageUri = intent!!.data
        imagePath = getImagePath(imageUri, null)
        cropPhoto()
        //        setImage();
    }

    private fun getImagePath(uri: Uri?, selection: String?): String? {
        var path: String? = null
        //通过Uri和selection老获取真实的图片路径
        val cursor = activity!!.contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun setImage() {
        var bitmap: Bitmap
        try {
            /**
             * 在经过剪裁之后，outputUri会被赋值
             * 将uri指向的内容复制给bitmap
             */
            bitmap = BitmapFactory.decodeStream(activity!!.contentResolver.openInputStream(outputUri))
            userImage.setImageDrawable(null)
            System.gc()
            bitmap = HandlePic.compressBitmapToFile(bitmap, 192, 192)
            userImage.setImageBitmap(bitmap)
            //            System.out.println("孙楷最帅"+bitmap.getWidth()+","+bitmap.getHeight());
            builder = AlertDialog.Builder(activity!!).setTitle("上传中").setView(R.layout.update_image_builder)
            ad = builder.create()
            ad.show()
            Thread(updateUserImage).start()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 裁剪
     */

    private fun cropPhoto() {
        val file = FileStorage().createCropFile()
        outputUri = Uri.fromFile(file)//缩略图保存地址
        val intent = Intent("com.android.camera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.setDataAndType(imageUri, "image/*")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("scale", true)
        intent.putExtra("return-data", false)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)//剪裁输出的uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", false)//人脸检测
        startActivityForResult(intent, REQUEST_PICTURE_CUT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            IS_INTO_LOGIN -> {
                userName.text = LoginActivity.userName
                GetUserInfo()
            }
            REQUEST_PICK_IMAGE//从相册选择
            -> if (Build.VERSION.SDK_INT >= 19) {
                handleImageOnKitKat(data)
            } else {
                handleImageBeforeKitKat(data)
            }
            REQUEST_CAPTURE//拍照
            -> if (resultCode == RESULT_OK) {
                cropPhoto()
            }
            REQUEST_PICTURE_CUT//裁剪完成
            -> setImage()
            REQUEST_PERMISSION//权限请求
            -> if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                activity!!.finish()
            } else {
                if (isClickCamera) {
                    openCamera()
                } else {
                    selectFromAlbum()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(focusAndFansCountChange)
        activity?.unregisterReceiver(refreshInfo)
    }

    companion object {


        private val IS_INTO_LOGIN = 1
        private val REQUEST_PICK_IMAGE = 11 //相册选取
        private val REQUEST_CAPTURE = 12  //拍照
        private val REQUEST_PICTURE_CUT = 13  //剪裁图片
        private val REQUEST_PERMISSION = 14  //权限请求
        internal val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    }
}// Required empty public constructor
