package com.example.sunkai.heritage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunkai.heritage.Data.classifyActiviyData;

/**
 * Created by sunkai on 2017-4-22.
 * 此类是在首页点击官方活动的list之后跳转的界面的类，用于显示活动的详细信息
 */

public class activityInformationActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView join_activity_img;
    private TextView join_activity_title;
    private TextView join_activity_content;
    private Button join_activity_btn;
    Bitmap bitmap;
    private ActionBar actionBack;


    /**
     * 在首页载入的时候，活动信息已被载入，点击list内容之后，将点击的类传入bundle（类实现了serializable接口，可以序列化），传入之后读取出来并赋值在控件上
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        initView();
        classifyActiviyData data=(classifyActiviyData) getIntent().getSerializableExtra("activity");
        if(null!=data.getActivityImage()) {
            bitmap = BitmapFactory.decodeByteArray(data.getActivityImage(), 0, data.getActivityImage().length);
            join_activity_img.setImageBitmap(bitmap);
        }
        join_activity_title.setText(data.getActivityTitle());
        join_activity_content.setText(data.getActivityContent());
        actionBack.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        join_activity_img = (ImageView) findViewById(R.id.join_activity_img);
        join_activity_title = (TextView) findViewById(R.id.join_activity_title);
        join_activity_content = (TextView) findViewById(R.id.join_activity_content);
        join_activity_btn = (Button) findViewById(R.id.join_activity_btn);
        join_activity_btn.setVisibility(View.GONE);
        actionBack=getSupportActionBar();

        join_activity_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.join_activity_btn:

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
}
