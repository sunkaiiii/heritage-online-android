package com.example.sunkai.heritage.Activity

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sunkai.heritage.Adapter.OtherPersonActivityRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Data.OtherPersonData
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.FANS
import com.example.sunkai.heritage.value.FOLLOW
import com.example.sunkai.heritage.value.NO_USERID
import com.github.chrisbanes.photoview.PhotoView
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_other_users.*
import org.kobjects.base64.Base64
import top.zibin.luban.Luban
import java.io.ByteArrayInputStream

class OtherUsersActivity : AppCompatActivity() ,View.OnClickListener{
    internal lateinit var userNameTextView:TextView
    internal lateinit var userImageView:RoundedImageView
    internal lateinit var focusNumberTextView:TextView
    internal lateinit var fansNumberTextView:TextView
    internal lateinit var focusText:TextView
    internal lateinit var fansText:TextView
    internal lateinit var userinfosRecyclerView:RecyclerView
    internal lateinit var llBackground:LinearLayout
    internal lateinit var pvImage:PhotoView
    var userID:Int= NO_USERID

    internal val inAnimation=AlphaAnimation(0f,1f)
    internal val outAnimation=AlphaAnimation(1f,0f)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users)
        initview()
        userID=intent.getIntExtra("userID",NO_USERID)
        if (userID!=NO_USERID){
            getUserAllInfo(userID)
            setAdapter(userID)
        }
    }

    internal fun getUserAllInfo(userID:Int){
        Thread{
            val data=HandlePerson.Get_User_All_Info(userID)
            data?.let{
                runOnUiThread({
                    setViews(data)
                })
            }
            val bitmap=findImageInSql(userID)
            bitmap?.let{
                runOnUiThread{
                    setImageView(bitmap)
                }
                return@Thread
            }
            val image=getUserImage(userID)
            image?.let {
                runOnUiThread {
                    setImageView(image)
                }
            }
        }.start()
    }

    internal fun findImageInSql(userID: Int):Bitmap?{
        val db = MySqliteHandler.GetReadableDatabase()
        val cursor: Cursor
        val table = "person_image"
        val selection = "imageID=?"
        val selectionArgs = arrayOf(userID.toString())
        cursor = db.query(table, null, selection, selectionArgs, null, null, null)
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            val imageIndex = cursor.getColumnIndex("image")
            val image = cursor.getBlob(imageIndex)
            cursor.close()
            return HandlePic.handlePic(ByteArrayInputStream(image), 0)
        }
        return null
    }
    internal fun addImageToSql(userID: Int,userImage:ByteArray){
        val contentValues = ContentValues()
        contentValues.put("imageID", userID)
        contentValues.put("image", userImage)
        val db = MySqliteHandler.GetWritableDatabase()
        val table = "person_image"
        db.insert(table, null, contentValues)
    }
    internal fun getUserImage(userID:Int):Bitmap?{
        val userImage=HandlePerson.Get_User_Image(userID)
        userImage?.let {
            addImageToSql(userID,Base64.decode(userImage))
            return HandlePic.handlePic(ByteArrayInputStream(Base64.decode(userImage)),0)
        }
        return null
    }

    internal fun setViews(data:OtherPersonData){
        userNameTextView.text=data.userName
        focusNumberTextView.text=data.followNum.toString()
        fansNumberTextView.text=data.fansNumber.toString()
    }

    internal fun setImageView(image:Bitmap){
        userImageView.setImageBitmap(image)
    }

    internal fun setAdapter(userID: Int){
        val adapter= OtherPersonActivityRecyclerViewAdapter(userID)
        val layoutManager = GridLayoutManager(this,4)
        rv_activity_other_users.layoutManager = layoutManager
        rv_activity_other_users.adapter=adapter

        adapter.setOnItemClickListen(object:OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val imageview:ImageView=view.findViewById(R.id.iv_other_person_view)
                val bitmap=getBitmap(imageview)
                bitmap?.let{
                    openImageView(bitmap)
                }
            }
        })
    }

    internal fun getBitmap(imageView: ImageView):Bitmap?{
        imageView.isDrawingCacheEnabled=true
        val drawable = imageView.drawable
        if(drawable is BitmapDrawable){
            return drawable.bitmap
        }
        return null
    }

    internal fun initview(){
        userNameTextView=findViewById(R.id.sign_name_textview)
        userImageView=findViewById(R.id.sign_in_icon)
        focusNumberTextView=findViewById(R.id.person_follow_number)
        fansNumberTextView=findViewById(R.id.person_fans_number)
        focusText=findViewById(R.id.person_follow)
        fansText=findViewById(R.id.person_fans)
        userinfosRecyclerView=findViewById(R.id.rv_activity_other_users)
        llBackground=findViewById(R.id.ll_activity_other_users_background)
        pvImage=findViewById(R.id.pv_activity_other_users_image)

        inAnimation.duration=300
        outAnimation.duration=300

        userImageView.setOnClickListener(this)
        focusNumberTextView.setOnClickListener(this)
        fansNumberTextView.setOnClickListener(this)
        focusText.setOnClickListener(this)
        fansText.setOnClickListener(this)
        llBackground.setOnClickListener(this)
        pvImage.setOnClickListener(this)
    }

    internal fun startActivity(what:Int){
        val intent=Intent(this,FocusInformationActivity::class.java)
        intent.putExtra("information",what.toString())
        intent.putExtra("userID",userID)
        startActivity(intent)
    }
    internal fun openImageView(bitmap:Bitmap){
        llBackground.startAnimation(inAnimation)
        pvImage.startAnimation(inAnimation)
        llBackground.visibility=View.VISIBLE
        pvImage.visibility=View.VISIBLE
        pvImage.setImageBitmap(bitmap)
    }
    internal fun closeImageView(){
        llBackground.startAnimation(outAnimation)
        pvImage.startAnimation(outAnimation)
        llBackground.visibility=View.GONE
        pvImage.visibility=View.GONE

    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.person_follow_number,R.id.person_follow->startActivity(FOLLOW)
            R.id.person_fans_number,R.id.person_fans->startActivity(FANS)
            R.id.ll_activity_other_users_background->{
                closeImageView()
            }
            R.id.pv_activity_other_users_image->{
                if(pvImage.scale==1.0f){
                    closeImageView()
                }else{
                    pvImage.setScale(1.0f,true)
                }
            }
        }
    }

    override fun onBackPressed() {
        if(llBackground.visibility==View.VISIBLE){
            closeImageView()
        }else {
            super.onBackPressed()
        }
    }
}
