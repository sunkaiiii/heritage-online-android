package com.example.sunkai.heritage.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleUser;
import com.example.sunkai.heritage.R;

import java.util.HashMap;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener {

    private TextView regist_actitivy_username_textView;
    private EditText regist_actitivy_username_editText;
    private TextView regist_actitivy_password_textView;
    private EditText regist_actitivy_password_editText;
    private TextView regist_actitivy_insure_textView;
    private EditText regist_actitivy_insure_editText;
    private TextView regist_actitivy_question_textView;
    private EditText regist_actitivy_question_editText;
    private TextView regist_actitivy_answer_textView;
    private EditText regist_actitivy_answer_editText;
    private Button regist_activity_regist_button;
    private ProgressBar progressBar;
    private ActionBar actionBack;
    private HashMap<EditText,TextView> editTextToTextViewHashMap;

    String userName, userPassword, findPasswordQuestion, findPasswordAnswer;
    private Button regist_activity_cancel_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initView();
    }

    private void initView() {
        editTextToTextViewHashMap=new HashMap<>();
        regist_actitivy_username_textView = (TextView) findViewById(R.id.regist_actitivy_username_textView);
        regist_actitivy_username_editText = (EditText) findViewById(R.id.regist_actitivy_username_editText);
        regist_actitivy_username_editText.setOnFocusChangeListener(this);
        editTextToTextViewHashMap.put(regist_actitivy_username_editText,regist_actitivy_username_textView);
        regist_actitivy_password_textView = (TextView) findViewById(R.id.regist_actitivy_password_textView);
        regist_actitivy_password_editText = (EditText) findViewById(R.id.regist_actitivy_password_editText);
        editTextToTextViewHashMap.put(regist_actitivy_password_editText,regist_actitivy_password_textView);
        regist_actitivy_password_editText.setOnFocusChangeListener(this);
        regist_actitivy_insure_textView = (TextView) findViewById(R.id.regist_actitivy_insure_textView);
        regist_actitivy_insure_editText = (EditText) findViewById(R.id.regist_actitivy_insure_editText);
        editTextToTextViewHashMap.put(regist_actitivy_insure_editText,regist_actitivy_insure_textView);
        regist_actitivy_insure_editText.setOnFocusChangeListener(this);
        regist_actitivy_question_textView = (TextView) findViewById(R.id.regist_actitivy_question_textView);
        regist_actitivy_question_editText = (EditText) findViewById(R.id.regist_actitivy_question_editText);
        editTextToTextViewHashMap.put(regist_actitivy_question_editText,regist_actitivy_question_textView);
        regist_actitivy_question_editText.setOnFocusChangeListener(this);
        regist_actitivy_answer_textView = (TextView) findViewById(R.id.regist_actitivy_answer_textView);
        regist_actitivy_answer_editText = (EditText) findViewById(R.id.regist_actitivy_answer_editText);
        editTextToTextViewHashMap.put(regist_actitivy_answer_editText,regist_actitivy_answer_textView);
        regist_actitivy_answer_editText.setOnFocusChangeListener(this);
        regist_activity_regist_button = (Button) findViewById(R.id.activity_regist_regist_button);
        regist_activity_regist_button.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setOnClickListener(this);
        regist_activity_cancel_button = (Button) findViewById(R.id.regist_activity_cancel_button);
        regist_activity_cancel_button.setOnClickListener(this);
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_regist_regist_button:
                submit();
                break;
            case R.id.regist_activity_cancel_button:
                this.setResult(0);
                finish();
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.regist_actitivy_username_editText:
            case R.id.regist_actitivy_password_editText:
            case R.id.regist_actitivy_insure_editText:
            case R.id.regist_actitivy_question_editText:
            case R.id.regist_actitivy_answer_editText:
                changeTextviewStatus((EditText)v,hasFocus);
                break;
            default:
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

    private void changeTextviewStatus(EditText editText,Boolean hasFocus){
        TextView textView=editTextToTextViewHashMap.get(editText);
        if(textView==null)
            return;
        if(hasFocus)
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        else
            textView.setTextColor(getResources().getColor(R.color.deepGrey));
    }
    private void submit() {
        // validate
        String editText = regist_actitivy_username_editText.getText().toString().trim();
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_username_editText.setError("用户名不能为空");
            regist_actitivy_username_editText.requestFocus();
            return;
        }

        editText = regist_actitivy_password_editText.getText().toString().trim();
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_password_editText.setError("密码不能为空");
            regist_actitivy_password_editText.requestFocus();
            return;
        }

        editText = regist_actitivy_insure_editText.getText().toString().trim();
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_insure_editText.setError("确认密码不能为空");
            regist_actitivy_insure_editText.requestFocus();
            return;
        }

        editText = regist_actitivy_question_editText.getText().toString().trim();
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_question_editText.setError("密码召回问题不能为空");
            regist_actitivy_question_editText.requestFocus();
            return;
        }

        editText = regist_actitivy_answer_editText.getText().toString().trim();
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_answer_editText.setError("密码找回答案不能为空");
            regist_actitivy_answer_editText.requestFocus();
            return;
        }
        String password=regist_actitivy_password_editText.getText().toString();
        String insurePassword=regist_actitivy_insure_editText.getText().toString();
        if(!(password.equals(insurePassword))){
            Toast.makeText(this,"密码输入不一致",Toast.LENGTH_SHORT).show();
            regist_actitivy_insure_editText.requestFocus();
            return;
        }

        // TODO validate success, do something
        userName = regist_actitivy_username_editText.getText().toString().trim();
        userPassword = regist_actitivy_password_editText.getText().toString().trim();
        findPasswordQuestion = regist_actitivy_question_editText.getText().toString().trim();
        findPasswordAnswer = regist_actitivy_answer_editText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        regist_actitivy_username_editText.setEnabled(false);
        regist_actitivy_password_editText.setEnabled(false);
        regist_actitivy_insure_editText.setEnabled(false);
        regist_actitivy_question_editText.setEnabled(false);
        regist_actitivy_answer_editText.setEnabled(false);
        regist_activity_cancel_button.setVisibility(View.GONE);
        regist_activity_regist_button.setEnabled(false);
        new Thread(userRegist).start();
    }


    Runnable userRegist = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(800);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            int result= HandleUser.User_Regist(userName,userPassword,findPasswordQuestion,findPasswordAnswer);
            Message msg=new Message();
            msg.what=result;
            final Handler userRegistHandler=new Handler(getMainLooper()){
                @Override
                public void handleMessage(Message msg){
                    if(msg.what==1){
                        progressBar.setVisibility(View.VISIBLE);
                        regist_activity_cancel_button.setVisibility(View.VISIBLE);
                        Intent intent=new Intent();
                        intent.putExtra("userName",userName);
                        intent.putExtra("passWord",userPassword);
                        RegistActivity.this.setResult(1,intent);
                        Toast.makeText(RegistActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else if(msg.what==0){
                        Toast.makeText(RegistActivity.this,"已有该用户",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegistActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    regist_actitivy_username_editText.setEnabled(true);
                    regist_actitivy_password_editText.setEnabled(true);
                    regist_actitivy_insure_editText.setEnabled(true);
                    regist_actitivy_question_editText.setEnabled(true);
                    regist_actitivy_answer_editText.setEnabled(true);
                    regist_activity_cancel_button.setVisibility(View.VISIBLE);
                    regist_activity_regist_button.setEnabled(true);
                }
            };
            userRegistHandler.sendMessage(msg);
        }
    };


}
