package com.example.sunkai.heritage.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.sunkai.heritage.Activity.ActivityInformationActivity;
import com.example.sunkai.heritage.Adapter.ActivityRecyclerViewAdpter;
import com.example.sunkai.heritage.Data.ClassifyActiviyData;
import com.example.sunkai.heritage.R;

import java.io.ByteArrayOutputStream;
import java.util.List;


/**
 * 首页viewpager五个页面的第一个页面
 * 理论上五个页面其实可以公用一个Fragment类，通过传递的参数不同执行不同的方法即可
 * 但是因为时间紧张，故用了5个Fragment类
 * 除了第一个页面，其他四个全都继承与BaseFrament，并实现了Lazyload，使其在页面可见的时候才加载内容
 */
public class ActivityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String channel = "channel";
    List<ClassifyActiviyData> activityDatas;
    ActivityRecyclerViewAdpter activityListviewAdapter;
    RecyclerView activityRecyclerView;

    // TODO: Rename and change types of parameters
    private String channelName;

    private OnFragmentInteractionListener mListener;

    public ActivityFragment() {
        // Required empty public constructor
    }

    /**
    创建一个此instance的实例，传入的参数为channel的名字，用于Fragment里面的ListView获取对应通道的内容
     */
    public static ActivityFragment newInstance(String channelName) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putString(channel, channelName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            channelName = getArguments().getString(channel);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_activity1, container, false);
        activityRecyclerView=(RecyclerView)view.findViewById(R.id.activity_listview);
        activityListviewAdapter=new ActivityRecyclerViewAdpter(getActivity(),channelName);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        activityRecyclerView.setLayoutManager(layoutManager);
        activityRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        activityRecyclerView.setHasFixedSize(true);
        activityRecyclerView.setAdapter(activityListviewAdapter);
        activityListviewAdapter.setOnItemClickListen((parent,position)->{
            ClassifyActiviyData activitydata=activityListviewAdapter.getItem(position);
            ImageView imageView=(ImageView)parent.findViewById(R.id.activity_layout_img);
            imageView.setDrawingCacheEnabled(true);
            Bitmap bitmap=imageView.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            activitydata.setActivityImage(byteArrayOutputStream.toByteArray());
            Intent intent=new Intent(getActivity(),ActivityInformationActivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("activity",activitydata);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        return view;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



}