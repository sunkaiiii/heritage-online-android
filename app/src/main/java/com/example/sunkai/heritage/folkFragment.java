package com.example.sunkai.heritage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleFolk;
import com.example.sunkai.heritage.Data.folkData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 民间页的类
 */
public class folkFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<folkData> datas;
    folkListviewAdapter folkListviewAdapter;
    // TODO: Rename and change types of parameters

    private EditText folk_edit;
    private Spinner folk_heritages_spinner;
    private Spinner folk_location_spinner;
    private ListView folk_show_listview;
    private ImageView folk_search_btn;
    public ProgressBar loadProgress;
    List<folkData> getDatas = new ArrayList<>();//用于处理搜索的List
    public static boolean isLoadData = false;

    boolean changeData = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folk, container, false);
        initView(view);
        /**
         * 当预约发生改变的时候，通知个人中心我的预约重新加载我的预约
         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.adpterGetDataBroadCast");
        folk_location_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectAdpterInformation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        folk_heritages_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectAdpterInformation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        folkListviewAdapter = new folkListviewAdapter(getActivity(), folkFragment.this);
        folk_show_listview.setAdapter(folkListviewAdapter);
        folk_show_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                folkData folkData = (folkData) folkListviewAdapter.getItem(position);
                ImageView imageView = (ImageView) view.findViewById(R.id.list_img);
                imageView.setDrawingCacheEnabled(true);
                Drawable drawable = imageView.getDrawable();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                folkData.image = byteArrayOutputStream.toByteArray();
                bundle.putSerializable("activity", folkData);
                Intent intent = new Intent(getActivity(), JoinActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }


    private void SelectAdpterInformation() {
        /**
         * datas作为原始数据保证期不做任何改变
         * getDatas在搜索框为空的时候即使datas，不为空的时候即为搜索出来的结果
         * selectData负责进行第一轮筛选得出的结果
         * finalData对第二个spinner的内容与selectData中的内容进行比对，筛选出结果
         * 将finalData传递个adpter
         */

        List<folkData> selectDatas = new ArrayList<>();
        if (folk_location_spinner.getSelectedItemPosition() == 0) {
            if (null != folkListviewAdapter) {
                selectDatas = getDatas;
            }
        } else {
            String selectLocation = (String) folk_location_spinner.getSelectedItem();
            selectDatas=filterLoacation(getDatas,selectLocation);
        }
        if (folk_heritages_spinner.getSelectedItemPosition() != 0) {
            String selectHeritage = (String) folk_heritages_spinner.getSelectedItem();
            selectDatas=filterHeritage(selectDatas,selectHeritage);
        }
        if (null != folkListviewAdapter && isLoadData) {
            folkListviewAdapter.setNewDatas(selectDatas);
        }
    }

//    private List<folkData> filterData(List<folkData> Datas,String dataString,String filterString){
//        if(Build.VERSION.SDK_INT>=24){
//            return Datas.stream().filter().collect(Collectors.toList());
//        }
//        List<folkData> selectDatas=new ArrayList<>();
//        for (folkData folkData:Datas) {
//            if () {
//                selectDatas.add(folkData);
//            }
//        }
//        return selectDatas;
//    }

    private static boolean CompareString(String str1,String str2){
        return str1.equals(str2);
    }

    private List<folkData> filterLoacation(List<folkData> Datas,String locationString){
        if(Build.VERSION.SDK_INT>=24){
            return Datas.stream().filter(eachData->eachData.location.equals(locationString)).collect(Collectors.toList());
        }
        List<folkData> selectDatas=new ArrayList<>();
        for (folkData folkData:Datas) {
            if (folkData.location.equals(locationString)) {
                selectDatas.add(folkData);
            }
        }
        return selectDatas;
    }

    private List<folkData> filterHeritage(List<folkData> Datas,String heritageString){
        if(Build.VERSION.SDK_INT>=24)
            return Datas.stream().filter(eachData->eachData.divide.equals(heritageString)).collect(Collectors.toList());
        List<folkData> selectDatas=new ArrayList<>();
        for (folkData folkData:Datas) {
            if (folkData.divide.equals(heritageString)) {
                selectDatas.add(folkData);
            }
        }
        return selectDatas;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initView(View view) {
        folk_edit = (EditText) view.findViewById(R.id.folk_edit);
        folk_heritages_spinner = (Spinner) view.findViewById(R.id.folk_heritages_spinner);
        folk_location_spinner = (Spinner) view.findViewById(R.id.folk_location_spinner);
        folk_show_listview = (ListView) view.findViewById(R.id.folk_show_listview);
        folk_search_btn = (ImageView) view.findViewById(R.id.folk_searchbtn);
        loadProgress = (ProgressBar) view.findViewById(R.id.folk_load_progress);
        folk_search_btn.setOnClickListener(this);
        folk_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /**
                 * 当搜索框被清空的时候，自动的将getData的内容清空并还原为datas的数据
                 * 刷新list使得list继续显示全部的内容
                 */
                if (TextUtils.isEmpty(folk_edit.getText())) {
//                    folkListviewAdapter.setNewDatas(datas);
                    getDatas = datas;
                    SelectAdpterInformation();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.folk_searchbtn:
                submit();
        }
    }

    private void submit() {
        // validate
        String edit = folk_edit.getText().toString().trim();
        if (TextUtils.isEmpty(edit)) {
            Toast.makeText(getContext(), "输入的内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyboard();
        setWidgetEnable(false);
        new HandleSearch(edit).execute();
    }

    public void setData(boolean changeData, List<folkData> datas) {
        this.changeData = changeData;
        this.datas = datas;
        getDatas=datas;
    }

    class HandleSearch extends AsyncTask<Void, Void, Integer> {
        String searInfo;
        List<folkData> searchData;

        /**
         * 此类用于处理搜索的相关内容
         *
         * @param searInfo 搜索框的文本
         */
        HandleSearch(String searInfo) {
            this.searInfo = searInfo;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            searchData = HandleFolk.Search_Folk_Info(searInfo);
            return searchData == null ? 0 : 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 1) {
                getDatas = searchData;
                SelectAdpterInformation();
            }
            setWidgetEnable(true);
        }
    }

    public void setWidgetEnable(boolean enable) {
        folk_heritages_spinner.setEnabled(enable);
        folk_location_spinner.setEnabled(enable);
        folk_search_btn.setEnabled(enable);
        folk_edit.setEnabled(enable);
    }


    //隐藏键盘
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
