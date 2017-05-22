package com.example.sunkai.heritage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sunkai.heritage.ConnectWebService.HandleFolk;
import com.example.sunkai.heritage.Data.folkData;

import java.util.List;

/**
 * 此类用于处理我的预约
 */

public class MyOrderActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView activity_my_order_listview;
    private List<folkData> datas;
    private myOrderListViewAdapter adapter;
    private ActionBar actionBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        initView();
        IntentFilter filter=new IntentFilter();
        /**
         * 添加一个广播，用于当用户取消预约、预约的时候发送广播，重新读取我的预约
         */
        filter.addAction("android.intent.action.cancelOrderBroadCast");

        new Thread(GetUserOrderByUserThread).start();
        this.registerReceiver(cancelOrderBroadcastRecevier,filter);

        activity_my_order_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("activity",(folkData)parent.getItemAtPosition(position));
                Intent intent=new Intent(MyOrderActivity.this,JoinActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        activity_my_order_listview = (ListView) findViewById(R.id.activity_my_order_listview);
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v){
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    Runnable GetUserOrderByUserThread=new Runnable() {
        @Override
        public void run() {
            datas= HandleFolk.Get_User_Orders(LoginActivity.userID);
            if(null!=datas){
                GetUserOrderByUserHandler.sendEmptyMessage(1);
            }
        }
    };

    Handler GetUserOrderByUserHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                adapter=new myOrderListViewAdapter(MyOrderActivity.this,datas);
                activity_my_order_listview.setAdapter(adapter);
            }
        }
    };

    /**
     * 当预约情况发生改变的时候，重新读取预约内容
     */
    BroadcastReceiver cancelOrderBroadcastRecevier=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result;
            result=intent.getStringExtra("message");
            if(null!=result&&"changed".equals(result)){
                new Thread(GetUserOrderByUserThread).start();
            }
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(cancelOrderBroadcastRecevier);
    }


}
