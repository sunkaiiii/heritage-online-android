package com.example.sunkai.heritage.Activity


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.SearchUserRecclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.TransitionHelper
import com.example.sunkai.heritage.value.STATE_CHANGE
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_search.*

/**
 * 此类用于处理用户搜索的页面
 */
class SearchActivity : AppCompatActivity(), View.OnClickListener, TextView.OnEditorActionListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        search_activity_btn.setOnClickListener(this)
        search_activity_edit.setOnEditorActionListener(this)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        when (actionId) {
            EditorInfo.IME_ACTION_SEARCH -> submit()
        }
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.search_activity_btn -> submit()
        }
    }

    private fun submit() {
        val edit = search_activity_edit.text.toString().trim()
        if (TextUtils.isEmpty(edit)) {
            MakeToast.MakeText("内容不能为空")
            return
        }
        /**
         * 将搜索的文本传入搜索类，并搜索内容
         */
        searchClass(edit)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchClass(searchText: String) {
        ThreadPool.execute {
            val searchData = HandlePerson.GetSearchUserInfo(searchText, LoginActivity.userID)
            val adapter = SearchUserRecclerAdapter(this, searchData)
            setListener(adapter)
            setItemClick(adapter)
            runOnUiThread {
                searchActivityList.adapter = adapter
            }
        }
    }

    private fun setListener(adapter: SearchUserRecclerAdapter) {
        adapter.setOnFocusChangeListener(object : OnFocusChangeListener {
            override fun onFocusChange() {
                setResult(STATE_CHANGE)
            }
        })
    }

    private fun setItemClick(adapter: SearchUserRecclerAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                val intent = Intent(this@SearchActivity, OtherUsersActivity::class.java)
                intent.putExtra("userID", data.id)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val userImage = view.findViewById<RoundedImageView>(R.id.user_head_image)
                    val userName = view.findViewById<TextView>(R.id.user_name)
                    val pairs = TransitionHelper.createSafeTransitionParticipants(this@SearchActivity, false, Pair(userName, getString(R.string.share_user_name)), Pair(userImage, getString(R.string.share_user_image)))
                    val transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SearchActivity, *pairs)
                    startActivity(intent, transitionOptions.toBundle())
                } else {
                    startActivity(intent)
                }
            }
        })
    }

}
