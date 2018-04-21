package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import android.widget.TextView
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.FocusListviewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.FollowInformation
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.TransitionHelper
import com.example.sunkai.heritage.value.*
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_focus_information.*

/**
 * 用户粉丝、关注列表的Activity
 */
class FocusInformationActivity : BaseGlideActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus_information)
        GetInformationToListView()
    }

    private fun GetInformationToListView() {
        val list: handleList
        /**
         * 判断是点击我的关注还是从我的粉丝进来的，从而执行不同的方法
         */
        when (intent.getStringExtra(INFORMATION)) {
            FROM_FOCUS -> {
                list = handleList(FOLLOW, FROM_PERSON)
                list.getFollowInformaiton()
            }
            FROM_FANS -> {
                list = handleList(FANS, FROM_PERSON)
                list.getFansInformation()
            }
            OTHER_FOLLOW.toString() -> {
                val userID = intent.getIntExtra(USER_ID, NO_USERID)
                if (userID != NO_USERID) {
                    list = handleList(FOLLOW, FROM_OTHER_PERSON, userID)
                    list.getFollowInformaiton()
                }
            }
            OTHER_FANS.toString() -> {
                val userID = intent.getIntExtra(USER_ID, NO_USERID)
                if (userID != NO_USERID) {
                    list = handleList(FANS, FROM_OTHER_PERSON, userID)
                    list.getFansInformation()
                }
            }
        }
    }

    private inner class handleList(what: Int, from: Int, userID: Int = LoginActivity.userID)
    /**
     * @param what 为1的时候说明是关注界面，为2的时候为粉丝界面
     */
    {
        private val userID: Int
        private val what: Int

        init {
            this.userID = userID
            this.what = what
        }

        /**
         * 根据what的不同，执行不同的方法
         */
        fun getFollowInformaiton() {
            requestHttp(getFollowinformation)
        }

        fun getFansInformation() {
            requestHttp(getFansinformation)
        }

        private val getFollowinformation = Runnable {
            //判断是从个人中心进入还是从其他用户列表页
            val datas = if (from == FROM_PERSON) {
                HandlePerson.GetFollowInformation(userID)
            } else {
                HandlePerson.GetOtherFollowInformation(LoginActivity.userID, userID)
            }
            runOnUiThread {
                setAdpter(datas)
            }

        }
        private val getFansinformation = Runnable {
            //判断是从个人中心进入还是从其他用户列表页
            val datas = if (from == FROM_PERSON) {
                HandlePerson.GetFansInformation(userID)
            } else {
                HandlePerson.GetOtherFansInformation(LoginActivity.userID, userID)
            }
            runOnUiThread {
                setAdpter(datas)
            }

        }


        private fun setAdpter(datas: List<FollowInformation>) {
            val adapter = FocusListviewAdapter(this@FocusInformationActivity, what, datas,glide)
            adapter.setOnFocusChangeListener(object : OnFocusChangeListener {
                override fun onFocusChange() {
                    setResult(STATE_CHANGE)
                }

            })
            setAdapterClick(adapter)
            focus_information_recyclerview.adapter = adapter
        }
    }

    private fun setAdapterClick(adapter: FocusListviewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@FocusInformationActivity, OtherUsersActivity::class.java)
                val data = adapter.getItem(position)
                intent.putExtra(USER_ID, data.focusFansID)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val userName = view.findViewById<TextView>(R.id.user_name)
                    val userImage = view.findViewById<RoundedImageView>(R.id.user_head_image)
                    val pairs = TransitionHelper.createSafeTransitionParticipants(this@FocusInformationActivity, false, Pair(userName, getString(R.string.share_user_name)), Pair(userImage, getString(R.string.share_user_image)))
                    val transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this@FocusInformationActivity, *pairs)
                    startActivity(intent, transitionOptions.toBundle())
                } else {
                    startActivity(intent)
                }
            }

        })
    }

    override fun onClick(v: View) {}

    companion object {
        const val FROM_PERSON = 1
        const val FROM_OTHER_PERSON = 2
    }
}
