package com.example.sunkai.heritage.Fragment

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.cardview.widget.CardView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Activity.NewsDetailActivity
import com.example.sunkai.heritage.Activity.SeeMoreNewsActivity
import com.example.sunkai.heritage.ConnectWebService.BaseSetting
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.tools.generateDarkColor
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.CATEGORIES
import com.example.sunkai.heritage.value.HOST
import com.example.sunkai.heritage.value.MAIN_PAGE_CATEGORY_NEWS_IMAGE
import kotlinx.android.synthetic.main.main_news_fragment_layout.*

class MainNewsFragment : BaseLazyLoadFragment(), OnPageLoaded {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_news_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainNewsSwipeRefresh.setOnRefreshListener { startLoadInformation() }
    }

    override fun startLoadInformation() {
        onPreLoad()
        loadCategoryNews()
    }

    private fun loadCategoryNews() {
        requestHttp {
            val news = HandleMainFragment.ReadMainNews()
            runOnUiThread {
                val activity = activity ?: return@runOnUiThread
                val views = createNewsListView(news, activity)
                views.forEach { mainNewsList.addView(it) }
                onPostLoad()
            }
        }

    }

    //创建每个分类的卡片
    private fun createNewsListView(news: List<List<FolkNewsLite>>, activity: androidx.fragment.app.FragmentActivity): List<androidx.cardview.widget.CardView> {
        val cardViews = arrayListOf<androidx.cardview.widget.CardView>()
        for ((i, folkNews) in news.withIndex()) {
            cardViews.add(createSecondViews(folkNews, activity, i))
        }
        return cardViews
    }

    //构建每个卡片的item
    private fun createSecondViews(it: List<FolkNewsLite>, activity: androidx.fragment.app.FragmentActivity, position: Int): androidx.cardview.widget.CardView {
        val view = LayoutInflater.from(activity).inflate(R.layout.main_news_card, mainNewsList, false)
        val seeMoreTextview = view.findViewById<TextView>(R.id.see_more)
        val linearLayout = view.findViewById<LinearLayout>(R.id.main_news_other_news)
        val categoryTextView = view.findViewById<TextView>(R.id.main_news_card_category)
        val categoryBackGroundImageView = view.findViewById<ImageView>(R.id.main_news_card_category_image)
        categoryTextView.text = CATEGORIES[position]
        setClick(position, seeMoreTextview, activity, categoryBackGroundImageView)
        it.forEach {
            setNewsItem(it, linearLayout, activity)
        }
        getCategoryImage(position, categoryBackGroundImageView, linearLayout, seeMoreTextview)
        return view as androidx.cardview.widget.CardView
    }

    private fun getCategoryImage(position: Int, imageView: ImageView, linearLayout: LinearLayout, seeMoreTextview: TextView) {
        glide.load(HOST + "/img/main_news_divide_img/" + MAIN_PAGE_CATEGORY_NEWS_IMAGE[position]).into(categoryImageSimpleTarget(imageView, linearLayout, seeMoreTextview))
    }

    private fun setClick(position: Int, textView: TextView, context: Context, categoryBackgroundImageView: ImageView) {
        val category = CATEGORIES[position]
        textView.setOnClickListener {
            loadSeemoreNewsActivity(category, context)
        }
        categoryBackgroundImageView.setOnClickListener {
            loadSeemoreNewsActivity(category, context)
        }
    }

    private fun loadSeemoreNewsActivity(category: String, context: Context) {
        val intent = Intent(context, SeeMoreNewsActivity::class.java)
        intent.putExtra("category", category)
        context.startActivity(intent)
    }

    private fun setNewsItem(item: FolkNewsLite, linearLayout: LinearLayout, context: Context) {
        val itemView = LayoutInflater.from(context).inflate(R.layout.main_news_item, linearLayout, false)
        val itemHolder = secondlyView(itemView)
        itemView.setOnClickListener {
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra("data", item)
            intent.putExtra("category", item.category)
            val activity = activity
            if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity, itemHolder.title, getString(R.string.news_detail_share_title)).toBundle())
            } else {
                startActivity(intent)
            }
        }
        itemHolder.title.text = item.title
        if (TextUtils.isEmpty(item.img)) {
            itemHolder.image.visibility = View.INVISIBLE
        } else {
            glide.load(BaseSetting.URL + item.img).into(simpleTarget(itemHolder))
            itemHolder.image.setOnClickListener {
                val activity=activity?:return@setOnClickListener
                ViewImageUtils.setViewImageClick(activity,itemHolder.image,item.img)
            }
        }
        linearLayout.addView(itemView)
    }

    private class secondlyView(view: View) {
        val image: ImageView
        val title: TextView

        init {
            image = view.findViewById(R.id.news_item_image)
            title = view.findViewById(R.id.news_item_title)
        }
    }

    private class simpleTarget(private val holder: secondlyView) : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            holder.image.setImageDrawable(resource)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            holder.image.visibility = View.INVISIBLE
        }

    }

    private class categoryImageSimpleTarget(val imageView: ImageView, val linearLayout: LinearLayout, val seeMoreTextView: TextView) : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            val darkColor = resource.generateDarkColor()
            val animation = AnimationUtils.loadAnimation(GlobalContext.instance, R.anim.fade_in_quick)
            imageView.setImageDrawable(resource)
            imageView.startAnimation(animation)
            linearLayout.children.forEach { it.findViewById<TextView>(R.id.news_item_title)?.setTextColor(darkColor) }
            seeMoreTextView.setTextColor(darkColor)
        }

    }

    override fun onPreLoad() {
        mainNewsList.removeAllViews()
        mainNewsSwipeRefresh.isRefreshing = true
    }

    override fun onPostLoad() {
        mainNewsSwipeRefresh?.isRefreshing = false
    }

    override fun onRestoreFragmentLoadInformation() {
        startLoadInformation()
    }

}