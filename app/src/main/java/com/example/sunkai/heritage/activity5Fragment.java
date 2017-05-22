package com.example.sunkai.heritage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment;
import com.example.sunkai.heritage.Data.ClassifyActivityDivide;
import com.example.sunkai.heritage.Data.classifyActiviyData;
import com.example.sunkai.heritage.OverrideClass.BaseFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 首页viewpager五个页面的第五个页面
 * * 方法说明请看Fragment1
 */
public class activity5Fragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    List<classifyActiviyData> activityDatas;
    activityListviewAdapter activityListviewAdapter;
    ListView activityListview;

    /** 标志位，标志已经初始化完成 */
    private boolean isPrepared;
    /** 是否已被加载过一次，第二次就不再去请求数据了 */
    private boolean mHasLoadedOnce;

    public activity5Fragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static activity5Fragment newInstance(String param1, String param2) {
        activity5Fragment fragment = new activity5Fragment();
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
        View view=inflater.inflate(R.layout.fragment_activity2, container, false);
        activityListview=(ListView)view.findViewById(R.id.activity_listview);
//        new Thread(getActivityCount).start();
        isPrepared=true;
        lazyLoad();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    protected void lazyLoad(){
        if(!isPrepared||!isVisible||mHasLoadedOnce){
            return;
        }
        new Thread(getActivityCount).start();

    }

    Runnable getActivityCount=new Runnable() {
        @Override
        public void run() {
            int count= HandleMainFragment.GetActivityCount(ClassifyActivityDivide.divide[4]);
            Message msg=new Message();
            msg.what=count;
            getActivityCountHandler.sendMessage(msg);
        }
    };

    Handler getActivityCountHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what>0){
                activityDatas=new ArrayList<>();
                for(int i=0;i<msg.what;i++) {
                    activityDatas.add(new classifyActiviyData());
                }
                activityListviewAdapter=new activityListviewAdapter(getActivity(),activityDatas,ClassifyActivityDivide.divide[4]);
                activityListview.setAdapter(activityListviewAdapter);
                mHasLoadedOnce=true;
                activityListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        classifyActiviyData activitydata=(classifyActiviyData)parent.getAdapter().getItem(position);
                        Intent intent=new Intent(getActivity(),activityInformationActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("activity",activitydata);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        }
    };
}
