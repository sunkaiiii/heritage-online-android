package com.example.sunkai.heritage.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ListView

import com.example.sunkai.heritage.Adapter.MyOrderListViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.FolkData
import com.example.sunkai.heritage.R

/**
 * 此类用于处理我的预约
 */

class MyOrderActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var activity_my_order_listview: ListView
    private var datas: List<FolkData>?=null
    private lateinit var adapter: MyOrderListViewAdapter

    internal var GetUserOrderByUserThread: Runnable = Runnable {
        datas = HandleFolk.Get_User_Orders(LoginActivity.userID)
        runOnUiThread {
            adapter = MyOrderListViewAdapter(this@MyOrderActivity, datas)
            activity_my_order_listview.adapter = adapter
        }
    }


    /**
     * 当预约情况发生改变的时候，重新读取预约内容
     */
    internal var cancelOrderBroadcastRecevier: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val result: String? = intent.getStringExtra("message")
            if (null != result && "changed" == result) {
                Thread(GetUserOrderByUserThread).start()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order)
        initView()
        val filter = IntentFilter()
        /**
         * 添加一个广播，用于当用户取消预约、预约的时候发送广播，重新读取我的预约
         */
        filter.addAction("android.intent.action.cancelOrderBroadCast")

        Thread(GetUserOrderByUserThread).start()
        this.registerReceiver(cancelOrderBroadcastRecevier, filter)

        activity_my_order_listview.setOnItemClickListener { parent, _, position, _ ->
            val bundle = Bundle()
            bundle.putSerializable("activity", parent.getItemAtPosition(position) as FolkData)
            val intent = Intent(this@MyOrderActivity, JoinActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun initView() {
        activity_my_order_listview = findViewById(R.id.activity_my_order_listview)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onClick(v: View) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(cancelOrderBroadcastRecevier)
    }


}
