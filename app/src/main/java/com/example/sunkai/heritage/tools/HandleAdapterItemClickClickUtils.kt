package com.example.sunkai.heritage.tools

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.example.sunkai.heritage.Activity.*
import com.example.sunkai.heritage.Adapter.*
import com.example.sunkai.heritage.Fragment.FindFragment
import com.example.sunkai.heritage.Interface.IHandleAdapterItemClick
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.ACTIVITY_FRAGMENT
import com.example.sunkai.heritage.value.ALL_FOLK_INFO_ACTIVITY
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.FROM

/**
 * 对adapter的item点击事件进行了统一的封装
 */

object HandleAdapterItemClickClickUtils : IHandleAdapterItemClick {
    override fun handleMyOwnCommentRecyclerViewItemClick(context: Context, adapter: MyOwnCommentRecyclerViewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(context, UserCommentDetailActivity::class.java)
                intent.putExtra(DATA, adapter.getItem(position))
                if (context is Activity) {
                    val imageView = view.findViewById<ImageView>(R.id.mycomment_item_image)
                    val title = view.findViewById<TextView>(R.id.mycomment_item_title)
                    val content = view.findViewById<TextView>(R.id.mycomment_item_content)
                    val pairs = arrayOf(CreateTransitionPair(imageView, R.string.find_share_view),
                            CreateTransitionPair(title, R.string.find_share_title),
                            CreateTransitionPair(content, R.string.find_share_content))
                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, *pairs).toBundle())
                } else {
                    context.startActivity(intent)
                }
            }
        })
    }

    override fun handleMyLikeCommentRecyclerviewItemClick(context: Context, adapter: MyLikeCommentRecyclerAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                val imageview = view.findViewById<ImageView>(R.id.coment_image)
                val infoBackGround = view.findViewById<LinearLayout>(R.id.info_background)
                val intent = Intent(context, UserCommentDetailActivity::class.java)
                intent.putExtra(DATA, data)
                if (context is Activity) {
                    val pairs = arrayOf(CreateTransitionPair(imageview, R.string.find_share_view),
                            CreateTransitionPair(infoBackGround, R.string.find_share_background))
                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, *pairs).toBundle())
                } else {
                    context.startActivity(intent)
                }
            }
        })
    }

    override fun handleFocusListviewItemClick(context: Context, adapter: FocusListviewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(context, OtherUsersActivity::class.java)
                val data = adapter.getItem(position)
                intent.putExtra(DATA, data)
                if (context is Activity) {
                    val userName = view.findViewById<TextView>(R.id.user_name)
                    val userImage = view.findViewById<ImageView>(R.id.user_head_image)
                    val pairs = arrayOf(CreateTransitionPair(userName, R.string.share_user_name)
                            , CreateTransitionPair(userImage, R.string.share_user_image))
                    val transitionOptions = ActivityOptions.makeSceneTransitionAnimation(context, *pairs)
                    context.startActivity(intent, transitionOptions.toBundle())
                } else {
                    context.startActivity(intent)
                }
            }
        })
    }

    override fun handleActivityRecyclerViewItemClick(context: Context, adapter: ActivityRecyclerViewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val activitydata = adapter.getItem(position)
                val intent = Intent(context, FolkInformationActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("activity", activitydata)
                intent.putExtra("image", activitydata.img)
                intent.putExtra("from", ACTIVITY_FRAGMENT)
                intent.putExtras(bundle)
                if (context is Activity) {
                    val image = view.findViewById<ImageView>(R.id.activity_layout_img)
                    val title = view.findViewById<TextView>(R.id.activity_layout_title)
                    val time = view.findViewById<TextView>(R.id.activity_layout_time)
                    val number = view.findViewById<TextView>(R.id.activity_layout_number)
                    val location = view.findViewById<TextView>(R.id.activity_layout_location)
                    val content = view.findViewById<TextView>(R.id.activity_layout_content)
                    val pairs = arrayOf(
                            CreateTransitionPair(image, R.string.share_user_image),
                            CreateTransitionPair(title, R.string.share_folk_title),
                            CreateTransitionPair(time, R.string.share_folk_time),
                            CreateTransitionPair(number, R.string.share_folk_number),
                            CreateTransitionPair(location, R.string.share_folk_location),
                            CreateTransitionPair(content, R.string.share_folk_content))
                    val transitionOptions = ActivityOptions.makeSceneTransitionAnimation(context, *pairs)
                    context.startActivity(intent, transitionOptions.toBundle())
                } else {
                    context.startActivity(intent)
                }
            }

        })
    }

    override fun handleBottomNewsAdapterItemClick(context: Context, adapter: BottomFolkNewsRecyclerviewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                val intent = Intent(context, BottomNewsDetailActivity::class.java)
                intent.putExtra("data", data)
                intent.putExtra("title", context.getString(R.string.focus_heritage))
                if (context is AppCompatActivity) {
                    val titleView = view.findViewById<TextView>(R.id.bottom_view_title)
                    val timeView = view.findViewById<TextView>(R.id.bottom_view_time)
                    val slide = Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, GlobalContext.instance.resources.configuration.layoutDirection))
                    slide.duration = 300
                    context.window.exitTransition = slide
                    val paris = arrayOf(CreateTransitionPair(titleView, R.string.bottom_news_share_title)
                            , CreateTransitionPair(timeView, R.string.bottom_news_share_time))
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
                val titleView = view.findViewById<TextView>(R.id.see_more_news_item_title)
                intent.putExtra("category", data.category)
                intent.putExtra("data", data)
                if (context is AppCompatActivity) {
                    val slide = Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, GlobalContext.instance.resources.configuration.layoutDirection))
                    slide.duration = 500
                    context.window?.exitTransition = slide
                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, titleView, context.getString(R.string.news_detail_share_title)).toBundle())
                } else {
                    context.startActivity(intent)
                }
            }
        })
    }

    override fun handleFolkHeritageAdapterItemCLick(context: Context, adapter: FolkRecyclerViewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                val intent = Intent(context, FolkInformationActivity::class.java)
                intent.putExtra(DATA, data)
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
                if (context is AppCompatActivity) {
                    val getview: View = view.findViewById(R.id.fragment_find_litview_img) ?: return
                    intent.putExtra("option", UserCommentDetailActivity.ANIMATION_SHOW)
                    ActivityCompat.startActivityForResult(context, intent, FindFragment.FROM_USER_COMMENT_DETAIL, ActivityOptions.makeSceneTransitionAnimation(context, getview, context.getString(R.string.find_share_view)).toBundle())
                } else {
                    if (context is Activity) {
                        context.startActivityForResult(intent, FindFragment.FROM_USER_COMMENT_DETAIL)
                    } else {
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
                intent.putExtra(DATA, data)
                if (context is AppCompatActivity) {
                    val userImage = view.findViewById<ImageView>(R.id.user_head_image)
                    val userName = view.findViewById<TextView>(R.id.user_name)
                    val pairs = arrayOf(CreateTransitionPair(userName, R.string.share_user_name),
                            CreateTransitionPair(userImage, R.string.share_user_image))
                    val transitionOptions = ActivityOptions.makeSceneTransitionAnimation(context, *pairs)
                    context.startActivity(intent, transitionOptions.toBundle())
                } else {
                    context.startActivity(intent)
                }
            }
        })
    }
}