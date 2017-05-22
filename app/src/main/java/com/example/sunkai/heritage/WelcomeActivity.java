package com.example.sunkai.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * 此页面是欢迎界面的类
 */
public class WelcomeActivity extends AppCompatActivity {
    private static int gotoLogin=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
//        try{
//            Thread.sleep(1000);
//        }
//        catch (InterruptedException e){
//            e.printStackTrace();
//        }
        welcomeHandler.sendEmptyMessageDelayed(gotoLogin,1000);
    }
    private Handler welcomeHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            /**
             * 判断用户是否登陆过，如果登陆，则跳过登陆
             */
            if(msg.what==gotoLogin){
                Intent intent;
                switch (LoginActivity.userID){
                    case 0:
                        intent=new Intent(WelcomeActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        intent=new Intent(WelcomeActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

            }
        }
    };
}
