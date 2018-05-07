package com.example.sunkai.heritage.tools

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.util.Pair
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.example.sunkai.heritage.Activity.*
import com.example.sunkai.heritage.Adapter.*
import com.example.sunkai.heritage.Fragment.FindFragment
import com.example.sunkai.heritage.Interface.AbsHandleAdapter
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.ALL_FOLK_INFO_ACTIVITY
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.FROM
import com.example.sunkai.heritage.value.USER_ID
import com.makeramen.roundedimageview.RoundedImageView

/**
 * 对adapter的item点击事件进行了统一的封装
 */
object HandleAdapterClickUtils:AbsHandleAdapter() {
    override fun handleBottomNewsAdapterItemClick(context: Context, adapter: BottomFolkNewsRecyclerviewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                val intent = Intent(context, BottomNewsDetailActivity::class.java)
                intent.putExtra("data", data)
                intent.putExtra("title", context.getString(R.string.focus_heritage))
                if (context is AppCompatActivity&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val titleView = view.findViewById<TextView>(R.id.bottom_view_title)
                    val timeView=view.findViewById<TextView>(R.id.bottom_view_time)
                    val slide= Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, GlobalContext.instance.resources.configuration.layoutDirection))
                    slide.duration=500
                    context.window.exitTransition=slide
                    val paris= arrayListOf<android.util.Pair<View,String>>(android.util.Pair(titleView,context.getString(R.string.bottom_news_share_title)), Pair(timeView,context.getString(R.string.bottom_news_share_time))).toTypedArray()
                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, *paris).toBundle())
                } else {
                    context.startActivity(intent)
                }
            }
        })
    }

    override fun handleAllFolkNewsAdapterItemClick(context: Context, adapter: SeeMoreNewsRecyclerViewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                val intent = Intent(context, NewsDetailActivity::class.java)
                val titleView=view.findViewById<TextView>(R.id.see_more_news_item_title)
                intent.putExtra("category", data.category)
                intent.putExtra("data", data)
                if(context is AppCompatActivity && Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    val slide = Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, GlobalContext.instance.resources.configuration.layoutDirection))
                    slide.duration = 500
                    context.window?.exitTransition=slide
                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context,titleView,context.getString(R.string.news_detail_share_title)).toBundle())
                }else{
                    context.startActivity(intent)
                }
            }
        })
    }

    override fun handleFolkHeritageAdapterItemCLick(context: Context, adapter: FolkRecyclerViewAdapter) {
        adapter.setOnItemClickListen(object :OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val data=adapter.getItem(position)
                val intent= Intent(context, FolkInformationActivity::class.java)
                intent.putExtra(DATA,data)
                intent.putExtra(FROM, ALL_FOLK_INFO_ACTIVITY)
                context.startActivity(intent)
            }

        })
    }

    override fun handleFindUserCommentAdapterItemClick(context: Context, adapter: FindFragmentRecyclerViewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(context, UserCommentDetailActivity::class.java)
                intent.putExtra("data", adapter.getItem(position))
                //如果手机是Android 5.0以上的话，使用新的Activity切换动画
                if (context is AppCompatActivity&&Build.VERSION.SDK_INT >= 21) {
                    val getview: View = view.findViewById(R.id.fragment_find_litview_img) ?: return
                    intent.putExtra("option", UserCommentDetailActivity.ANIMATION_SHOW)
                    ActivityCompat.startActivityForResult(context,intent, FindFragment.FROM_USER_COMMENT_DETAIL, ActivityOptions.makeSceneTransitionAnimation(context, getview, context.getString(R.string.find_share_view)).toBundle())
                } else {
                    if(context is Activity) {
                        context.startActivityForResult(intent, FindFragment.FROM_USER_COMMENT_DETAIL)
                    }else{
                        context.startActivity(intent)
                    }
                }
            }
        })
    }

    override fun handlePersonAdapterItemClick(context: Context, adapter: SearchUserRecclerAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                val intent = Intent(context, OtherUsersActivity::class.java)
                intent.putExtra(USER_ID, data.id)
                if (context is AppCompatActivity&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val userImage = view.findViewById<RoundedImageView>(R.id.user_head_image)
                    val userName = view.findViewById<TextView>(R.id.user_name)
                    val pairs = TransitionHelper.createSafeTransitionParticipants(context, false, android.support.v4.util.Pair(userName, context.getString(R.string.share_user_name)), android.support.v4.util.Pair(userImage, context.getString(R.string.share_user_image)))
                    val transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(context, *pairs)
                    context.startActivity(intent, transitionOptions.toBundle())
                } else {
                    context.startActivity(intent)
                }
            }
        })
    }
}