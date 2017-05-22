package com.example.sunkai.heritage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandlePerson;
import com.example.sunkai.heritage.Data.focusData;

import java.util.List;

/**
 * 此类用于处理用户搜索的页面
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView search_activity_btn;
    private EditText search_activity_edit;
    private ListView search_activity_list;
    private ActionBar actionBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        search_activity_btn = (ImageView) findViewById(R.id.search_activity_btn);
        search_activity_edit = (EditText) findViewById(R.id.search_activity_edit);
        search_activity_list = (ListView) findViewById(R.id.search_activity_list);
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);
        search_activity_btn.setOnClickListener(this);
    }

    private void submit() {
        // validate
        String edit = search_activity_edit.getText().toString().trim();
        if (TextUtils.isEmpty(edit)) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 将搜索的文本传入搜索类，并搜索内容
         */
        searchClass searchClass=new searchClass(edit);
        searchClass.getSearchInfo();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.search_activity_btn:
                submit();
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

    class searchClass{
        List<focusData> datas;
        String searchText;
        focusListviewAdpter adpter;
        searchClass(String searchText){
            this.searchText=searchText;
        }
        void getSearchInfo(){
            new Thread(getSearchInfo).start();
        }
        Runnable getSearchInfo=new Runnable() {
            @Override
            public void run() {
                datas= HandlePerson.Get_Search_UserInfo(searchText);
                if(null==datas){
                    getSearchInfoHandler.sendEmptyMessage(0);
                }
                else{
                    getSearchInfoHandler.sendEmptyMessage(1);
                }
            }
        };
        Handler getSearchInfoHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==1){
                    adpter=new focusListviewAdpter(SearchActivity.this,datas,3);
                    search_activity_list.setAdapter(adpter);
                }
            }
        };
    }
}
