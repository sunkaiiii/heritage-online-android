package com.example.sunkai.heritage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunkai.heritage.ConnectWebService.HandleFolk;
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.folkData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by sunkai on 2017/3/3.
 */

public class folkListviewAdapter extends BaseAdapter {
    private Context context;
    private List<folkData> datas;
    Bitmap[] bitmap;
    folkFragment folkFragment;
    public folkListviewAdapter(Context context,List<folkData> datas,folkFragment folkFragment){
        this.context=context;
        this.datas=datas;
        bitmap=new Bitmap[datas.size()];
        this.folkFragment=folkFragment;
        if(this.folkFragment!=null) {
            setProgress(true);
        }
        new Thread(getFolkInformationThred).start();
    }
    public int getCount(){
        return datas.size();
    }
    public Object getItem(int position) {
        return datas.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        Holder vh;
        if(convertView==null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.folk_listview_layout, null);
            vh = new Holder();
            vh.v1 = (TextView) convertView.findViewById(R.id.list_text);
            vh.v2 = (TextView) convertView.findViewById(R.id.list_location);
            vh.v3 = (TextView) convertView.findViewById(R.id.list_title);
            vh.v4 = (ImageView) convertView.findViewById(R.id.list_img);
            vh.v5=(TextView)convertView.findViewById(R.id.list_divide);
            convertView.setTag(vh);
        }
        else
        {
            vh=(Holder)convertView.getTag();
        }
        folkData data=datas.get(position);
        String content=data.content;
        String location=data.location;
        String title=data.title;
        String divide=data.divide;
        vh.v1.setText("        " + content);
        vh.v2.setText(location);
        vh.v3.setText(title);
        if(bitmap.length>position) {
            vh.v4.setImageBitmap(bitmap[position]);
        }
        vh.v5.setText(divide);
        return convertView;
    }
    public List<folkData> getDatas(){
        return this.datas;
    }
    public void setProgress(boolean show){
        if(show){
            folkFragment.loadProgress.setVisibility(View.VISIBLE);
            folkFragment.setWidgetEnable(!show);
        }
        else{
            folkFragment.loadProgress.setVisibility(View.GONE);
            folkFragment.setWidgetEnable(!show);
        }
    }
    public void setNewDatas(List<folkData> datas){
        this.datas=datas;
        bitmap=new Bitmap[datas.size()];
        for(int i=0;i<datas.size();i++){
            if(null!=datas.get(i).image) {
                InputStream in = new ByteArrayInputStream(datas.get(i).image);
                bitmap[i] = HandlePic.handlePic(context, in, 0);
            }
        }
        notifyDataSetChanged();
    }
    public List<folkData> getData(){
        return datas;
    }
    public class Holder {
        TextView v1,v2,v3,v5;
        ImageView v4;
    }

    Runnable getFolkInformationThred=new Runnable() {
        @Override
        public void run() {
            datas= HandleFolk.GetFolkInforMation();
            getFolkInformationHandler.sendEmptyMessage(1);
        }
    };

    android.os.Handler getFolkInformationHandler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg){
            notifyDataSetChanged();
            Intent intent=new Intent("android.intent.action.adpterGetDataBroadCast");
            intent.putExtra("message","changed");
            context.sendBroadcast(intent);
            folkFragment.isLoadData=true;
            new Thread(getFolkImage).start();
        }
    };
    Runnable getFolkImage=new Runnable() {
        @Override
        public void run() {
            bitmap=new Bitmap[datas.size()];
            for(int i=0;i<datas.size();i++){
                byte[] img=HandleFolk.GetFolkImage(datas.get(i).id);
                if(null==img){
                    getFolkImageHandler.sendEmptyMessage(0);
                }
                datas.get(i).image=img;
                InputStream in=new ByteArrayInputStream(img);
                bitmap[i]= HandlePic.handlePic(context,in,0);
                getFolkImageHandler.sendEmptyMessage(1);
            }
            getFolkImageHandler.sendEmptyMessage(2);

        }
    };


    Handler getFolkImageHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                notifyDataSetChanged();
            }
            if(msg.what==2){
                if(folkFragment!=null) {
                    setProgress(false);
                }
            }
        }
    };
}

