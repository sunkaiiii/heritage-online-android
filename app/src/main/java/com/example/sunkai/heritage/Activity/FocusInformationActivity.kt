package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.FocusListviewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.FollowInformation
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.activity_focus_information.*

class FocusInformationActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus_information)
        initView()
        GetInformationToListView()
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun GetInformationToListView() {
        val list: handleList
        /**
         * 判断是点击我的关注还是从我的粉丝进来的，从而执行不同的方法
         */
        when (intent.getStringExtra("information")) {
            "focus" -> {
                list = handleList(FOLLOW, FROM_PERSON)
                list.getFollowInformaiton()
            }
            "fans" -> {
                list = handleList(FANS, FROM_PERSON)
                list.getFansInformation()
            }
            OTHER_FOLLOW.toString() -> {
                val userID = intent.getIntExtra("userID", NO_USERID)
                if (userID != NO_USERID) {
                    list = handleList(FOLLOW, FROM_OTHER_PERSON, userID)
                    list.getFollowInformaiton()
                }
            }
            OTHER_FANS.toString() -> {
                val userID = intent.getIntExtra("userID", NO_USERID)
                if (userID != NO_USERID) {
                    list = handleList(FANS, FROM_OTHER_PERSON, userID)
                    list.getFansInformation()
                }
            }
            else -> {
            }
        }

    }

    internal inner class handleList(what: Int, from: Int, userID: Int = LoginActivity.userID)
    /**
     *
     * @param what 为1的时候说明是关注界面，为2的时候为粉丝界面
     */
    {
        val userID: Int
        private val what: Int

        init {
            this.userID = userID
            this.what = what
        }

        private val getFollowinformation = Runnable {
            val datas=if(from== FROM_PERSON) {
                HandlePerson.GetFollowInformation(userID)
            }else{
                HandlePerson.GetOtherFollowInformation(LoginActivity.userID,userID)
            }
            runOnUiThread {
                setAdpter(datas)
            }

        }
        private val getFansinformation = Runnable {
            val datas = if(from== FROM_PERSON){
                HandlePerson.GetFansInformation(userID)
            }else{
                HandlePerson.GetOtherFansInformation(LoginActivity.userID,userID)
            }
            runOnUiThread {
                setAdpter(datas)
            }

        }


        private fun setAdpter(datas: List<FollowInformation>) {
            val adapter = FocusListviewAdapter(this@FocusInformationActivity, what, datas)
            adapter.setOnFocusChangeListener(object : OnFocusChangeListener {
                override fun onFocusChange() {
                    setResult(STATE_CHANGE)
                }

            })
            focus_information_recyclerview.adapter = adapter
        }

        /**
         * 根据what的不同，执行不同的方法
         */
        fun getFollowInformaiton() {
            ThreadPool.execute(getFollowinformation)
        }

        fun getFansInformation() {
            ThreadPool.execute(getFansinformation)
        }
    }

    override fun onClick(v: View) {}
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val FROM_PERSON = 1
        const val FROM_OTHER_PERSON = 2
    }
}
