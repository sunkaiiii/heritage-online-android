package com.example.sunkai.heritage.Fragment

import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.example.sunkai.heritage.Activity.AddFindCommentActivity
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.Activity.SearchActivity
import com.example.sunkai.heritage.Activity.UserCommentDetailActivity
import com.example.sunkai.heritage.Adapter.FindFragmentRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Data.FindActivityData
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * 发现页面的类
 */

class FindFragment : Fragment(), View.OnClickListener {
    private lateinit var tips: Array<ImageView?>
    private lateinit var mImageViews: Array<ImageView?>
    private lateinit var findSearchBtn: ImageView
    private lateinit var findEdit: TextView
    private lateinit var imgIdArray: IntArray
    internal lateinit var view: View
    private lateinit var recyclerViewAdpter: FindFragmentRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshBtn: FloatingActionButton
    private lateinit var selectSpiner: Spinner
    private lateinit var addCommentBtn: Button
    private lateinit var viewPager: ViewPager
    lateinit internal var activityDatas: List<FindActivityData>
    internal lateinit var btnAnimation: Animation
    private lateinit var bitmaps: Array<Bitmap?>
    internal var count = 0
    internal var count2 = 0

    internal var animationStopReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            refreshBtn.clearAnimation()
            refreshBtn.isEnabled = true
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_find, container, false)
        findEdit = view.findViewById(R.id.find_text)
        findSearchBtn = view.findViewById(R.id.find_searchbtn)
        findEdit.setOnClickListener(this)
        findSearchBtn.setOnClickListener(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.animationStop")
        activity!!.registerReceiver(animationStopReceiver, intentFilter)
        selectSpiner = view.findViewById(R.id.find_select_spinner)
        setHasOptionsMenu(true)
        bitmaps = arrayOfNulls(4)
        //主页活动页面
        imgIdArray = intArrayOf(R.drawable.backgound_grey, R.drawable.backgound_grey, R.drawable.backgound_grey, R.drawable.backgound_grey)
        viewPager = view.findViewById(R.id.find_fragment_viewPager)
        loadMyPage()
        //设置Adapter
        viewPager.adapter = MyAdapter()
        //设置监听，主要是设置点点的背景
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.currentItem = mImageViews.size * 100
        //        listView = (ListView) view.findViewById(R.id.fragment_find_listview);
        recyclerView = view.findViewById(R.id.fragment_find_recyclerView)
        activityDatas = ArrayList()
        loadMyPage()
        viewPager.adapter = MyAdapter()
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                getActivityID(position % 4, this@FindFragment).execute()
                setImageBackground(position % mImageViews.size)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        //加载轮播页第一张图片
        getActivityID(0, this).execute()

        /*
         * 程序默认显示广场的全部帖子
         */
        recyclerViewAdpter = FindFragmentRecyclerViewAdapter(activity!!, 1)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerViewAdpter
        refreshBtn = view.findViewById(R.id.fragment_find_refreshbtn)
        refreshBtn.setOnClickListener { _ ->
            btnAnimation = AnimationUtils.loadAnimation(context, R.anim.refresh_button_rotate)
            refreshBtn.startAnimation(btnAnimation)
            refreshBtn.isEnabled = false
            recyclerViewAdpter.reFreshList()
        }

        /*
            * Spinear切换，重新加载adpater的数据
            */
        selectSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        recyclerViewAdpter = FindFragmentRecyclerViewAdapter(activity!!, 1)
                        setAdpterClick(recyclerViewAdpter)
                        recyclerView.adapter = recyclerViewAdpter
                    }
                    1 -> {
                        if (LoginActivity.userID == 0) {
                            Toast.makeText(activity, "没有登录", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, LoginActivity::class.java)
                            intent.putExtra("isInto", 1)
                            startActivityForResult(intent, 1)
                            selectSpiner.setSelection(0)
                            return
                        }
                        recyclerViewAdpter = FindFragmentRecyclerViewAdapter(activity!!, 2)
                        setAdpterClick(recyclerViewAdpter)
                        recyclerView.adapter = recyclerViewAdpter
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        /*
         * 发帖
         */
        addCommentBtn = view.findViewById(R.id.btn_add_comment)
        addCommentBtn.setOnClickListener { _ ->
            if (LoginActivity.userID == 0) {
                Toast.makeText(activity, "没有登录", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("isInto", 1)
                startActivityForResult(intent, 1)
                return@setOnClickListener
            }
            val intent = Intent(activity, AddFindCommentActivity::class.java)
            /*
     * 当成功添加帖子的时候，页面刷新
     */
            startActivityForResult(intent, 1)
        }
        return view
    }

    private fun setAdpterClick(adpter: FindFragmentRecyclerViewAdapter?) {
        adpter!!.setOnItemClickListen(object :OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(activity, UserCommentDetailActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("data", adpter.getItem(position))
                bundle.putInt("position", position)
                val getview:View? = view.findViewById(R.id.fragment_find_litview_img)
                getview?.let {
                    val imageView=getview as ImageView
                    imageView.isDrawingCacheEnabled = true
                    val drawable = imageView.drawable
                    val bitmapDrawable = drawable as BitmapDrawable
                    val bitmap = bitmapDrawable.bitmap
                    val out = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    intent.putExtra("bitmap", out.toByteArray())
                    intent.putExtras(bundle)

                    //如果手机是Android 5.0以上的话，使用新的Activity切换动画
                    if(Build.VERSION.SDK_INT>=21)
                        startActivityForResult(intent, FROM_USER_COMMENT_DETAIL,ActivityOptions.makeSceneTransitionAnimation(activity,imageView,"shareView").toBundle())
                    else
                        startActivityForResult(intent, FROM_USER_COMMENT_DETAIL)
                }
            }
        })
    }

    /**
     * 加载首页轮转窗
     */
    fun loadMyPage() {
        val group = view.findViewById<ViewGroup>(R.id.find_fragment_imageView)
        tips = arrayOfNulls(imgIdArray.size)
        for (i in tips.indices) {
            val imageView = ImageView(activity)
            imageView.layoutParams = ViewGroup.LayoutParams(10, 10)
            tips[i] = imageView
            if (i == 0) {
                tips[i]?.setBackgroundResource(R.drawable.page_indicator_focused)
            } else {
                tips[i]?.setBackgroundResource(R.drawable.page_indicator_unfocused)
            }
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))
            layoutParams.leftMargin = 5
            layoutParams.rightMargin = 5
            if (count >= tips.size) {
                group.addView(imageView, layoutParams)
                count2++
            }
        }
        mImageViews = arrayOfNulls(imgIdArray.size)
        for (i in mImageViews.indices) {
            val imageView = ImageView(activity)
            mImageViews[i] = imageView
            if (count < mImageViews.size) {
                bitmaps[i] = HandlePic.handlePic(GlobalContext.instance, imgIdArray[i], 0)
                count++
            }
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageBitmap(bitmaps[i])
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.find_text, R.id.find_searchbtn -> {
                if (LoginActivity.userID == 0) {
                    Toast.makeText(activity, "没有登录", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.putExtra("isInto", 1)
                    startActivityForResult(intent, 1)
                }
                val intent = Intent(activity, SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }

    inner class MyAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return Integer.MAX_VALUE
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(mImageViews[position % mImageViews.size])
        }

        /*
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(mImageViews[position % mImageViews.size], 0)
            return mImageViews[position % mImageViews.size] as ImageView
        }

    }

    private fun setImageBackground(selectItems: Int) {
        for (i in tips.indices) {
            if (i == selectItems) {
                tips[i]?.setBackgroundResource(R.drawable.page_indicator_focused)
            } else {
                tips[i]?.setBackgroundResource(R.drawable.page_indicator_unfocused)
            }
        }
    }


    /**
     * 按照图片的id获取首页浮窗信息
     */
    internal class getActivityID internal constructor(var index: Int, findFragment: FindFragment) : AsyncTask<Void, Void, Bitmap>() {
        lateinit var db: SQLiteDatabase
        var findFragmentWeakReference: WeakReference<FindFragment>

        init {
            findFragmentWeakReference = WeakReference(findFragment)
        }

        override fun doInBackground(vararg voids: Void): Bitmap? {
            val findFragment = findFragmentWeakReference.get() ?: return null
            if (findFragment.activityDatas.isEmpty()) {
                val getDatas=HandleFind.Get_Find_Activity_ID(findFragment.activityDatas)
                getDatas?.let {
                    findFragment.activityDatas = getDatas
                }
            }
            db = MySqliteHandler.GetReadableDatabase()
            val table = "find_fragment_activity"
            val selection = "id=?"
            if (findFragment.activityDatas.isEmpty())
                return null
            val selectionArgs = arrayOf(findFragment.activityDatas[index].id.toString())
            val cursor = db.query(table, null, selection, selectionArgs, null, null, null)
            cursor.moveToFirst()
            if (!cursor.isAfterLast) {
                val imageIndex = cursor.getColumnIndex("image")
                val img = cursor.getBlob(imageIndex)
                cursor.close()
                if (img != null) {
                    val `in` = ByteArrayInputStream(img)
                    return HandlePic.handlePic(`in`, 0)
                }
            }
            val data:FindActivityData? = HandleFind.Get_Find_Activity_Information(findFragment.activityDatas[index].id)
            data?.let {
                findFragment.activityDatas[index].title = data.title
                findFragment.activityDatas[index].content = data.content
                findFragment.activityDatas[index].image = data.image
                val `in` = ByteArrayInputStream(findFragment.activityDatas[index].image)
                val bitmap = HandlePic.handlePic(`in`, 0)
                db = MySqliteHandler.GetWritableDatabase()
                val contentValues = ContentValues()
                contentValues.put("id", data.id)
                contentValues.put("title", data.title)
                contentValues.put("image", data.image)
                contentValues.put("content", data.content)
                db.insert(table, null, contentValues)
                return bitmap
            }
            return null
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            val findFragment = findFragmentWeakReference.get() ?: return
            findFragment.mImageViews[index]?.scaleType = ImageView.ScaleType.CENTER_CROP
            findFragment.mImageViews[index]?.setImageBitmap(bitmap)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> {
                startRefreshButtonAnimation()
                recyclerViewAdpter.reFreshList()
            }
            FROM_USER_COMMENT_DETAIL -> if (resultCode == UserCommentDetailActivity.ADD_COMMENT) {
                val bundle = data!!.extras
                val commentID = bundle?.getInt("commentID") ?: BUNDLE_ERROR
                val position = bundle?.getInt("position") ?: BUNDLE_ERROR
                if (commentID != BUNDLE_ERROR && position != BUNDLE_ERROR) {
                    recyclerViewAdpter.getReplyCount(commentID, position)
                }
            } else if (resultCode == UserCommentDetailActivity.DELETE_COMMENT) {
                startRefreshButtonAnimation()
                recyclerViewAdpter.reFreshList()
            }
        }
    }

    private fun startRefreshButtonAnimation() {
        btnAnimation = AnimationUtils.loadAnimation(context, R.anim.refresh_button_rotate)
        refreshBtn.startAnimation(btnAnimation)
        refreshBtn.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(animationStopReceiver)
    }

    companion object {
        private val FROM_USER_COMMENT_DETAIL = 2

        private val BUNDLE_ERROR = -4
    }
}
