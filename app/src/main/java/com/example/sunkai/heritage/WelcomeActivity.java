package com.example.sunkai.heritage;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.sunkai.heritage.Data.mySqlLite;

/**
 * 此页面是欢迎界面的类
 */
public class WelcomeActivity extends AppCompatActivity {
    private static int gotoLogin=0;
    public static mySqlLite myHelper;

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
        myHelper = new mySqlLite(this, "heritage.db", null, 1);
        welcomeHandler.sendEmptyMessageDelayed(gotoLogin,1000);
    }
    private Handler welcomeHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            /**
             * 判断用户是否登陆过，如果登陆，则跳过登陆
             */
            SQLiteDatabase db=myHelper.getReadableDatabase();
            String table="user_login_info";
            String[] columns=new String[]{"user_id","user_name","user_password"};
            String selection="id=?";
            String[] selectionArgs=new String[]{"1"};
            Cursor cursor=db.query(table,columns,selection,selectionArgs,null,null,null);
            int idIndex=cursor.getColumnIndex(columns[0]);
            int nameIndex=cursor.getColumnIndex(columns[1]);
            int passwordIndex=cursor.getColumnIndex(columns[2]);
            cursor.moveToFirst();
            if(!(cursor.isAfterLast())){
                LoginActivity.userID=cursor.getInt(idIndex);
                LoginActivity.userName=cursor.getString(nameIndex);
            }
            else{
                LoginActivity.userID=0;
            }
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
