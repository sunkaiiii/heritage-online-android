package com.example.sunkai.heritage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleFind;
import com.example.sunkai.heritage.Data.FindActivityData;
import com.example.sunkai.heritage.Data.HandlePic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 发现页面的类
 */

public class findFragment extends Fragment implements ViewPager.OnPageChangeListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewPager viewPager;
    private ImageView[] tips;
    private ImageView[] mImageViews;
    private int[] imgIdArray;
    private View view;
    private OnFragmentInteractionListener mListener;
    private findFragmentAdapter adapter;
    private ListView listView;
    private FloatingActionButton refreshBtn;
    private Spinner selectSpiner;
    private Button addCommentBtn;
    List<FindActivityData> activityDatas;
    Animation btnAnimation;
    private Bitmap[] bitmaps; //装载首页轮转窗的Bitmap[]
    int count=0;
    int count2=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_find, container, false);
        selectSpiner=(Spinner)view.findViewById(R.id.find_select_spinner);
        setHasOptionsMenu(true);
        bitmaps=new Bitmap[4];
        //主页活动页面
        imgIdArray=new int[]{R.mipmap.img1,R.mipmap.img1,R.mipmap.img1,R.mipmap.img1};
        viewPager=(ViewPager)view.findViewById(R.id.find_fragment_viewPager);
        loadMyPage();
        //设置Adapter
        viewPager.setAdapter(new MyAdapter());
        //设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(this);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.setCurrentItem((mImageViews.length) * 100);;
        listView=(ListView)view.findViewById(R.id.fragment_find_listview);
        activityDatas=new ArrayList<>();
        for(int i=0;i<4;i++){
            activityDatas.add(new FindActivityData());
        }
        loadMyPage();
        viewPager.setAdapter(new MyAdapter());
        new Thread(getActivityID).start();
