package com.example.sunkai.heritage.Activity

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.OtherPersonData
import com.example.sunkai.heritage.R
import com.makeramen.roundedimageview.RoundedImageView
import org.kobjects.base64.Base64
import java.io.ByteArrayInputStream

class OtherUsersActivity : AppCompatActivity() ,View.OnClickListener{
    internal lateinit var userNameTextView:TextView
    internal lateinit var userImageView:RoundedImageView
    internal lateinit var focusNumberTextView:TextView
    internal lateinit var fansNumberTextView:TextView
    internal lateinit var focusText:TextView
    internal lateinit var fansText:TextView
    internal lateinit var userinfosRecyclerView:RecyclerView

    private val NO_USERID=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users)
        initview()

        val userID=intent.getIntExtra("userID",NO_USERID)
        if (userID!=NO_USERID){
            getUserAllInfo(userID)
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
            val image=getUserImage(userID)
            image?.let {
                runOnUiThread {
                    setImageView(image)
                }
            }
        }.start()
    }

    internal fun getUserImage(userID:Int):Bitmap?{
        val userImage=HandlePerson.Get_User_Image(userID)
        userImage?.let {
            return HandlePic.handlePic(this,ByteArrayInputStream(Base64.decode(userImage)),0)
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

    internal fun initview(){
        userNameTextView=findViewById(R.id.sign_name_textview)
        userImageView=findViewById(R.id.sign_in_icon)
        focusNumberTextView=findViewById(R.id.person_follow_number)
        fansNumberTextView=findViewById(R.id.person_fans_number)
        focusText=findViewById(R.id.person_follow)
        fansText=findViewById(R.id.person_fans)
        userinfosRecyclerView=findViewById(R.id.rv_activity_other_users)

        userImageView.setOnClickListener(this)
        focusNumberTextView.setOnClickListener(this)
        fansNumberTextView.setOnClickListener(this)
        focusText.setOnClickListener(this)
        fansText.setOnClickListener(this)
    }


    override fun onClick(v: View?) {

    }
}
