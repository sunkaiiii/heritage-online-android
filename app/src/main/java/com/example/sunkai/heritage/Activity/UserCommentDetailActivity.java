package com.example.sunkai.heritage.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleFind;
import com.example.sunkai.heritage.Data.FindActivityAllData;
import com.example.sunkai.heritage.Data.GlobalContext;
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.CommentReplyData;
import com.example.sunkai.heritage.Data.UserCommentData;
import com.example.sunkai.heritage.R;
import com.example.sunkai.heritage.tools.MakeToast;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import static com.example.sunkai.heritage.Activity.LoginActivity.userID;

/**
 * 此类用于处理用户发帖详细信息页面
 */
public class UserCommentDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int ADD_COMMENT = 1;
    public static final int DELETE_COMMENT=2;
    private boolean isReply = false;

    private ImageView information_img;
    private TextView information_title;
    private TextView information_time;
    private TextView information_content;
    private TextView information_username;
    private TextView reverse;
    private TextView information_reply_num;
    private LinearLayout linearLayout4;
    private LinearLayout LinearLayout_reply;
    private EditText replyEdit;
    private Button replyBtn;
    private ProgressBar progressBar;
    UserCommentData data;
    List<CommentReplyData> datas;
    private ActionBar actionBack;

    private boolean isReverse = false;

    private static final String TAG = "UserCommentDetail";

    /**
     * 记录传入进来的帖子在原帖的位置和ID
     */
    private int commentID, inListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comment_detail);
        initView();
        getData();

    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getSerializable("data") instanceof UserCommentData) {
            data = (UserCommentData) bundle.getSerializable("data");
            byte[] imageByte = getIntent().getByteArrayExtra("bitmap");
            Bitmap bitmap = HandlePic.handlePic(this, new ByteArrayInputStream(imageByte), 0);
            information_img.setImageBitmap(bitmap);
            commentID = data.getId();
            inListPosition = bundle.getInt("position");
            information_title.setText(data.getCommentTitle());
            information_time.setText(data.getCommentTime());
            information_content.setText(data.getCommentContent());
            information_reply_num.setText(data.getCommentReplyNum());
            setTitle(data.getUserName());
            new Thread(getReply).start();
        } else {
            int id = getIntent().getIntExtra("id", 0);
            Log.d(TAG, "onCreate: getID:" + id);
            if (id == 0)
                return;
            new getCommentInfo(id, this).execute();
        }
    }


    private void initView() {
        information_img = findViewById(R.id.information_img);
        information_title = findViewById(R.id.information_title);
        information_time = findViewById(R.id.information_time);
        information_content = findViewById(R.id.information_content);
        information_username = findViewById(R.id.information_username);
        information_reply_num = findViewById(R.id.information_reply_num);
        reverse = findViewById(R.id.user_comment_detail_reverse);
        linearLayout4 = findViewById(R.id.linearLayout4);
        LinearLayout_reply = findViewById(R.id.LinearLayout_reply);
        replyEdit = findViewById(R.id.reply_edittext);
        replyBtn = findViewById(R.id.reply_button);
        replyBtn.setOnClickListener(this);
        reverse.setOnClickListener(this);
        progressBar = findViewById(R.id.user_comment_detail_progressbar);
        actionBack = getSupportActionBar();
        if (actionBack != null) {
            actionBack.setDisplayHomeAsUpEnabled(true);
        }

    }


    //隐藏键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    //显示键盘
    private void showKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(view, 1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reply_button:
                submit();
                break;
            case R.id.user_comment_detail_reverse:
                changeList();
                break;
        }
    }

    private void submit() {
        if (TextUtils.isEmpty(replyEdit.getText().toString().trim())) {
            Toast.makeText(UserCommentDetailActivity.this, "回复不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userID == 0) {
            Toast.makeText(this, "没有登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("isInto", 1);
            startActivityForResult(intent, 1);
            return;
        }
        String content = replyEdit.getText().toString();
        replyBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        /*
         * 将回复的内容传给恢复类
         */
        HandleReply handleReply = new HandleReply(content);
        new Thread(handleReply.addReply).start();
    }

    private void changeList() {
        if (isReverse) {
            backupReverse();
            setTextViewBackup();
        } else {
            setReverse();
            setTextViewReverse();
        }
        isReverse = !isReverse;
    }

    private void setTextViewReverse() {
        reverse.setText(R.string.reverse_look);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reverse.setTextColor(getColor(R.color.colorPrimary));
        } else {
            reverse.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_arrow_upward_black_24dp), null);
        } else {
            reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_upward_black_24dp), null);
        }
    }

    private void setTextViewBackup() {
        reverse.setText(R.string.non_reverse_look);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reverse.setTextColor(getColor(R.color.black));
        } else {
            reverse.setTextColor(getResources().getColor(R.color.black));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_arrow_downward_black_24dp), null);
        } else {
            reverse.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_downward_black_24dp), null);
        }
    }

    private void setReverse() {
        LinearLayout_reply.removeAllViews();
        ListIterator<CommentReplyData> iterator = datas.listIterator(datas.size());
        hideKeyboard();
        for (; iterator.hasPrevious(); ) {
            CommentReplyData data = iterator.previous();
            setView(data);
        }
    }

    private void backupReverse() {
        LinearLayout_reply.removeAllViews();
        for (CommentReplyData data : datas) {
            setView(data);
        }
    }

    private void setView(CommentReplyData data) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.user_comment_reply_information, null);
        Holder vh = new Holder();
        vh.name = view.findViewById(R.id.reply_name);
        vh.time = view.findViewById(R.id.reply_time);
        vh.content = view.findViewById(R.id.reply_content);
        vh.name.setText(data.getUserName());
        vh.time.setText(data.getReplyTime());
        vh.content.setText(data.getReplyContent());
        LinearLayout_reply.addView(view);
    }

    private void deleteComment(){
        new AlertDialog.Builder(this).setTitle("是否删除帖子?").setPositiveButton("删除", (dialog, which) -> {
            @SuppressLint("InflateParams")
            final AlertDialog ad= new AlertDialog.Builder(this).setView(LayoutInflater.from(this).inflate(R.layout.progress_view,null)).create();
            ad.show();
            new Thread(()->{
                final boolean result = HandleFind.Delete_User_Comment_By_ID(data.getId());
                runOnUiThread(()->{
                    if(ad.isShowing()){
                        ad.dismiss();
                    }
                    if(result){
                        MakeToast.MakeText(getResources().getString(R.string.delete_success));
                    }else{
                        MakeToast.MakeText(getResources().getString(R.string.has_problem));
                    }
                    setResult(DELETE_COMMENT,getIntent());
                    finish();
                });
            }).start();
        }).setNegativeButton("取消",((dialog, which) ->{} )).create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.user_comment_detail_item_delete:
                deleteComment();
        }
        return super.onOptionsItemSelected(item);
    }


    Runnable getReply = new Runnable() {
        @Override
        public void run() {
            List<CommentReplyData> getdatas = HandleFind.Get_User_Comment_Reply(data.getId());
            if (getdatas!=null) {
                datas = getdatas;
                runOnUiThread(()->{
                    hideKeyboard();
                    for (int i = 0; i < datas.size(); i++) {
                        CommentReplyData data = datas.get(i);
                        setView(data);
                    }
                });
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (data != null && data.getUser_id() == userID) {
            getMenuInflater().inflate(R.menu.user_comment_detail_menu, menu);
        }
        return true;
    }

    static class getCommentInfo extends AsyncTask<Void, Void, FindActivityAllData> {
        private int id;
        private WeakReference<UserCommentDetailActivity> userCommentDetailWeakReference;

        getCommentInfo(int id, UserCommentDetailActivity userCommentDetail) {
            this.id = id;
            userCommentDetailWeakReference = new WeakReference<>(userCommentDetail);
        }

        @Override
        protected FindActivityAllData doInBackground(Void... voids) {

            return HandleFind.Get_All_User_Coment_Info_By_ID(LoginActivity.userID, id);
        }

        @Override
        protected void onPostExecute(FindActivityAllData findActivityAllData) {
            UserCommentDetailActivity userCommentDetail = userCommentDetailWeakReference.get();
            if (userCommentDetail == null || findActivityAllData == null)
                return;
            userCommentDetail.allDataSetView(findActivityAllData);
            new Thread(userCommentDetail.getReply).start();
        }
    }

    private void allDataSetView(FindActivityAllData data) {
        if(data.getImgCode()!=null) {
            byte[] imageByte = org.kobjects.base64.Base64.decode(data.getImgCode());
            Bitmap bitmap = HandlePic.handlePic(this, new ByteArrayInputStream(imageByte), 0);
            information_img.setImageBitmap(bitmap);
        }
        commentID = data.getId();
        information_title.setText(data.getComment_title());
        information_time.setText(data.getComent_time());
        information_content.setText(data.getComment_content());
        information_reply_num.setText(data.getReplyCount());
        setTitle(data.getUserName());
        //和老版本做一下兼容，复用代码
        this.data = new UserCommentData();
        this.data.setUserName(data.getUserName());
        this.data.setCommentReplyNum(data.getReplyCount());
        this.data.setId(data.getId());
        this.data.setCommentContent(data.getComment_content());
        this.data.setUser_id(data.getUserID());
        this.data.setCommentTime(data.getComent_time());
    }

    class HandleReply {
        int userID;
        String content, userName, replyTime;

        HandleReply(String content) {
            this.userID = LoginActivity.userID;
            this.content = content;
            this.userName = LoginActivity.userName;
        }

        Runnable addReply = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GlobalContext.Companion.getInstance(), UserCommentDetailActivity.class);
                intent.putExtra("id", commentID);
                String uriString = intent.toUri(Intent.URI_INTENT_SCHEME);
                Log.d(TAG, "uriString: " + uriString);
                final int result = HandleFind.Add_User_Comment_Reply(userID, commentID, content, uriString);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                replyTime = df.format(new Date());
                runOnUiThread(()->{
                    if(result>0) {
                        final CommentReplyData data=addDataToList(result);
                        setView(data);
                        resetWidge();
                        isReply = true;
                        MakeToast.MakeText("回复成功");
                        hideKeyboard();
                    }else{
                        MakeToast.MakeText("发生错误，请稍后再试");
                    }
                });
            }
        };
        private CommentReplyData addDataToList(int result){
            CommentReplyData data = new CommentReplyData();
            data.setReplyContent(content);
            data.setUserName(userName);
            data.setReplyTime(replyTime);
            data.setReplyId(result);
            datas.add(data);
            return data;
        }
        private void resetWidge(){
            progressBar.setVisibility(View.GONE);
            replyBtn.setVisibility(View.VISIBLE);
            information_reply_num.setText(String.valueOf(Integer.parseInt(information_reply_num.getText().toString()) + 1));
            replyEdit.setText("");
        }
    }

    class Holder {
        TextView name, time, content;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isReply) {
            Bundle bundle = new Bundle();
            bundle.putInt("commentID", commentID);
            bundle.putInt("position", inListPosition);
            Intent backIntent = new Intent();
            backIntent.putExtras(bundle);
            setResult(ADD_COMMENT, backIntent);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
