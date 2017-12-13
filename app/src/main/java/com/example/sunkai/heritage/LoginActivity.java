package com.example.sunkai.heritage;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleUser;
import com.example.sunkai.heritage.Data.MySqliteHandler;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
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
        populateAutoComplete();

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
        mProgressView = findViewById(R.id.login_progress);
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
        jumpSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findPassword=(TextView)findViewById(R.id.activity_login_find_password);
        findPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                builder=new AlertDialog.Builder(LoginActivity.this).setTitle("忘记密码").setView(R.layout.find_password);
                ad=builder.create();
                ad.show();
                final EditText find_password_user=(EditText)ad.findViewById(R.id.find_password_username);
                final EditText find_password_question=(EditText)ad.findViewById(R.id.find_password_question);
                final EditText find_password_answer=(EditText)ad.findViewById(R.id.find_password_answer);
                final Button queding=(Button)ad.findViewById(R.id.find_password_queding);
                final Button cancel=(Button)ad.findViewById(R.id.find_password_cancel);
                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.dismiss();
                    }
                });
                queding.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(find_password_user.getText())){
                            Toast.makeText(LoginActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final String userName=find_password_user.getText().toString();
                        final android.os.Handler findPasswordHandler=new android.os.Handler(){
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
                        Runnable findPasswordQuestion=new Runnable() {
                            @Override
                            public void run() {
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
                            }
                        };
                        if(find_password_question.getVisibility()==View.GONE){
                            queding.setEnabled(false);
                            new Thread(findPasswordQuestion).start();
                        }
                        else{
                            final Handler checkUserAnswerHandler=new Handler(){
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
                            Runnable checkUserAnswer=new Runnable() {
                                @Override
                                public void run() {
                                    Boolean result=HandleUser.Check_Question_Answer(userName,find_password_answer.getText().toString());
                                    if(result){
                                        checkUserAnswerHandler.sendEmptyMessage(1);
                                    }
                                    else{
                                        checkUserAnswerHandler.sendEmptyMessage(0);
                                    }
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
                    }
                });
            }
        });
        buttonDebug=(Button)findViewById(R.id.activity_login_Debug);
        buttonDebug.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,DebugActivity.class);
                startActivity(intent);
            }
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
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });

        final Handler changePasswordHandler=new Handler(){
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

        final Runnable changePasswordThread=new Runnable() {
            @Override
            public void run() {
                boolean result=HandleUser.Change_Password(changePasswordUsername,password.getText().toString());
                if(result){
                    changePasswordHandler.sendEmptyMessage(1);
                }
                else {
                    changePasswordHandler.sendEmptyMessage(0);
                }
            }
        };

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
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
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            cancel = true;
        }

        if (cancel) {
            mEmailView.setError("用户名不能为空");
            mEmailView.requestFocus();
            // There was an error; don't attempt login and focus the first
            // form field with an error.
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(800);
                return HandleUser.Sign_in(mEmail,mPassword);
            } catch (InterruptedException e) {
                return false;
            }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }

            // TODO: register the new account here.
//            return true;
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
                SQLiteDatabase db=MySqliteHandler.INSTANCE.GetWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("user_id",userID);
                values.put("user_name",userName);
                values.put("user_password",mPasswordView.getText().toString());
                String[] whereString={"1"};
                db.update("user_login_info",values,"id=?",whereString);
                if(isIntoMainpage==0) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
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

