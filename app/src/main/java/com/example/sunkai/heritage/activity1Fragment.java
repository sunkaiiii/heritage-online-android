package com.example.sunkai.heritage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment;
import com.example.sunkai.heritage.Data.ClassifyActivityDivide;
import com.example.sunkai.heritage.Data.classifyActiviyData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 首页viewpager五个页面的第一个页面
 * 理论上五个页面其实可以公用一个Fragment类，通过传递的参数不同执行不同的方法即可
 * 但是因为时间紧张，故用了5个Fragment类
 * 除了第一个页面，其他四个全都继承与BaseFrament，并实现了Lazyload，使其在页面可见的时候才加载内容
 */
public class activity1Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<classifyActiviyData> activityDatas;
    activityListviewAdapter activityListviewAdapter;
    ListView activityListview;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public activity1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment activity1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static activity1Fragment newInstance(String param1, String param2) {
        activity1Fragment fragment = new activity1Fragment();
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
        View view=inflater.inflate(R.layout.fragment_activity1, container, false);
        activityListview=(ListView)view.findViewById(R.id.activity_listview);

//        @本地测试数据
//        activityDatas=new ArrayList<>();
//        int[] imgs={R.mipmap.i1,R.mipmap.i2,R.mipmap.i3,R.mipmap.i4,R.mipmap.i5,R.mipmap.i6};
//        String[] texts={"这次实验当中首次接触到了CSS样式，通过上课的敲打和课后的理解，发现其与以前学的知识具有一定的连通性，在理解上也不是很难。但是如何编写一个好的CSS样式十分复杂的，可以看到仅仅是几个CSS文件就加入了不少对于不"
//        ,"彼此的冲突和互动产生。通过对于剧本的分析，可以分析出什么样性格的人物他们在一起产生互动、矛盾可以推进电影剧情，亦或者对于剧情的成功有帮助。同时对于激烈场景或者主要剧情发生的地点也可以加以分析，通过对于地点的分析可以分析出对于某种个性的角色，通常是在哪里伴随着激烈的剧情发展，同时大部分具有相同个性的角色他们应该都具有相似的角色标签以及出没于接近的各个建筑之中"
//        ,"依托于时下流行的大数据，可以参照其他相同类型的剧本的电影的票房",
//        "多经典的电影背后的底子并非不是无迹可寻的，其很多可能都是采样于诸多民间经典的故事。国内电影也不乏这样的改编，对于剧本来说如何能将古老的故事改变成一个立体丰满的电影剧本同样是一件十分具有挑战的事情。在"
//        ,"于IP改编的电影提供出合理的改变路线。以数据分析分析出那种改编的路线更适合荧屏上的展现，以及如何在电影这个时间框架下展现出松弛有度的剧情设定和人物刻画，从而让最","在剧本分析中，往往还可以根据剧本的用词分析出剧本角色台词的用词是否符合当下的流行，恰当的实"};
//        for(int a=0;a<imgs.length;a++){
//            activityData data=new activityData();
//            data.imgID=imgs[a];
//            data.title=texts[a];
//            activityDatas.add(data);
//        }
//        activityListviewAdapter=new activityListviewAdapter(getActivity(),activityDatas);
//        activityListview.setAdapter(activityListviewAdapter);

        new Thread(getActivityCount).start();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * 在获取信息的时候首先获得活动的数量，可以初始化List
     * 感觉这并不是一个好的方法，如果未来继续维护软件的话应该会改掉这部分
     */
    Runnable getActivityCount=new Runnable() {
        @Override
        public void run() {
            int count= HandleMainFragment.GetActivityCount(ClassifyActivityDivide.divide[0]);
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
                /**
                 * 在获得数量之后，加入List内容，使其List在软件中显示出来，并逐步填充信息
                 */
                for(int i=0;i<msg.what;i++) {
                    activityDatas.add(new classifyActiviyData());
                }
                activityListviewAdapter=new activityListviewAdapter(getActivity(),activityDatas,ClassifyActivityDivide.divide[0]);
                activityListview.setAdapter(activityListviewAdapter);
                activityListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        classifyActiviyData activitydata=(classifyActiviyData)parent.getAdapter().getItem(position);
                        ImageView imageView=(ImageView)view.findViewById(R.id.activity_layout_img);
                        imageView.setDrawingCacheEnabled(true);
                        Bitmap bitmap=imageView.getDrawingCache();
                        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                        activitydata.activityImage=byteArrayOutputStream.toByteArray();
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
