package com.example.sunkai.heritage;

import android.content.Context;
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
 * Created by sunkai on 2017-4-24.
 * 此类用于处理我的预约的List
 */

public class myOrderListViewAdapter extends BaseAdapter {
    private Context context;
    private List<folkData> datas;
    Bitmap[] bitmap;
    public myOrderListViewAdapter(Context context,List<folkData> datas){
        this.context=context;
        this.datas=datas;
        bitmap=new Bitmap[datas.size()];
        new Thread(getFolkImage).start();
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
        vh.v1.setText("        " + content);
        vh.v2.setText(location);
        vh.v3.setText(title);
        vh.v4.setImageBitmap(bitmap[position]);
        return convertView;
    }

    public class Holder {
        TextView v1,v2,v3;
        ImageView v4;
    }


    Runnable getFolkImage=new Runnable() {
        @Override
        public void run() {
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
        }
    };

    Handler getFolkImageHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                notifyDataSetChanged();
            }
        }
    };
}
