package com.example.sunkai.heritage;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 此类用于处理登陆
 */

public class MainActivity extends AppCompatActivity implements OnClickListener{
    /**
     * 用于展示消息的Fragment
     */
    private MainFragment mainFragment;

    /**
     * 用于展示联系人的Fragment
     */
    private folkFragment folkFragment;

    /**
     * 用于展示动态的Fragment
     */
    private findFragment findFragment;

    /**
     * 用于展示设置的Fragment
     */
    private personFragment personFragment;

    /**
     * 消息界面布局
     */
    private View mainLayout;

    /**
     * 联系人界面布局
     */
    private View folkLayout;

    /**
     * 动态界面布局
     */
    private View findLayout;

    /**
     * 设置界面布局
     */
    private View personLayout;

    /**
     * 在Tab布局上显示消息图标的控件
     */
    private ImageView mainImage;

    /**
     * 在Tab布局上显示联系人图标的控件
     */
    private ImageView folkImage;

    /**
     * 在Tab布局上显示动态图标的控件
     */
    private ImageView findImage;

    /**
     * 在Tab布局上显示设置图标的控件
     */
    private ImageView personImage;

    /**
     * 在Tab布局上显示消息标题的控件
     */
    private TextView mainText;

    /**
     * 在Tab布局上显示联系人标题的控件
     */
    private TextView folkText;

    /**
     * 在Tab布局上显示动态标题的控件
     */
    private TextView findText;

    /**
     * 在Tab布局上显示设置标题的控件
     */
    private TextView personText;

    /**
     * 用于对Fragment进行管理
     */
    private android.support.v4.app.FragmentManager fragmentManager;


    AlertDialog ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        fragmentManager=getSupportFragmentManager();
        setTabSelection(0);
//0        ActionBar actionBarr = getActionBar();
//        actionBar.hide();
    }

    private void initViews() {
        mainLayout = findViewById(R.id.main_layout);
        folkLayout = findViewById(R.id.folk_layout);
        findLayout = findViewById(R.id.find_layout);
        personLayout = findViewById(R.id.person_layout);
        mainImage = (ImageView) findViewById(R.id.main_image);
        folkImage = (ImageView) findViewById(R.id.folk_image);
        findImage = (ImageView) findViewById(R.id.find_image);
        personImage = (ImageView) findViewById(R.id.person_image);
        mainText = (TextView) findViewById(R.id.main_text);
        folkText = (TextView) findViewById(R.id.folk_text);
        findText = (TextView) findViewById(R.id.find_text);
        personText = (TextView) findViewById(R.id.person_text);
        mainLayout.setOnClickListener(this);
        folkLayout.setOnClickListener(this);
        findLayout.setOnClickListener(this);
        personLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_layout:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.folk_layout:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.find_layout:
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2);
                break;
            case R.id.person_layout:
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(3);
                break;
            default:
                break;
        }
    }

    private void setTabSelection(int index) {
        clearSelection();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        hideFragments(transaction);

        switch (index) {
            case 0:
                mainImage.setImageResource(R.drawable.ic_home_brown_500_24dp);
                mainText.setTextColor(Color.BLACK);
                if (null == mainFragment) {
                    mainFragment = new MainFragment();
                    transaction.add(R.id.content, mainFragment);
                } else {
                    transaction.show(mainFragment);
                }
                break;
            case 1:
                folkImage.setImageResource(R.drawable.ic_people_brown_500_24dp);
                folkText.setTextColor(Color.BLACK);
                if (null == folkFragment) {
                    folkFragment = new folkFragment();
                    transaction.add(R.id.content, folkFragment);
                } else {
                    transaction.show(folkFragment);
                }
                break;
            case 2:
                findImage.setImageResource(R.drawable.ic_search_brown_500_24dp);
                findText.setTextColor(Color.BLACK);
                if (null == findFragment) {
                    findFragment = new findFragment();
                    transaction.add(R.id.content, findFragment);
                } else {
                    transaction.show(findFragment);
                }
                break;
            case 3:
            default:
                personImage.setImageResource(R.drawable.ic_assignment_ind_brown_500_24dp);
                personText.setTextColor(Color.BLACK);
                if (null == personFragment) {
                    personFragment = new personFragment();
                    transaction.add(R.id.content, personFragment);
                } else {
                    transaction.show(personFragment);
                }
                break;
        }
        transaction.commit();
    }

    private void clearSelection(){
        mainImage.setImageResource(R.drawable.ic_home_grey_500_24dp);
        mainText.setTextColor(Color.parseColor("#82858b"));
        folkImage.setImageResource(R.drawable.ic_people_grey_500_24dp);
        folkText.setTextColor(Color.parseColor("#82858b"));
        findImage.setImageResource(R.drawable.ic_search_grey_500_24dp);
        findText.setTextColor(Color.parseColor("#82858b"));
        personImage.setImageResource(R.drawable.ic_assignment_ind_grey_500_24dp);
        personText.setTextColor(Color.parseColor("#82858b"));
    }

    private void hideFragments(FragmentTransaction transaction){
        if(null!=mainFragment){
            transaction.hide(mainFragment);
        }
        if(null!=folkFragment){
            transaction.hide(folkFragment);
        }
        if(null!=findFragment){
            transaction.hide(findFragment);
        }
        if(null!=personFragment){
            transaction.hide(personFragment);
        }
    }


    //重写onKeyDown方法，监听返回键
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setTitle("退出?").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                    }
                });
                ad = builder.create();
                ad.show();
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WelcomeActivity.myHelper.close();
    }
}
