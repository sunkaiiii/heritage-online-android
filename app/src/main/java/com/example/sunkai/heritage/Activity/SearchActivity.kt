package com.example.sunkai.heritage.Activity


import android.content.Intent
import android.os.*
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.example.sunkai.heritage.Activity.BaseActivity.BaseStopGlideActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.SearchUserRecclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.TransitionHelper
import com.example.sunkai.heritage.value.STATE_CHANGE
import com.example.sunkai.heritage.value.USER_ID
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_search.*

/**
 * 此类用于处理用户搜索的页面
 */
class SearchActivity : BaseStopGlideActivity(), View.OnClickListener, TextView.OnEditorActionListener,TextWatcher {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        search_activity_btn.setOnClickListener(this)
        search_activity_edit.setOnEditorActionListener(this)
        search_activity_edit.addTextChangedListener(this)
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
            toast("内容不能为空")
            return
        }
        //将搜索的文本传入搜索类，并搜索内容
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
            val adapter = SearchUserRecclerAdapter(this, searchData,glide)
            setListener(adapter)
            setItemClick(adapter)
            if(isDestroy)return@execute
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
                intent.putExtra(USER_ID, data.id)
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

    //处理postDelay事件，当收到事件的时候自动搜索用户输入的内容
    private val timeTaskHandler= object:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message?) {
            if(msg?.what==SEARCH_USER_MESSAGE){
                val text=search_activity_edit.text.toString().trim()
                if(TextUtils.isEmpty(text)){
                    return
                }
                searchClass(text)
            }
        }
    }
    //当用户输入之后，自动搜索用户
    //设置一个DELAY，减少请求的次数
    override fun afterTextChanged(s: Editable?) {
        if(timeTaskHandler.hasMessages(SEARCH_USER_MESSAGE)){
            timeTaskHandler.removeMessages(SEARCH_USER_MESSAGE)
        }
        timeTaskHandler.sendEmptyMessageDelayed(SEARCH_USER_MESSAGE, DELAY)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        if(timeTaskHandler.hasMessages(SEARCH_USER_MESSAGE)){
            timeTaskHandler.removeMessages(SEARCH_USER_MESSAGE)
        }

    }

    companion object {
        const val SEARCH_USER_MESSAGE=1
        const val DELAY=800L
    }
}
