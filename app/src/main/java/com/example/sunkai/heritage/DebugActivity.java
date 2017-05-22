package com.example.sunkai.heritage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.BaseSetting;
import com.example.sunkai.heritage.ConnectWebService.HandleFolk;

/**
 * 工程调试页面
 */
public class DebugActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private EditText Debug_ID_Edittext;
    private Button Update_IP_Button;
    private Button Back_Button;
    private Button testButton;
    private TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        initView();
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.textView);
        Debug_ID_Edittext = (EditText) findViewById(R.id.Debug_ID_Edittext);
        Update_IP_Button = (Button) findViewById(R.id.Update_IP_Button);
        Back_Button = (Button) findViewById(R.id.Back_Button);
        textView3 = (TextView) findViewById(R.id.textView3);
        testButton=(Button)findViewById(R.id.test_function);

        Update_IP_Button.setOnClickListener(this);
        Back_Button.setOnClickListener(this);
        testButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Update_IP_Button:
                submit();
                break;
            case R.id.Back_Button:
                finish();
                break;
            case R.id.test_function:
                testFunction();
                break;
        }
    }

    private void submit() {
        // validate
        String Edittext = Debug_ID_Edittext.getText().toString().trim();
        if (TextUtils.isEmpty(Edittext)) {
            Toast.makeText(this, "Edittext不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something
        BaseSetting.Change_IP(Edittext);
        Toast.makeText(this, "修改IP完成", Toast.LENGTH_SHORT).show();
        finish();

    }
    private void testFunction(){
        Intent intent=new Intent(DebugActivity.this,AddFindCommentActivity.class);
        startActivity(intent);
    }

    Runnable testFunction=new Runnable() {
        @Override
        public void run() {
            HandleFolk.GetFolkImage(1);
        }
    };
}
