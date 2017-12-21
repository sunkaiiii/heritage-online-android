package com.example.sunkai.heritage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
    LruCache<Integer,Bitmap> lruCache;
    private ListView thisListView;
    public myOrderListViewAdapter(Context context,List<folkData> datas){
        this.context=context;
        this.datas=datas;
        int maxMemory=(int)Runtime.getRuntime().maxMemory();
        int avilableMemory=maxMemory/8;
        lruCache=new LruCache<Integer,Bitmap>(avilableMemory){
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
//        new Thread(getFolkImage).start();
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
        if(thisListView==null){
            thisListView=(ListView)parent;
        }
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
        String content=data.getContent();
        String location=data.getLocation();
        String title=data.getTitle();
        vh.v1.setText("        " + content);
        vh.v2.setText(location);
        vh.v3.setText(title);
//        vh.v4.setImageBitmap(bitmap[position]);
        Bitmap bitmap=lruCache.get(data.getId());
        vh.v4.setTag(data.getId());
        if(null==bitmap)
            new LoadImageAsync(data.getId(),position).execute();
        else
            vh.v4.setImageBitmap(bitmap);
        return convertView;
    }

    public class Holder {
        TextView v1,v2,v3;
        ImageView v4;
    }

    class LoadImageAsync extends AsyncTask<Void,Void,Bitmap>{
        int id;
        int position;
        public LoadImageAsync(int id,int position){
            this.id=id;
            this.position=position;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            byte[] img=HandleFolk.GetFolkImage(id);
            if(null==img)
                return null;
            datas.get(position).setImage(img);
            InputStream in=new ByteArrayInputStream(img);
            Bitmap bitmap=HandlePic.handlePic(context,in,0);
            lruCache.put(id,bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView=(ImageView) thisListView.findViewWithTag(id);
            if(imageView==null)
                return;
            imageView.setImageBitmap(bitmap);
        }
    }

}
