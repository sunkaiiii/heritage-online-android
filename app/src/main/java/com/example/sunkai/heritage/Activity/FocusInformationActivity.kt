package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ListView

import com.example.sunkai.heritage.Adapter.FocusListviewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.FocusData
import com.example.sunkai.heritage.R

import org.kobjects.base64.Base64

class FocusInformationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var focus_information_listview: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus_information)
        initView()
        GetInformationToListView()
    }

    private fun initView() {
        focus_information_listview = findViewById<View>(R.id.focus_information_listview) as ListView
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun GetInformationToListView() {
        val list: handleList
        /**
         * 判断是点击我的关注还是从我的粉丝进来的，从而执行不同的方法
         */
        when (intent.getStringExtra("information")) {
            "focus" -> {
                list = handleList(1)
                list.getFollowInformaiton()
            }
            "fans" -> {
                list = handleList(2)
                list.getFansInformation()
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

    internal inner class handleList
    /**
     *
     * @param what 为1的时候说明是关注界面，为2的时候为粉丝界面
     */
    (var what: Int) {
        var datas: List<FocusData>? = null
        private var adapter: FocusListviewAdapter? = null
        private val getFollowinformation = Runnable {
            datas = HandlePerson.Get_Follow_Information(LoginActivity.userID)
            datas?.let {
                runOnUiThread {
                    setAdpter()
                    Thread(getUsersImage).start()
                }
            }
        }
        private val getFansinformation = Runnable {
            datas = HandlePerson.Get_Fans_Information(LoginActivity.userID)
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
                if (adapter?.what == 1) {
                    result = HandlePerson.Get_User_Image(adapter!!.datas[i].focusFansID)
                } else {
                    result = HandlePerson.Get_User_Image(adapter!!.datas[i].focusUserid)
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