//测试用图片
//        int[] imgID={R.mipmap.find1,R.mipmap.find2,R.mipmap.find3,R.mipmap.find4,R.mipmap.find5,R.mipmap.find9,R.mipmap.find7,R.mipmap.find8};
//        List<findData> datas=new ArrayList<>();
//        for(int i=0;i<imgID.length;i++){
//            findData data=new findData();
//            data.imgID=imgID[i];
//            datas.add(data);
//        }

        /**
         * 生命两个广播，一个用于在点击list进入下一页面之后，用户回帖，发现页重新加载回复数（adpterGetReplyCount)
         * 另一个用于点击刷新按钮，刷新按钮动画运行，在adpter刷新内容之后，发送广播让动画停止运行（animationStop）
         */
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.adpterGetReplyCount");
        getActivity().registerReceiver(getReplyCountReceiver,intentFilter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.animationStop");
        getActivity().registerReceiver(animationStopReceiver,intentFilter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("andrid.intent.action.refreshList");
        getActivity().registerReceiver(refreshList,intentFilter);
        /**
         * 程序默认显示广场的全部帖子
         */
        adapter=new findFragmentAdapter(getActivity(),1);
        listView.setAdapter(adapter);
        refreshBtn=(FloatingActionButton)view.findViewById(R.id.fragment_find_refreshbtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAnimation= AnimationUtils.loadAnimation(getContext(),R.anim.refresh_button_rotate);
                refreshBtn.startAnimation(btnAnimation);
                refreshBtn.setEnabled(false);
                adapter.reFreshList();
            }
        });

        /**
         * Spinear切换，重新加载adpater的数据
         */
        selectSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        adapter=new findFragmentAdapter(getActivity(),1);
                        listView.setAdapter(adapter);
                        break;
                    case 1:
                        if(LoginActivity.userID==0){
                            Toast.makeText(getActivity(),"没有登录",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getActivity(),LoginActivity.class);
                            intent.putExtra("isInto",1);
                            startActivityForResult(intent,1);
                            selectSpiner.setSelection(0);
                            return;
                        }
                        adapter=new findFragmentAdapter(getActivity(),2);
                        listView.setAdapter(adapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 发帖
         */
        addCommentBtn=(Button)view.findViewById(R.id.btn_add_comment);
        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginActivity.userID==0){
                    Toast.makeText(getActivity(),"没有登录",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isInto",1);
                    startActivityForResult(intent,1);
                    return;
                }
                Intent intent=new Intent(getActivity(),AddFindCommentActivity.class);
                /**
                 * 当成功添加帖子的时候，页面刷新
                 */
                startActivityForResult(intent,1);
            }
        });
        return view;
    }

    /**
     * 加载首页轮转窗
     */
    public void loadMyPage() {
        ViewGroup group = (ViewGroup) view.findViewById(R.id.find_fragment_imageView);
        tips = new ImageView[imgIdArray.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            if (count >= tips.length) {
                group.addView(imageView, layoutParams);
                count2++;
            }
        }
        mImageViews = new ImageView[imgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            mImageViews[i] = imageView;
            if (count < mImageViews.length) {
                bitmaps[i] = HandlePic.handlePic(getActivity(), imgIdArray[i], 0);
                count++;
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(bitmaps[i]);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent=new Intent(getActivity(),RegistActivity.class);
//                    startActivity(intent);
                }
            });
        }
    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0);
            return mImageViews[position % mImageViews.length];
        }
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        setImageBackground(arg0 % mImageViews.length);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private void setImageBackground(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i == selectItems){
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().unregisterReceiver(getReplyCountReceiver);
        getActivity().unregisterReceiver(animationStopReceiver);
        getActivity().unregisterReceiver(refreshList);
    }

    @Override
    public void onResume(){
        super.onResume();
//        adapter.reFreshList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.find_fragment_menu, menu);
        setIconVisible(menu,true);
        super.onCreateOptionsMenu(menu,inflater);
    }
    public void setIconVisible(Menu menu, boolean visable){
        Field field;
        try {
            field = menu.getClass().getDeclaredField("mOptionalIconsVisible");

            Log.d("TAG"," setIconVisible1() field="+field);
            field.setAccessible(true);
            field.set(menu, visable);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_search_user:
                if(LoginActivity.userID==0){
                    Toast.makeText(getActivity(),"没有登录",Toast.LENGTH_SHORT).show();
                    intent=new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isInto",1);
                    startActivityForResult(intent,1);
                    break;
                }
                intent=new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * 首先获取轮转窗的id
     */
    Runnable getActivityID=new Runnable() {
        @Override
        public void run() {
            activityDatas= HandleFind.Get_Find_Activity_ID(activityDatas);
            getActivityIDHandler.sendEmptyMessage(1);
        }
    };
    Handler getActivityIDHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                new Thread(getAcitivtyInformaiton).start();
            }
        }
    };

    /**
     * 获取轮转窗的信息
     */
    Runnable getAcitivtyInformaiton=new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<activityDatas.size();i++){
                FindActivityData data=HandleFind.Get_Find_Activity_Information(activityDatas.get(i));
                activityDatas.get(i).title=data.title;
                activityDatas.get(i).content=data.content;
                activityDatas.get(i).image=data.image;
                if(data.image!=null) {
                    InputStream in = new ByteArrayInputStream(data.image);
                    bitmaps[i]=HandlePic.handlePic(getActivity(),in,0);
                }
                getActivityInformaitonHandler.sendEmptyMessage(i);
            }
        }
    };

    Handler getActivityInformaitonHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            /**
             * msg.what传递过来对应位置的bitmap加载完成，对对应位置的imageview时期加载图片，并提醒viewpager刷新
             */
            mImageViews[msg.what].setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageViews[msg.what].setImageBitmap(bitmaps[msg.what]);
            viewPager.getAdapter().notifyDataSetChanged();
        }
    };

    BroadcastReceiver getReplyCountReceiver=new BroadcastReceiver() {
        int commentID;
        int position;
        @Override
        public void onReceive(Context context, Intent intent) {
            commentID=intent.getIntExtra("commentID",0);
            position=intent.getIntExtra("position",0);
            if(commentID==0||position==0){
                return;
            }
            adapter.getReplyCount(commentID,position);
        }
    };
    BroadcastReceiver animationStopReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(),"刷新成功",Toast.LENGTH_SHORT).show();
            refreshBtn.clearAnimation();
            refreshBtn.setEnabled(true);
        }
    };

    BroadcastReceiver refreshList=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            btnAnimation= AnimationUtils.loadAnimation(getContext(),R.anim.refresh_button_rotate);
            refreshBtn.startAnimation(btnAnimation);
            refreshBtn.setEnabled(false);
            adapter.reFreshList();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:
                btnAnimation= AnimationUtils.loadAnimation(getContext(),R.anim.refresh_button_rotate);
                refreshBtn.startAnimation(btnAnimation);
                refreshBtn.setEnabled(false);
                adapter.reFreshList();
                break;
        }
    }
}
