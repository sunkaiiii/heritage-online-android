package com.example.sunkai.heritage.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment;
import com.example.sunkai.heritage.Data.ClassifyActivityDivide;
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.MainActivityData;
import com.example.sunkai.heritage.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ViewPager viewPager;
    private ImageView[] tips;
    private ImageView[] mImageViews;
    private int[] imgIdArray;
    private View view;
    List<MainActivityData> activityDatas;
    Bitmap[] bitmaps;
    int count=0;

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        view= inflater.inflate(R.layout.fragment_main, container, false);
        bitmaps=new Bitmap[4];
        //主页活动页面
        imgIdArray=new int[]{R.mipmap.img1,R.mipmap.img1,R.mipmap.img1,R.mipmap.img1};
        viewPager=(ViewPager)view.findViewById(R.id.main_fragment_viewPager);
        loadMyPage();
        //设置Adapter
        viewPager.setAdapter(new MyAdapter());
        //设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(this);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.setCurrentItem((mImageViews.length) * 100);
        new Thread(loadMainActivity).start();

        //此处是活动分类
//        String[] activitys={"分类1","分类2"};
        TabLayout tableLayout=(TabLayout)view.findViewById(R.id.tab_layout);
        ViewPager viewPager=(ViewPager)view.findViewById(R.id.main_tab_content);
        setupViewPager(viewPager);
        tableLayout.setupWithViewPager(viewPager);
        tableLayout.setTabTextColors(Color.GRAY,Color.WHITE);
        for(int i=0;i< ClassifyActivityDivide.getDivide().length;i++){
            tableLayout.getTabAt(i).setText(ClassifyActivityDivide.getDivide()[i]);
        }
        tableLayout.getTabAt(0).select();

        return view;
    }

    public void loadMyPage(){
        ViewGroup group=(ViewGroup)view.findViewById(R.id.main_fragment_imageView);
        tips=new ImageView[imgIdArray.length];
        for(int i=0;i<tips.length;i++){
            ImageView imageView=new ImageView(getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10,10));
            tips[i]=imageView;
            if(i == 0){
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            if(count>=tips.length) {
                group.addView(imageView, layoutParams);
            }
        }
        mImageViews = new ImageView[imgIdArray.length];
        for(int i=0; i<mImageViews.length; i++){
            ImageView imageView = new ImageView(getActivity());
            mImageViews[i] = imageView;
            if(count<mImageViews.length) {
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
    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter=new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        //给viewpager添加Fragment，以并传输通道名以显示对应通道的内容
        for(String channelName:ClassifyActivityDivide.getDivide()){
            adapter.insertNewFragment(ActivityFragment.newInstance(channelName));
        }
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        public ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void insertNewFragment(android.support.v4.app.Fragment fragment) {
            mFragmentList.add(fragment);
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

    /**
     * 设置选中的tip的背景
     * @param selectItems
     */
    private void setImageBackground(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i == selectItems){
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    Runnable loadMainActivity=new Runnable() {
        @Override
        public void run() {
            activityDatas= HandleMainFragment.ReadMainActivity();
            Message message=new Message();
            if(null==activityDatas){
                message.what=0;
            }
            else {
                message.what=1;
            }
            loadMainActivityHandler.sendMessage(message);
        }
    };

    Handler loadMainActivityHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                for(int i=0;i<activityDatas.size();i++){
                    Bitmap bitmap= BitmapFactory.decodeByteArray(activityDatas.get(i).getActivityImage(),0,activityDatas.get(i).getActivityImage().length);
                    bitmaps[i]=bitmap;
                }
                loadMyPage();
                viewPager.setAdapter(new MyAdapter());
            }
        }
    };
}
