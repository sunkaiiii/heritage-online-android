package com.example.sunkai.heritage.Activity;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleUser;
import com.example.sunkai.heritage.Data.GlobalContext;
import com.example.sunkai.heritage.Data.MySqliteHandler;
import com.example.sunkai.heritage.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 0;
    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button registButton,mEmailSignInButton;
    private TextView jumpSignIn,findPassword;
    private int requestCode;
    private Button buttonDebug;

    /**
     * 登陆之后获取用户的ID
     */
    public static int userID;

    private String changePasswordUsername;

    private AlertDialog.Builder builder;
    private AlertDialog ad;
    public static String userName;

    int isIntoMainpage=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        resetLoginSql();
        isIntoMainpage=getIntent().getIntExtra("isInto",0);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        registButton=(Button)findViewById(R.id.activity_login_regist);
        registButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegistActivity.class);
                startActivityForResult(intent,requestCode);
            }
        });

        jumpSignIn=(TextView)findViewById(R.id.activity_login_jump_login);
        if(isIntoMainpage==0){
            jumpSignIn.setVisibility(View.VISIBLE);
        }
        else{
            jumpSignIn.setVisibility(View.GONE);
        }
        jumpSignIn.setOnClickListener(v -> {
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        });
        findPassword=(TextView)findViewById(R.id.activity_login_find_password);
        findPassword.setOnClickListener(v -> {
            builder=new AlertDialog.Builder(LoginActivity.this).setTitle("忘记密码").setView(R.layout.find_password);
            ad=builder.create();
            ad.show();
            final EditText find_password_user=(EditText)ad.findViewById(R.id.find_password_username);
            final EditText find_password_question=(EditText)ad.findViewById(R.id.find_password_question);
            final EditText find_password_answer=(EditText)ad.findViewById(R.id.find_password_answer);
            final Button queding=(Button)ad.findViewById(R.id.find_password_queding);
            final Button cancel=(Button)ad.findViewById(R.id.find_password_cancel);
            cancel.setOnClickListener(v1 -> ad.dismiss());
            queding.setOnClickListener(v12 -> {
                if(TextUtils.isEmpty(find_password_user.getText())){
                    Toast.makeText(LoginActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                final String userName=find_password_user.getText().toString();
                final Handler findPasswordHandler=new Handler(getMainLooper()){
                    @Override
                    public void handleMessage(Message msg){
                        queding.setEnabled(true);
                        if(msg.what==-1){
                            Toast.makeText(LoginActivity.this,"发生错误",Toast.LENGTH_SHORT).show();
                        }
                        else if(msg.what==0){
                            Toast.makeText(LoginActivity.this,"没有该用户",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            find_password_question.setVisibility(View.VISIBLE);
                            find_password_answer.setVisibility(View.VISIBLE);
                            String result=msg.getData().getString("userName");
                            find_password_question.setText(result);
                            find_password_user.setEnabled(false);
                        }
                    }
                };
                Runnable findPasswordQuestion= () -> {
                    String result;
                    result=HandleUser.Find_Password_Question(userName);
                    if(null==result){
                        findPasswordHandler.sendEmptyMessage(-1);
                    }
                    else if("noUser".equals(result)){
                        findPasswordHandler.sendEmptyMessage(0);
                    }
                    else{
                        Message msg=new Message();
                        Bundle bundle=new Bundle();
                        msg.what=1;
                        bundle.putString("userName",result);
                        msg.setData(bundle);
                        findPasswordHandler.sendMessage(msg);
                    }
                };
                if(find_password_question.getVisibility()==View.GONE){
                    queding.setEnabled(false);
                    new Thread(findPasswordQuestion).start();
                }
                else{
                    final Handler checkUserAnswerHandler=new Handler(getMainLooper()){
                        @Override
                        public void handleMessage(Message msg){
                            queding.setEnabled(true);
                            if(msg.what==1){
//                                        Toast.makeText(LoginActivity.this,"成功",Toast.LENGTH_SHORT).show();
                                changePasswordUsername=userName;
                                ad.dismiss();
                                changePassword();
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"回答错误",Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    Runnable checkUserAnswer= () -> {
                        Boolean result=HandleUser.Check_Question_Answer(userName,find_password_answer.getText().toString());
                        if(result){
                            checkUserAnswerHandler.sendEmptyMessage(1);
                        }
                        else{
                            checkUserAnswerHandler.sendEmptyMessage(0);
                        }
                    };
                    if(TextUtils.isEmpty(find_password_answer.getText())){
                        Toast.makeText(LoginActivity.this,"答案不能为空",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        queding.setEnabled(false);
                        new Thread(checkUserAnswer).start();
                    }
                }
            });
        });
        buttonDebug=(Button)findViewById(R.id.activity_login_Debug);
        buttonDebug.setOnClickListener(v -> {
            Intent intent=new Intent(LoginActivity.this,DebugActivity.class);
            startActivity(intent);
        });
    }

    private void resetLoginSql(){
        SQLiteDatabase db= MySqliteHandler.INSTANCE.GetWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("user_id",0);
        values.put("user_name","0");
        values.put("user_password","0");
        String[] whereString={"1"};
        db.update("user_login_info",values,"id=?",whereString);
    }
    private void changePassword(){
        builder=new AlertDialog.Builder(LoginActivity.this).setTitle("修改密码").setView(R.layout.change_password);
        ad=builder.create();
        ad.show();
        final EditText userName,password,insure;
        final Button submit,cancel;
        userName=(EditText)ad.findViewById(R.id.change_password_name);
        password=(EditText)ad.findViewById(R.id.change_password_password);
        insure=(EditText)ad.findViewById(R.id.change_password_insure);
        submit=(Button) ad.findViewById(R.id.change_password_queding);
        cancel=(Button)ad.findViewById(R.id.change_password_cancel);
        userName.setText(changePasswordUsername);
        cancel.setOnClickListener(v -> ad.dismiss());

        final Handler changePasswordHandler=new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==1){
                    Toast.makeText(LoginActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    ad.dismiss();
                }
                else{
                    Toast.makeText(LoginActivity.this,"修改失败，请稍后再试",Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                }
            }
        };

        final Runnable changePasswordThread= () -> {
            boolean result=HandleUser.Change_Password(changePasswordUsername,password.getText().toString());
            if(result){
                changePasswordHandler.sendEmptyMessage(1);
            }
            else {
                changePasswordHandler.sendEmptyMessage(0);
            }
        };

        submit.setOnClickListener(v -> {
            if(TextUtils.isEmpty(password.getText())||TextUtils.isEmpty(insure.getText())){
                Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!password.getText().toString().equals(insure.getText().toString())){
                Toast.makeText(LoginActivity.this,"密码输入不一致",Toast.LENGTH_SHORT).show();
                return;
            }
            submit.setEnabled(false);
            new Thread(changePasswordThread).start();
        });
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        if (TextUtils.isEmpty(email)) {
            cancel = true;
        }

        if (cancel) {
            mEmailView.setError("用户名不能为空");
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if(show){
            mEmailSignInButton.setText("登陆中...");
        }
        else{
            mEmailSignInButton.setText("登陆");
        }
        mEmailSignInButton.setEnabled(!show);
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return HandleUser.Sign_in(mEmail,mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                userName = mEmail.toString();
                new Thread(getUserID).start();
            } else {
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    Runnable getUserID=new Runnable() {
        @Override
        public void run() {
            userID=HandleUser.Get_User_ID(userName);
            if(userID>0){
                getUserIDHandler.sendEmptyMessage(1);
            }
            else{
                getUserIDHandler.sendEmptyMessage(0);
            }
        }
    };
    Handler getUserIDHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                final SharedPreferences.Editor editor=getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                editor.putInt("user_id",userID);
                editor.putString("user_name",userName);
                editor.putString("user_password",mPasswordView.getText().toString());
                editor.commit();
                GlobalContext.Companion.getInstance().registUser();
                //从Welcome页过来而并非从二级页面登录，则直接进入主页
                if(isIntoMainpage==0) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                //从首页跳过登录在二级页面登陆后，通知所有页面信息刷新
                Intent intent=new Intent("andrid.intent.action.refreshList");
                sendBroadcast(intent);
                intent=new Intent("android.intent.action.refreInfomation");
                sendBroadcast(intent);
                setResult(1,getIntent());
                finish();
            }
            else{
                Toast.makeText(LoginActivity.this,"出现错误",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                showProgress(true);
                String loginName=data.getStringExtra("userName");
                String loginPassword=data.getStringExtra("passWord");
                mAuthTask = new UserLoginTask(loginName, loginPassword);
                mAuthTask.execute((Void) null);
                break;
            default:
                break;
        }
    }
}

