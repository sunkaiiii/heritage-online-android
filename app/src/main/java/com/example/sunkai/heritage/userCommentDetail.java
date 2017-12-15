package com.example.sunkai.heritage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import com.example.sunkai.heritage.Data.commentReplyData;
import com.example.sunkai.heritage.Data.userCommentData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 此类用于处理用户发帖详细信息页面
 */
public class userCommentDetail extends AppCompatActivity implements View.OnClickListener{
    public static final int ADD_COMMENT=1;
    private boolean isReply=false;

    private ImageView information_img;
    private TextView information_title;
    private TextView information_time;
    private TextView information_content;
    private TextView information_username;
    private TextView information_reply_num;
    private LinearLayout linearLayout4;
    private LinearLayout LinearLayout_reply;
    private EditText replyEdit;
    private Button replyBtn;
    private ProgressBar progressBar;
    userCommentData data;
    List<commentReplyData> datas;
    private ActionBar actionBack;


    /**
     * 记录传入进来的帖子在原帖的位置和ID
     */
    private int commentID,inListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comment_detail);
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle.getSerializable("data") instanceof userCommentData) {
            data = (userCommentData) bundle.getSerializable("data");
//            if(data.userCommentIamge!=null) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(data.userCommentIamge, 0, data.userCommentIamge.length);
//                information_img.setImageBitmap(bitmap);
//            }
            commentID=data.getId();
            inListPosition=data.getInListPosition();
            information_title.setText(data.getCommentTitle());
            information_time.setText(data.getCommentTime());
            information_content.setText(data.getCommentContent());
//            information_username.setText(data.);
            information_reply_num.setText(data.getCommentReplyNum());
            setTitle(data.getUserName());
            new Thread(getReply).start();
        }
    }

    private void initView() {
        information_img = (ImageView) findViewById(R.id.information_img);
        information_title = (TextView) findViewById(R.id.information_title);
        information_time = (TextView) findViewById(R.id.information_time);
        information_content = (TextView) findViewById(R.id.information_content);
        information_username = (TextView) findViewById(R.id.information_username);
        information_reply_num = (TextView) findViewById(R.id.information_reply_num);
        linearLayout4 = (LinearLayout) findViewById(R.id.linearLayout4);
        LinearLayout_reply = (LinearLayout) findViewById(R.id.LinearLayout_reply);
        replyEdit=(EditText)findViewById(R.id.reply_edittext);
        replyBtn=(Button)findViewById(R.id.reply_button);
        replyBtn.setOnClickListener(this);
        progressBar=(ProgressBar)findViewById(R.id.user_comment_detail_progressbar);
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);

    }
    //隐藏键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //显示键盘
    private void showKeyboard()
    {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    showSoftInput(view,1);
        }
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.reply_button:
                submit();

        }
    }

    private void submit(){
        if(TextUtils.isEmpty(replyEdit.getText().toString().trim())){
            Toast.makeText(userCommentDetail.this,"回复不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(LoginActivity.userID==0){
            Toast.makeText(this,"没有登录",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,LoginActivity.class);
            intent.putExtra("isInto",1);
            startActivityForResult(intent,1);
            return;
        }
        String content=replyEdit.getText().toString();
        replyBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        /**
         * 将回复的内容传给恢复类
         */
        HandleReply handleReply=new HandleReply(content);
        new Thread(handleReply.addReply).start();
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

    Runnable getReply=new Runnable() {
        @Override
        public void run() {
            List<commentReplyData> getdatas= HandleFind.Get_User_Comment_Reply(data.getId());
            if(null==getdatas){
                getReplyHandler.sendEmptyMessage(0);
            }
            else{
                datas=getdatas;
                getReplyHandler.sendEmptyMessage(1);
            }
        }
    };
    class HandleReply{
        int userID;
        String content,userName,replyTime;
        HandleReply(String content){
            this.userID=LoginActivity.userID;
            this.content=content;
            this.userName=LoginActivity.userName;
        }
        Runnable addReply=new Runnable() {
            @Override
            public void run() {
                int result=HandleFind.Add_User_Comment_Reply(userID,commentID,content);
                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                replyTime=df.format(new Date());
                addReplyHandler.sendEmptyMessage(result);
            }
        };
        Handler addReplyHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                progressBar.setVisibility(View.GONE);
                replyBtn.setVisibility(View.VISIBLE);
                if(msg.what>0){
                    /**
                     * 添加回复完成之后，在原来的回复页面新家一个LinearLayout
                     */
                    commentReplyData data=new commentReplyData();
                    data.setReplyContent(content);
                    data.setUserName(userName);
                    data.setReplyTime(replyTime);
                    data.setReplyId(msg.what);
                    LayoutInflater inflater=getLayoutInflater();
                    View view=inflater.inflate(R.layout.user_comment_reply_information,null);
                    Holder vh=new Holder();
                    vh.name=(TextView)view.findViewById(R.id.reply_name);
                    vh.time=(TextView)view.findViewById(R.id.reply_time);
                    vh.content=(TextView)view.findViewById(R.id.reply_content);
                    vh.name.setText(data.getUserName());
                    vh.time.setText(data.getReplyTime());
                    vh.content.setText(data.getReplyContent());
                    LinearLayout_reply.addView(view);
                    Toast.makeText(userCommentDetail.this,"回复成功",Toast.LENGTH_SHORT).show();
                    information_reply_num.setText(String.valueOf(Integer.parseInt(information_reply_num.getText().toString())+1));
                    replyEdit.setText("");
                    isReply=true;
                }
                else{
                    Toast.makeText(userCommentDetail.this,"发生错误，请稍后再试",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
    Handler getReplyHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                Holder vh;
//                Toast.makeText(userCommentDetail.this,"2321321",Toast.LENGTH_SHORT).show();
                /**
                 * 将获取的恢复信息通过循环加入到LinearLayout并通过addview传入到回复页面中
                 */
                for(int i=0;i<datas.size();i++){
                    hideKeyboard();
                    LayoutInflater inflater=getLayoutInflater();
                    View view=inflater.inflate(R.layout.user_comment_reply_information,null);
                    vh=new Holder();
                    vh.name=(TextView)view.findViewById(R.id.reply_name);
                    vh.time=(TextView)view.findViewById(R.id.reply_time);
                    vh.content=(TextView)view.findViewById(R.id.reply_content);
                    commentReplyData data=datas.get(i);
                    vh.name.setText(data.getUserName());
                    vh.time.setText(data.getReplyTime());
                    vh.content.setText(data.getReplyContent());
                    LinearLayout_reply.addView(view);
                }
            }
        }
    };
    class Holder{
        TextView name,time,content;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&isReply){
            Bundle bundle=new Bundle();
            bundle.putInt("commentID",commentID);
            bundle.putInt("position",inListPosition);
            Intent backIntent=new Intent();
            backIntent.putExtras(bundle);
            setResult(ADD_COMMENT,backIntent);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
