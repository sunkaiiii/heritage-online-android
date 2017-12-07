package com.example.sunkai.heritage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleFolk;
import com.example.sunkai.heritage.Data.folkData;

/**
 * 此类用来处理预约界面
 */

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView join_activity_img;
    private TextView join_activity_title;
    private TextView join_activity_content;
    private Button join_activity_btn;
    private folkData folkActiviyData;
    private Bitmap bitmap;
    int accentColor;
    boolean isOrderd;//判断是否已经预约了
    private ActionBar actionBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        initView();
        folkActiviyData=(folkData) getIntent().getSerializableExtra("activity");
        if(null!=folkActiviyData.image) {
            bitmap = BitmapFactory.decodeByteArray(folkActiviyData.image, 0, folkActiviyData.image.length);
            join_activity_img.setImageBitmap(bitmap);
        }
        join_activity_title.setText(folkActiviyData.title);
        join_activity_content.setText(folkActiviyData.content);
        /**
         * 在页面显示的时候判断此用户是否已经预约此活动
         */
        join_activity_btn.setEnabled(false);
        new Thread(CheckUserOrderThread).start();
    }

    private void initView() {
        join_activity_img = (ImageView) findViewById(R.id.join_activity_img);
        join_activity_title = (TextView) findViewById(R.id.join_activity_title);
        join_activity_content = (TextView) findViewById(R.id.join_activity_content);
        join_activity_btn = (Button) findViewById(R.id.join_activity_btn);
        accentColor=join_activity_btn.getCurrentTextColor();
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);

        join_activity_btn.setOnClickListener(this);

        join_activity_img.setScaleType(ImageView.ScaleType.FIT_XY);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 根据用户是否已经预约，执行取消预约、预约
             */
            case R.id.join_activity_btn:
                if(LoginActivity.userID==0){
                    Toast.makeText(JoinActivity.this,"没有登录",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(JoinActivity.this,LoginActivity.class);
                    intent.putExtra("isInto",1);
                    startActivity(intent);
                    return;
                }
                if(isOrderd) {
                    new Thread(AddUserOrderThread).start();
                }
                else{
                    new Thread(CancelOrderThred).start();
                }
                break;
        }
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

    Runnable CheckUserOrderThread=new Runnable() {
        @Override
        public void run() {
            int result=HandleFolk.Check_User_Order(LoginActivity.userID,folkActiviyData.id);
            CheckUserOrderHandler.sendEmptyMessage(result);
        }
    };

    Runnable AddUserOrderThread=new Runnable() {
        @Override
        public void run() {
            boolean isSuccess;
            isSuccess=HandleFolk.Add_User_Order(LoginActivity.userID,folkActiviyData.id);
            Message msg=new Message();
//            System.out.println(isSuccess);
            if(isSuccess){
                msg.what=1;
                AddUserOrderHandler.sendMessage(msg);
            }
            else{
                msg.what=0;
                AddUserOrderHandler.sendMessage(msg);
            }
        }
    };

    Runnable CancelOrderThred=new Runnable() {
        @Override
        public void run() {
            boolean isSuccess;
            isSuccess=HandleFolk.Cancel_User_Order(LoginActivity.userID,folkActiviyData.id);
            if(isSuccess){
                CancelOrderHandler.sendEmptyMessage(1);
            }
            else{
                CancelOrderHandler.sendEmptyMessage(0);
            }
        }
    };

    Handler CheckUserOrderHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            join_activity_btn.setEnabled(true);
            if(msg.what==1){
                isOrderd=false;
                changeButton();
            }
            else{
                isOrderd=true;
                changeButton();
            }
        }
    };

    Handler AddUserOrderHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                Toast.makeText(JoinActivity.this,"预约成功",Toast.LENGTH_SHORT).show();
                isOrderd=false;
                changeButton();
            }
            else{
                Toast.makeText(JoinActivity.this,"预约失败",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Handler CancelOrderHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                Toast.makeText(JoinActivity.this,"取消成功",Toast.LENGTH_SHORT).show();
                isOrderd=true;
                changeButton();
                Intent intent=new Intent("android.intent.action.cancelOrderBroadCast");
                intent.putExtra("message","changed");
                sendBroadcast(intent);
            }
            else{
                Toast.makeText(JoinActivity.this,"取消失败",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void changeButton(){
        if(isOrderd){
            join_activity_btn.setTextColor(accentColor);
            join_activity_btn.setText("立即预约");
        }
        else{
//            join_activity_btn.setEnabled(false);
            join_activity_btn.setTextColor(Color.GRAY);
            join_activity_btn.setText("已预约,点击取消");
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        join_activity_btn.setEnabled(false);
        new Thread(CheckUserOrderThread).start();
    }
}
