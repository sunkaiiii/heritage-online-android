package com.example.sunkai.heritage.Fragment;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.Activity.AddFindCommentActivity;
import com.example.sunkai.heritage.Activity.LoginActivity;
import com.example.sunkai.heritage.Activity.SearchActivity;
import com.example.sunkai.heritage.Activity.UserCommentDetailActivity;
import com.example.sunkai.heritage.Adapter.FindFragmentRecyclerViewAdpter;
import com.example.sunkai.heritage.ConnectWebService.HandleFind;
import com.example.sunkai.heritage.Data.FindActivityData;
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.MySqliteHandler;
import com.example.sunkai.heritage.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 发现页面的类
 */

public class FindFragment extends Fragment implements View.OnClickListener{
    private static final int FROM_USER_COMMENT_DETAIL=2;
    private ImageView[] tips;
    private ImageView[] mImageViews;
    private ImageView findSearchBtn;
    private TextView findEdit;
    private int[] imgIdArray;
    private View view;
    private FindFragmentRecyclerViewAdpter recyclerViewAdpter;
    private RecyclerView recyclerView;
    private FloatingActionButton refreshBtn;
    private Spinner selectSpiner;
    public Button addCommentBtn;
    public ViewPager viewPager;
    private RelativeLayout searchRelative;
    List<FindActivityData> activityDatas;
    Animation btnAnimation;
    private Bitmap[] bitmaps; //装载首页轮转窗的Bitmap[]
    int count = 0;
    int count2 = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_find, container, false);
        findEdit=(TextView)view.findViewById(R.id.find_text);
        findSearchBtn=(ImageView)view.findViewById(R.id.find_searchbtn);
        findEdit.setOnClickListener(this);
        findSearchBtn.setOnClickListener(this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.animationStop");
        getActivity().registerReceiver(animationStopReceiver,intentFilter);
        selectSpiner = (Spinner) view.findViewById(R.id.find_select_spinner);
        setHasOptionsMenu(true);
        bitmaps = new Bitmap[4];
        //主页活动页面
        imgIdArray = new int[]{R.mipmap.img1, R.mipmap.img1, R.mipmap.img1, R.mipmap.img1};
        viewPager = (ViewPager) view.findViewById(R.id.find_fragment_viewPager);
        loadMyPage();
        //设置Adapter
        viewPager.setAdapter(new MyAdapter());
        //设置监听，主要是设置点点的背景
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.setCurrentItem((mImageViews.length) * 100);
//        listView = (ListView) view.findViewById(R.id.fragment_find_listview);
        recyclerView=(RecyclerView)view.findViewById(R.id.fragment_find_recyclerView);
        activityDatas = new ArrayList<>();
        loadMyPage();
        viewPager.setAdapter(new MyAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                new getActivityID(position % 4,FindFragment.this).execute();
                setImageBackground(position % mImageViews.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //加载轮播页第一张图片
        new getActivityID(0,this).execute();

        /*
         * 程序默认显示广场的全部帖子
         */
        recyclerViewAdpter=new FindFragmentRecyclerViewAdpter(getActivity(),1);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdpter);
        refreshBtn = (FloatingActionButton) view.findViewById(R.id.fragment_find_refreshbtn);
        searchRelative=(RelativeLayout)view.findViewById(R.id.find_fragment_search_relative);
        refreshBtn.setOnClickListener(v -> {
            btnAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.refresh_button_rotate);
            refreshBtn.startAnimation(btnAnimation);
            refreshBtn.setEnabled(false);
            recyclerViewAdpter.reFreshList();
        });

        /*
         * Spinear切换，重新加载adpater的数据
         */
        selectSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        recyclerViewAdpter = new FindFragmentRecyclerViewAdpter(getActivity(), 1);
                        setAdpterClick(recyclerViewAdpter);
                        recyclerView.setAdapter(recyclerViewAdpter);
                        break;
                    case 1:
                        if (LoginActivity.userID == 0) {
                            Toast.makeText(getActivity(), "没有登录", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("isInto", 1);
                            startActivityForResult(intent, 1);
                            selectSpiner.setSelection(0);
                            return;
                        }
                        recyclerViewAdpter = new FindFragmentRecyclerViewAdpter(getActivity(), 2);
                        setAdpterClick(recyclerViewAdpter);
                        recyclerView.setAdapter(recyclerViewAdpter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /*
         * 发帖
         */
        addCommentBtn = (Button) view.findViewById(R.id.btn_add_comment);
        addCommentBtn.setOnClickListener(v -> {
            if (LoginActivity.userID == 0) {
                Toast.makeText(getActivity(), "没有登录", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("isInto", 1);
                startActivityForResult(intent, 1);
                return;
            }
            Intent intent = new Intent(getActivity(), AddFindCommentActivity.class);
            /*
             * 当成功添加帖子的时候，页面刷新
             */
            startActivityForResult(intent, 1);
        });
        return view;
    }

    private void setAdpterClick(FindFragmentRecyclerViewAdpter adpter) {
        adpter.setOnItemClickListen((view, position) -> {
            Intent intent = new Intent(getActivity(), UserCommentDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data",adpter.getItem(position));
            bundle.putInt("position", position);
            ImageView imageView = (ImageView) view.findViewById(R.id.fragment_find_litview_img);
            imageView.setDrawingCacheEnabled(true);
            Drawable drawable = imageView.getDrawable();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            intent.putExtra("bitmap", out.toByteArray());
            intent.putExtras(bundle);
            startActivityForResult(intent, FROM_USER_COMMENT_DETAIL);
        });
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.find_text:
            case R.id.find_searchbtn:
                if(LoginActivity.userID==0){
                    Toast.makeText(getActivity(),"没有登录",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isInto",1);
                    startActivityForResult(intent,1);
                }
                Intent intent=new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);
                break;
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
            ((ViewPager) container).removeView(mImageViews[position % mImageViews.length]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mImageViews[position % mImageViews.length], 0);
            return mImageViews[position % mImageViews.length];
        }
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }


    /**
     * 按照图片的id获取首页浮窗信息
     */
    static class getActivityID extends AsyncTask<Void, Void, Bitmap> {
        int index;
        SQLiteDatabase db;
        WeakReference<FindFragment> findFragmentWeakReference;
        private getActivityID(int index,FindFragment findFragment) {
            this.index = index;
            findFragmentWeakReference=new WeakReference<>(findFragment);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            FindFragment findFragment=findFragmentWeakReference.get();
            if(findFragment==null)
                return null;
            if (findFragment.activityDatas==null||findFragment.activityDatas.size() == 0)
                findFragment.activityDatas = HandleFind.Get_Find_Activity_ID(findFragment.activityDatas);
            db = MySqliteHandler.INSTANCE.GetReadableDatabase();
            String table = "find_fragment_activity";
            String selection = "id=?";
            if(findFragment.activityDatas==null||findFragment.activityDatas.size()==0)
                return null;
            String[] selectionArgs = new String[]{String.valueOf(findFragment.activityDatas.get(index).getId())};
            Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                int imageIndex = cursor.getColumnIndex("image");
                byte[] img = cursor.getBlob(imageIndex);
                cursor.close();
                if (img != null) {
                    InputStream in = new ByteArrayInputStream(img);
                    return HandlePic.handlePic(findFragment.getActivity(), in, 0);
                }
            }
            FindActivityData data = HandleFind.Get_Find_Activity_Information(findFragment.activityDatas.get(index).getId());
            findFragment.activityDatas.get(index).setTitle(data.getTitle());
            findFragment.activityDatas.get(index).setContent(data.getContent());
            findFragment.activityDatas.get(index).setImage(data.getImage());
            InputStream in = new ByteArrayInputStream(findFragment.activityDatas.get(index).getImage());
            Bitmap bitmap = HandlePic.handlePic(findFragment.getActivity(), in, 0);
            db = MySqliteHandler.INSTANCE.GetWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", data.getId());
            contentValues.put("title", data.getTitle());
            contentValues.put("image", data.getImage());
            contentValues.put("content", data.getContent());
            db.insert(table, null, contentValues);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            FindFragment findFragment=findFragmentWeakReference.get();
            if(findFragment==null)
                return;
            findFragment.mImageViews[index].setScaleType(ImageView.ScaleType.CENTER_CROP);
            findFragment.mImageViews[index].setImageBitmap(bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                startRefreshButtonAnimation();
                recyclerViewAdpter.reFreshList();
                break;
            case FROM_USER_COMMENT_DETAIL:
                if(resultCode== UserCommentDetailActivity.ADD_COMMENT) {
                    Bundle bundle = data.getExtras();
                    int commentID =bundle.getInt("commentID");
                    int position=bundle.getInt("position");
                    recyclerViewAdpter.getReplyCount(commentID,position);
                }else if(resultCode==UserCommentDetailActivity.DELETE_COMMENT){
                    startRefreshButtonAnimation();
                    recyclerViewAdpter.reFreshList();
                }
        }
    }

    private void startRefreshButtonAnimation(){
        btnAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.refresh_button_rotate);
        refreshBtn.startAnimation(btnAnimation);
        refreshBtn.setEnabled(false);
    }

    BroadcastReceiver animationStopReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshBtn.clearAnimation();
            refreshBtn.setEnabled(true);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(animationStopReceiver);
    }
}
