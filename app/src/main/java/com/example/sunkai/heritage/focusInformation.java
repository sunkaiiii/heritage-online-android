package com.example.sunkai.heritage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.sunkai.heritage.ConnectWebService.HandlePerson;
import com.example.sunkai.heritage.Data.focusData;

import org.kobjects.base64.Base64;

import java.util.List;

public class focusInformation extends AppCompatActivity implements View.OnClickListener {

    private ListView focus_information_listview;
    private ActionBar backAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_information);
        initView();
        GetInformationToListView();
    }

    private void initView() {
        focus_information_listview = (ListView) findViewById(R.id.focus_information_listview);
        backAction=getSupportActionBar();
        backAction.setDisplayHomeAsUpEnabled(true);
    }
    private void GetInformationToListView(){
        handleList list;
        /**
         * 判断是点击我的关注还是从我的粉丝进来的，从而执行不同的方法
         */
        switch (getIntent().getStringExtra("information")){
            case "focus":
                list=new handleList(1);
                list.getFollowInformaiton();
                break;
            case "fans":
                list=new handleList(2);
                list.getFansInformation();
                break;
            default:
                break;
        }

    }
    @Override
    public void onClick(View v){
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

    class handleList{
        List<focusData> datas;
        private focusListviewAdpter adpter;
        int what;

        /**
         *
         * @param what 为1的时候说明是关注界面，为2的时候为粉丝界面
         */
        handleList(int what){
            this.what=what;
        }
        void setData(List<focusData> datas){
            this.datas=datas;
        }
        void setAdpter(){
            adpter=new focusListviewAdpter(focusInformation.this,datas,what);
            focus_information_listview.setAdapter(adpter);
        }

        /**
         * 根据what的不同，执行不同的方法
         */
        void getFollowInformaiton(){
            new Thread(getFollowinformation).start();
        }
        void getFansInformation(){
            new Thread(getFansinformation).start();
        }
        private Runnable getFollowinformation=new Runnable() {
            @Override
            public void run() {
                datas= HandlePerson.Get_Follow_Information(LoginActivity.userID);
                if(null==datas){
                    getinformationHandler.sendEmptyMessage(0);
                }
                else {
                    getinformationHandler.sendEmptyMessage(1);
                }
            }
        };
        private Runnable getFansinformation=new Runnable() {
            @Override
            public void run() {
                datas=HandlePerson.Get_Fans_Information(LoginActivity.userID);
                if(null==datas){
                    getinformationHandler.sendEmptyMessage(0);
                }
                else{
                    getinformationHandler.sendEmptyMessage(1);
                }
            }
        };
        private Handler getinformationHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==1){
                    setAdpter();
                    new Thread(getUsersImage).start();
                }
            }
        };
        private Runnable getUsersImage=new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<adpter.datas.size();i++){
                    String result;
                    if(adpter.what==1) {
                        result = HandlePerson.Get_User_Image(adpter.datas.get(i).focusFansID);
                    }
                    else{
                        result=HandlePerson.Get_User_Image(adpter.datas.get(i).focusUserid);
                    }
                    if("Error".equals(result)||null==result){
                        getUserImageHandler.sendEmptyMessage(0);
                    }
                    else {
                        adpter.datas.get(i).userImage= Base64.decode(result);
                        getUserImageHandler.sendEmptyMessage(1);
                    }
                }
            }
        };

        private Handler getUserImageHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    adpter.notifyDataSetChanged();
                }
            }
        };

    }
}
