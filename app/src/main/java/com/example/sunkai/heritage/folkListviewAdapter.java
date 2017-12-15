package com.example.sunkai.heritage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.sunkai.heritage.Data.MySqliteHandler;
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
    folkFragment folkFragment;
    private ListView listView;
    private LruCache<Integer,Bitmap> lruCache;
    public folkListviewAdapter(Context context,folkFragment folkFragment){
        this.context=context;
        int maxSize=(int)Runtime.getRuntime().maxMemory();
        int avilibleMemory=maxSize/16;
        lruCache=new LruCache<Integer,Bitmap>(avilibleMemory){
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        this.folkFragment=folkFragment;
        if(this.folkFragment!=null) {
            setProgress(true);
        }
        //获取folk的文本信息
        new getInformation().execute();
    }
    public int getCount(){
        return datas==null?0:datas.size();
    }
    public Object getItem(int position) {
        return datas.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        if(listView==null)
            listView=(ListView)parent;
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
        String content=data.getContent();
        String location=data.getLocation();
        String title=data.getTitle();
        String divide=data.getDivide();
        vh.v1.setText("        " + content);
        vh.v2.setText(location);
        vh.v3.setText(title);
        vh.v4.setImageResource(R.drawable.empty_background);
        vh.v4.setTag(data.getId());
        Bitmap bitmap=lruCache.get(data.getId());
        if(bitmap==null){
            new getFolkImage(data.getId()).execute();
        }else{
            vh.v4.setImageBitmap(bitmap);
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
        notifyDataSetChanged();
    }
    public List<folkData> getData(){
        return datas;
    }
    public class Holder {
        TextView v1,v2,v3,v5;
        ImageView v4;
    }

    private class getInformation extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            datas=HandleFolk.GetFolkInforMation();
            folkFragment.isLoadData=true;
            folkFragment.setData(true,datas);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
            setProgress(false);
        }
    }

    private class getFolkImage extends AsyncTask<Void,Void,Bitmap>{
        int id;
        SQLiteDatabase db=MySqliteHandler.INSTANCE.GetReadableDatabase();
        public getFolkImage(int id){this.id=id;}
        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap=null;
            String table="folk_image";
            String selection="id=?";
            String[] selectionArgs=new String[]{String.valueOf(id)};
            Cursor cursor=db.query(table,null,selection,selectionArgs,null,null,null);
            cursor.moveToFirst();
            byte[] img=null;
            if(!cursor.isAfterLast()){
                int imageIndex=cursor.getColumnIndex("image");
                img=cursor.getBlob(imageIndex);
                cursor.close();
                if(img!=null) {
                    InputStream in=new ByteArrayInputStream(img);
                    bitmap=HandlePic.handlePic(context,in,0);
                    lruCache.put(id,bitmap);
                    return bitmap;
                }
            }
            img=HandleFolk.GetFolkImage(id);
            if(img==null)
                return null;
            InputStream in=new ByteArrayInputStream(img);
            ContentValues contentValues=new ContentValues();
            contentValues.put("id",id);
            contentValues.put("image",img);
            db= MySqliteHandler.INSTANCE.GetWritableDatabase();
            db.insert(table,null,contentValues);
            bitmap=HandlePic.handlePic(context,in,0);
            lruCache.put(id,bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null){
                ImageView imageView=(ImageView)listView.findViewWithTag(id);
                if(imageView!=null){
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }



}

