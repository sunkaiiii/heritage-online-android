package com.example.sunkai.heritage.Activity

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sunkai.heritage.Adapter.OtherPersonActivityRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Data.OtherPersonData
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.FindInSql
import com.example.sunkai.heritage.value.FANS
import com.example.sunkai.heritage.value.FOLLOW
import com.example.sunkai.heritage.value.NO_USERID
import com.github.chrisbanes.photoview.PhotoView
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_other_users.*
import org.kobjects.base64.Base64
import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference
import java.util.*

class OtherUsersActivity : AppCompatActivity() ,View.OnClickListener{
    internal lateinit var userNameTextView:TextView
    internal lateinit var userImageView:RoundedImageView
    internal lateinit var focusNumberTextView:TextView
    internal lateinit var fansNumberTextView:TextView
    internal lateinit var focusText:TextView
    internal lateinit var fansText:TextView
    internal lateinit var userinfosRecyclerView:RecyclerView
    internal lateinit var llBackground:LinearLayout
    internal lateinit var vpViewPager:ViewPager
    internal lateinit var viewPagerAdapter:ViewPagerAdapter

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
                openImageView(position,adapter)
            }
        })
    }

    internal class ViewPagerAdapter(val datas:List<Int>,activity:OtherUsersActivity):PagerAdapter(){
        val weakRefrence=WeakReference(activity)
        val photoViewMap:WeakHashMap<Int,PhotoView>
        init {
            photoViewMap= WeakHashMap()
        }
        override fun getCount(): Int {
            return datas.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view==`object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photoView=PhotoView(GlobalContext.instance)
            photoView.scaleType=ImageView.ScaleType.FIT_CENTER
            container.addView(photoView)
            photoView.setImageDrawable(ContextCompat.getDrawable(GlobalContext.instance,R.drawable.backgound_grey))
            photoView.setOnClickListener {
                if(photoView.scale==1.0f){
                    weakRefrence.get()?.closeImageView()
                }else{
                    photoView.setScale(1.0f,true)
                }
            }
            photoViewMap.put(position,photoView)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
        }
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
        vpViewPager=findViewById(R.id.vp_activity_other_users)

        inAnimation.duration=300
        outAnimation.duration=300

        userImageView.setOnClickListener(this)
        focusNumberTextView.setOnClickListener(this)
        fansNumberTextView.setOnClickListener(this)
        focusText.setOnClickListener(this)
        fansText.setOnClickListener(this)
        llBackground.setOnClickListener(this)
        vpViewPager.setOnClickListener(this)

    }

    internal fun startActivity(what:Int){
        val intent=Intent(this,FocusInformationActivity::class.java)
        intent.putExtra("information",what.toString())
        intent.putExtra("userID",userID)
        startActivity(intent)
    }
    internal fun openImageView(position: Int,adapter: OtherPersonActivityRecyclerViewAdapter){
        llBackground.startAnimation(inAnimation)
        vpViewPager.startAnimation(inAnimation)
        llBackground.visibility=View.VISIBLE
        vpViewPager.visibility=View.VISIBLE
        viewPagerAdapter= ViewPagerAdapter(adapter.datas,this)
        vpViewPager.adapter=viewPagerAdapter
        vpViewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageSelected(position: Int) {
                getImage(position,viewPagerAdapter)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {            }

            override fun onPageScrollStateChanged(state: Int) {           }

        })
        vpViewPager.currentItem=position
        //如果点击的是第一个图片，则要指定载入图片，其他情况因为会触发OnPageChangeListener,则无需管理
        if(position==0){
            getImage(position,viewPagerAdapter)
        }
    }

    internal fun closeImageView(){
        llBackground.startAnimation(outAnimation)
        llBackground.visibility=View.GONE
        vpViewPager.startAnimation(outAnimation)
        vpViewPager.visibility=View.GONE
    }

    internal fun getImage(position: Int,adapter: ViewPagerAdapter){
        Thread{
            var bitmap=FindInSql.searchFindCommentImageFromSQL(adapter.datas[position])
            if(bitmap==null){
                val imageByte=HandleFind.Get_User_Comment_Image(adapter.datas[position])
                imageByte?.let{
                    FindInSql.addFindCommentImageToSQL(adapter.datas[position],imageByte)
                    bitmap=HandlePic.handlePic(ByteArrayInputStream(imageByte),0)
                }
            }
            bitmap?.let{
                runOnUiThread({
                    if(position==vpViewPager.currentItem){
                        val photoview=viewPagerAdapter.photoViewMap[position]
                        photoview?.let{
                            photoview.setImageBitmap(bitmap)
                        }
                    }
                })
            }
        }.start()
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.person_follow_number,R.id.person_follow->startActivity(FOLLOW)
            R.id.person_fans_number,R.id.person_fans->startActivity(FANS)
            R.id.ll_activity_other_users_background,R.id.vp_activity_other_users->{
                closeImageView()
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
