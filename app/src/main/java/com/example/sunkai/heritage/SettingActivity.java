package com.example.sunkai.heritage;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleUser;
import com.xiaomi.mipush.sdk.MiPushClient;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    AlertDialog.Builder builder;
    AlertDialog ad;
    private ImageView sign_in_icon;
    private TextView sign_name_textview;
    private LinearLayout linearLayout2;
    private ImageView setting_sigh_out_img;
    private TextView setting_sigh_out_text;
    private ImageView setting_changepassword_img;
    private TextView setting_changepassword_text;
    private ImageView setting_about_us_img;
    private TextView setting_about_us_text;
    private ImageView setting_connent_us_img;
    private TextView setting_connect_us_text;
    private ActionBar actionBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        sign_in_icon = (ImageView) findViewById(R.id.sign_in_icon);
        sign_name_textview = (TextView) findViewById(R.id.sign_name_textview);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        setting_sigh_out_img = (ImageView) findViewById(R.id.setting_sigh_out_img);
        setting_sigh_out_text = (TextView) findViewById(R.id.setting_sigh_out_text);
        setting_changepassword_img = (ImageView) findViewById(R.id.setting_changepassword_img);
        setting_changepassword_text = (TextView) findViewById(R.id.setting_changepassword_text);
        setting_about_us_img = (ImageView) findViewById(R.id.setting_about_us_img);
        setting_about_us_text = (TextView) findViewById(R.id.setting_about_us_text);
        setting_connent_us_img = (ImageView) findViewById(R.id.setting_connent_us_img);
        setting_connect_us_text = (TextView) findViewById(R.id.setting_connect_us_text);
        setting_sigh_out_text.setOnClickListener(this);
        setting_sigh_out_img.setOnClickListener(this);
        setting_changepassword_img.setOnClickListener(this);
        setting_changepassword_text.setOnClickListener(this);
        setting_about_us_img.setOnClickListener(this);
        setting_about_us_text.setOnClickListener(this);
        setting_connect_us_text.setOnClickListener(this);
        setting_connent_us_img.setOnClickListener(this);
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);
        sign_name_textview.setText(LoginActivity.userName);
        Bitmap bitmap=getIntent().getParcelableExtra("userImage");
        sign_in_icon.setImageBitmap(bitmap);
    }


    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.setting_sigh_out_img:
            case R.id.setting_sigh_out_text:
                sign_out();
                break;
            case R.id.setting_changepassword_img:
            case R.id.setting_changepassword_text:
                if(LoginActivity.userID==0){
                    Toast.makeText(this,"没有登录",Toast.LENGTH_SHORT).show();
                    intent=new Intent(this,LoginActivity.class);
                    intent.putExtra("isInto",1);
                    startActivityForResult(intent,1);
                    return;
                }
                changePassword();
                break;
            case R.id.setting_about_us_img:
            case R.id.setting_about_us_text:
                intent=new Intent(this,AboutUSActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_connect_us_text:
            case R.id.setting_connent_us_img:
                intent=new Intent(this,ConnectUsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {
        builder = new AlertDialog.Builder(SettingActivity.this).setTitle("修改密码").setView(R.layout.change_password);
        ad = builder.create();
        ad.show();
        final EditText userName, password, insure;
        final Button submit, cancel;
        userName = (EditText) ad.findViewById(R.id.change_password_name);
        password = (EditText) ad.findViewById(R.id.change_password_password);
        insure = (EditText) ad.findViewById(R.id.change_password_insure);
        submit = (Button) ad.findViewById(R.id.change_password_queding);
        cancel = (Button) ad.findViewById(R.id.change_password_cancel);
        userName.setText(LoginActivity.userName);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });

        final Handler changePasswordHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Toast.makeText(SettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    ad.dismiss();
                } else {
                    Toast.makeText(SettingActivity.this, "修改失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                }
            }
        };

        final Runnable changePasswordThread = new Runnable() {
            @Override
            public void run() {
                boolean result = HandleUser.Change_Password(LoginActivity.userName, password.getText().toString());
                if (result) {
                    changePasswordHandler.sendEmptyMessage(1);
                } else {
                    changePasswordHandler.sendEmptyMessage(0);
                }
            }
        };

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(insure.getText())) {
                    Toast.makeText(SettingActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.getText().toString().equals(insure.getText().toString())) {
                    Toast.makeText(SettingActivity.this, "密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                submit.setEnabled(false);
                new Thread(changePasswordThread).start();
            }
        });
    }

    private void sign_out(){
        new AlertDialog.Builder(SettingActivity.this).setTitle("是否注销?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent("android.intent.action.focusAndFansCountChange");
                intent.putExtra("message","sigh_out");
                sendBroadcast(intent);
                MiPushClient.unsetUserAccount(getApplicationContext(),LoginActivity.userName,null); //注销的时候退出当前账号
                LoginActivity.userID=0;
                LoginActivity.userName=null;
                finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
