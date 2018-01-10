package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View

import com.example.sunkai.heritage.Adapter.FocusListviewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.FocusData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.FANS
import com.example.sunkai.heritage.value.FOLLOW
import com.example.sunkai.heritage.value.NO_USERID
import kotlinx.android.synthetic.main.activity_focus_information.*

import org.kobjects.base64.Base64

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
            "focus"-> {
                list = handleList(1)
                list.getFollowInformaiton()
            }
            "fans"-> {
                list = handleList(2)
                list.getFansInformation()
            }
            FOLLOW.toString()->{
                val userID=intent.getIntExtra("userID", NO_USERID)
                if(userID!= NO_USERID) {
                    list = handleList(FOLLOW,userID)
                    list.getFollowInformaiton()
                }
            }
            FANS.toString()->{
                val userID=intent.getIntExtra("userID", NO_USERID)
                if(userID!= NO_USERID) {
                    list = handleList(FANS,userID)
                    list.getFansInformation()
                }
            }
            else -> {
            }
        }

    }

    override fun onClick(v: View) {}
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    internal inner class handleList(what: Int,userID:Int=LoginActivity.userID)
    /**
     *
     * @param what 为1的时候说明是关注界面，为2的时候为粉丝界面
     */
    {
        val userID:Int
        val what:Int
        init{
            this.userID=userID
            this.what=what
        }
        var datas: List<FocusData>? = null
        private var adapter: FocusListviewAdapter? = null
        private val getFollowinformation = Runnable {
            datas = HandlePerson.Get_Follow_Information(userID)
            datas?.let {
                runOnUiThread {
                    setAdpter()
                    Thread(getUsersImage).start()
                }
            }
        }
        private val getFansinformation = Runnable {
            datas = HandlePerson.Get_Fans_Information(userID)
            datas?.let {
                runOnUiThread {
                    setAdpter()
                    Thread(getUsersImage).start()
                }
            }
        }
        private val getUsersImage = Runnable {
            for (i in adapter!!.datas.indices) {
                val result: String?
                result = if (adapter?.what == 1) {
                    HandlePerson.Get_User_Image(adapter!!.datas[i].focusFansID)
                } else {
                    HandlePerson.Get_User_Image(adapter!!.datas[i].focusUserid)
                }
                if (!("Error" == result || null == result)) {
                    adapter!!.datas[i].userImage = Base64.decode(result)
                    runOnUiThread { adapter!!.notifyDataSetChanged() }
                }
            }
        }


        fun setAdpter() {
            datas?.let {
                adapter = FocusListviewAdapter(this@FocusInformationActivity, datas!!, what)
                focus_information_listview.adapter = adapter
            }
        }

        /**
         * 根据what的不同，执行不同的方法
         */
        fun getFollowInformaiton() {
            Thread(getFollowinformation).start()
        }

        fun getFansInformation() {
            Thread(getFansinformation).start()
        }
    }
}
