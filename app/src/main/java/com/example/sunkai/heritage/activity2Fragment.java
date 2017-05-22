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
 * 首页viewpager五个页面的第二个页面
 * 方法说明请看Fragment1
 */
public class activity2Fragment extends BaseFragment {
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

    /** 标志位，标志已经初始化完成 */
    private boolean isPrepared;
    /** 是否已被加载过一次，第二次就不再去请求数据了 */
    private boolean mHasLoadedOnce;
    private OnFragmentInteractionListener mListener;

    public activity2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment activity2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static activity2Fragment newInstance(String param1, String param2) {
        activity2Fragment fragment = new activity2Fragment();
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
        View view=inflater.inflate(R.layout.fragment_activity2, container, false);
        activityListview=(ListView)view.findViewById(R.id.activity_listview);
//        @本地测试数据
//        activityDatas=new ArrayList<>();
//        int[] imgs={R.mipmap.i7,R.mipmap.i8,R.mipmap.i9,R.mipmap.i10,R.mipmap.i11,R.mipmap.i12};
//        String[] texts={"首先时间来到了戊戌变法时期，戊戌变法是康有为和梁启超两个人共同发起的，但是康有为和梁启超实际上师徒的政治取向并不是完全统一的，在变法失败之后两个人的政治观念逐渐的渐行渐远最终导致了师徒两人关系决裂，而变法失败的一个"
//                ,"来我们来开一下一些其他人物来评价袁世凯，我们先看一下正面的评价，一个是李鸿章的（照着读一下），另一个是袁世凯的私人外交秘书，既然是自己的人当然要说自己的主人一些好话了。（照着再读一下）。然后是一些反面的评价，分别来"
//                ,"令孙中山完全没有想到的事情就是，在退位诏书当中，竟然写的是即由袁世凯以全权组织临时共和政府，与民军协商统一办法...",
//                "汪淼感到一股逼人的寒气。前方出现了两个行走的人影，在曙光的背景里显现黑色的剪影。汪淼追了上去，他看到两人都是男性，披着破烂的长袍，外面还裹着一张肮脏的兽皮，都带着一把青铜时代那种又宽又短的剑。"
//                ,"他一直活到现在呢，纣王也活着。”另一个没背箱子的人说，“我是周文王的追随者，我的ID就是：‘周文王追随者’，他可是个天","至少从莎士比亚到巴尔扎克到托尔斯泰都是这样，他们创造的那些经典形象都是这么着从他们思想的子宫中生出来的。"};
//        for(int a=0;a<imgs.length;a++){
//            classifyActiviyData data=new activityData();
//            data.imgID=imgs[a];
//            data.title=texts[a];
//            activityDatas.add(data);
//        }
//        activityListviewAdapter=new activityListviewAdapter(getActivity(),activityDatas);
//        activityListview.setAdapter(activityListviewAdapter);
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
            int count= HandleMainFragment.GetActivityCount(ClassifyActivityDivide.divide[1]);
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
                activityListviewAdapter=new activityListviewAdapter(getActivity(),activityDatas,ClassifyActivityDivide.divide[1]);
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
