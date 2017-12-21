package com.example.sunkai.heritage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.sunkai.heritage.Data.GlobalContext;
import com.example.sunkai.heritage.Data.MySqliteHandler;
import com.example.sunkai.heritage.Data.mySqlLite;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.zip.Inflater;

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
        String MODEL= Build.MODEL;
        String DEVICE=Build.DEVICE;
        final SharedPreferences sharedPreferences=getSharedPreferences("setting", Context.MODE_PRIVATE);
        int startCount=sharedPreferences.getInt("startCount",0);
        if(startCount==0) {
            startCount++;
            if (!MODEL.contains("xiaomi")) {
                final SharedPreferences.Editor editor = getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
                editor.putInt("startCount", startCount);
                editor.putBoolean("pushSwitch", false);
                editor.commit();
                View view = View.inflate(this, R.layout.push_warining_layout, null);
                AlertDialog ad = new AlertDialog.Builder(this).setTitle("是否开启推送？").setView(view).setPositiveButton("开启", (dialog, which) -> {
                    editor.putBoolean("pushSwitch", true);
                    editor.commit();
                    GlobalContext.Companion.getInstance().registMipush();
                }).setNegativeButton("关闭", (dialog, which) -> {

                }).create();
                ad.setOnDismissListener(dialog -> welcomeHandler.sendEmptyMessageDelayed(gotoLogin, 1000));
                ad.show();
            } else { //如果是小米手机的话，默认开启推送
                final SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                editor.putInt("startCount", startCount);
                editor.putBoolean("pushSwitch", true);
                editor.commit();
                welcomeHandler.sendEmptyMessageDelayed(gotoLogin, 1000);
            }
        }
        else {
            welcomeHandler.sendEmptyMessageDelayed(gotoLogin, 1000);
        }
    }
    private Handler welcomeHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            /**
             * 判断用户是否登陆过，如果登陆，则跳过登陆
             */
            final SharedPreferences sharedPreferences=getSharedPreferences("data", Context.MODE_PRIVATE);
            LoginActivity.userID=sharedPreferences.getInt("user_id",0);
            LoginActivity.userName=sharedPreferences.getString("user_name",null);
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
                        GlobalContext.Companion.getInstance().registUser();
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        }
    };
}
