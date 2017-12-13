package com.example.sunkai.heritage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment;
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.MySqliteHandler;
import com.example.sunkai.heritage.Data.classifyActiviyData;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by sunkai on 2017/3/3.
 * 首页Listview的Adpter
 */

public class activityListviewAdapter extends BaseAdapter {
    private Context context;
    private List<classifyActiviyData> activityDatas;
    private String channel;
    Animation imageAnimation;//图片出现动画

    ListView thisListView;
    LruCache<Integer,Bitmap> lruCache;

    /**
     *
     * @param context
     * @param activityDatas 由首页getCount出来加入了相应数量的空类的List
     * @param channel   传入的类别，viewpaer的位置不同此值不同，用于加载不同的分类内容
     */
    public activityListviewAdapter(Context context,List<classifyActiviyData> activityDatas,String channel){
        this.context=context;
        this.activityDatas=null;
        this.channel=channel;
        int maxMemory=(int)Runtime.getRuntime().maxMemory();
        int avilableMemory=maxMemory/32;
        lruCache=new LruCache<Integer,Bitmap>(avilableMemory){
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        imageAnimation= AnimationUtils.loadAnimation(context,R.anim.image_apear);
        new getChannelInformation().execute();
    }
    public int getCount() {
        if(null!=activityDatas)
            return activityDatas.size();
        return 0;
    }
    public Object getItem(int position) {
        return activityDatas.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if(thisListView==null)
            thisListView=(ListView)parent;
        Holder vh;
        if(null==convertView){
            LayoutInflater inflater= LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.activity_layout,null);
            vh=new Holder();
            vh.img=(ImageView)convertView.findViewById(R.id.activity_layout_img);
            vh.textView=(TextView)convertView.findViewById(R.id.activity_layout_text);
            convertView.setTag(vh);
        }
        else{
            vh=(Holder)convertView.getTag();
        }
        classifyActiviyData data=activityDatas.get(position);
        String text=data.activitContent;
        vh.textView.setText(text);
        vh.img.setTag(data.id);
        vh.img.setImageResource(R.drawable.empty_background);
        Bitmap bitmap=lruCache.get(data.id);
        if(bitmap!=null){
            vh.img.setImageBitmap(bitmap);
        }
        else{
            new getChannelImage(data.id).execute();
        }
        return convertView;
    }
    private class Holder{
        ImageView img;
        TextView textView;
    }

    class getChannelInformation extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            activityDatas=HandleMainFragment.GetChannelInformation(channel);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }


    class getChannelImage extends AsyncTask<Void,Void,Bitmap>{
        SQLiteDatabase db= MySqliteHandler.INSTANCE.GetReadableDatabase();
        String table="channel_activity_image";
        String selection="imageID=?";
        int id;
        public getChannelImage(int id){
            this.id=id;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap=null;
            String[] selectionArgs=new String[]{String.valueOf(id)};
            Cursor cursor=db.query(table,null,selection,selectionArgs,null,null,null);
            cursor.moveToFirst();
            byte[] imgByte=null;
            if(!cursor.isAfterLast()) {
                int image = cursor.getColumnIndex("image");
                imgByte = cursor.getBlob(image);
                cursor.close();
                if (imgByte != null) {
                    InputStream in = new ByteArrayInputStream(imgByte);
                    bitmap=HandlePic.handlePic(context, in, 0);
                    lruCache.put(id,bitmap);
                    return bitmap;
                }
            }
            imgByte=HandleMainFragment.GetChannelImage(id);
            if(imgByte==null)
                return null;
            ContentValues contentValues=new ContentValues();
            contentValues.put("imageID",id);
            contentValues.put("image",imgByte);
            db=MySqliteHandler.INSTANCE.GetWritableDatabase();
            db.insert("channel_activity_image",null,contentValues);
            InputStream in=new ByteArrayInputStream(imgByte);
            bitmap=HandlePic.handlePic(context,in,0);
            lruCache.put(id,bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null){
                ImageView imageView=(ImageView)thisListView.findViewWithTag(id);
                if(imageView!=null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.startAnimation(imageAnimation);
                }
            }
        }
    }
}
