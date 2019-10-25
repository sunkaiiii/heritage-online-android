package com.example.sunkai.heritage.tools

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.sunkai.heritage.activity.*
import com.example.sunkai.heritage.adapter.*
import com.example.sunkai.heritage.interfaces.IHandleAdapterItemClick
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.ACTIVITY_FRAGMENT
import com.example.sunkai.heritage.value.ALL_FOLK_INFO_ACTIVITY
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.FROM

/**
 * 对adapter的item点击事件进行了统一的封装
 */

object HandleAdapterItemClickClickUtils : IHandleAdapterItemClick {


    override fun handleActivityRecyclerViewItemClick(context: Context, adapter: ActivityRecyclerViewAdapter) {
        adapter.setOnItemClickListener { view, position ->
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
    }

    override fun handleBottomNewsAdapterItemClick(context: Context, adapter: BottomFolkNewsRecyclerviewAdapter) {
        adapter.setOnItemClickListener { view, position ->
            val data = adapter.getItem(position)
            val intent = Intent(context, BottomNewsDetailActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }
    }

    override fun handleAllFolkNewsAdapterItemClick(context: Context, adapter: SeeMoreNewsRecyclerViewAdapter) {
        adapter.setOnItemClickListener { view, position ->
            val data = adapter.getItem(position)
            val intent = Intent(context, NewsDetailActivity::class.java)
            val titleView = view.findViewById<TextView>(R.id.see_more_news_item_title)
            intent.putExtra("category", data.category)
            intent.putExtra("data", data)
            if (context is AppCompatActivity) {
                val slide = Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, GlobalContext.instance.resources.configuration.layoutDirection))
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, titleView, context.getString(R.string.news_detail_share_title)).toBundle())
            } else {
                context.startActivity(intent)
            }
        }
    }

    override fun handleFolkHeritageAdapterItemCLick(context: Context, adapter: FolkRecyclerViewAdapter) {
        adapter.setOnItemClickListener { view, position ->
            val data = adapter.getItem(position)
            val intent = Intent(context, FolkInformationActivity::class.java)
            intent.putExtra(DATA, data)
            intent.putExtra(FROM, ALL_FOLK_INFO_ACTIVITY)
            context.startActivity(intent)
        }
    }


}