package com.example.sunkai.heritage.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.R;

/**
 * 废弃的预约页面的详细信息的类
 */
public class FolkInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button folk_infomation_send_btn;
    private EditText fork_information_edittext;
    private RelativeLayout fork_information_reltive;
    private ImageView list_img;
    private TextView list_location;
    private TextView list_title;
    private TextView list_text;
    private LinearLayout fork_information_linnerlayout;
    private ListView fork_information_listview;
    private RelativeLayout activity_folk_information;
    private ActionBar actionBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folk_information);
        initView();
        String title=getIntent().getStringExtra("title");
        String content=getIntent().getStringExtra("content");
        String location=getIntent().getStringExtra("location");
        int imgID=getIntent().getIntExtra("imgID",0);
        list_text.setText("        " + content);
        list_title.setText(title);
        list_location.setText(location);
        Bitmap bitmap= HandlePic.handlePic(this,imgID,1);
        list_img.setImageBitmap(bitmap);
    }

    private void initView() {
        folk_infomation_send_btn = (Button) findViewById(R.id.folk_infomation_send_btn);
        fork_information_edittext = (EditText) findViewById(R.id.fork_information_edittext);
        fork_information_reltive = (RelativeLayout) findViewById(R.id.fork_information_reltive);
        list_img = (ImageView) findViewById(R.id.fork_information_list_img);
        list_location = (TextView) findViewById(R.id.fork_information_list_location);
        list_title = (TextView) findViewById(R.id.fork_information_list_title);
        list_text = (TextView) findViewById(R.id.fork_information_list_text);
        fork_information_linnerlayout = (LinearLayout) findViewById(R.id.fork_information_linnerlayout);
        fork_information_listview = (ListView) findViewById(R.id.fork_information_listview);
        activity_folk_information = (RelativeLayout) findViewById(R.id.activity_folk_information);

        folk_infomation_send_btn.setOnClickListener(this);
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.folk_infomation_send_btn:
                submit();
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

    private void submit() {
        // validate
        String edittext = fork_information_edittext.getText().toString().trim();
        if (TextUtils.isEmpty(edittext)) {
            Toast.makeText(this, "edittext不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}
