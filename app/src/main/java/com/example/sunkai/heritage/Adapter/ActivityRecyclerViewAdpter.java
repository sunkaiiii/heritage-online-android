package com.example.sunkai.heritage.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment;
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.MySqliteHandler;
import com.example.sunkai.heritage.Data.ClassifyActiviyData;
import com.example.sunkai.heritage.Interface.OnItemClickListener;
import com.example.sunkai.heritage.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by sunkai on 2017/12/22.
 */

public class ActivityRecyclerViewAdpter extends RecyclerView.Adapter<ActivityRecyclerViewAdpter.ViewHolder> implements View.OnClickListener{
    private Context context;
    private List<ClassifyActiviyData> activityDatas;
    private String channel;
    Animation imageAnimation;//图片出现动画

    RecyclerView thisRecyclerView;
    LruCache<Integer,Bitmap> lruCache;
    private OnItemClickListener mOnItemClickListener=null;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView textView;
        View view;
        ViewHolder(View view){
            super(view);
            this.view=view;
            initView();
        }
        private void initView(){
            img=(ImageView)view.findViewById(R.id.activity_layout_img);
            textView=(TextView)view.findViewById(R.id.activity_layout_text);
        }
    }

    public ActivityRecyclerViewAdpter(Context context,String channel){
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(thisRecyclerView==null)
            thisRecyclerView=(RecyclerView)parent;
        View view= LayoutInflater.from(context).inflate(R.layout.activity_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        ClassifyActiviyData data=activityDatas.get(position);
        String text=data.getActivityContent();
        holder.textView.setText(text);
        holder.img.setImageResource(R.drawable.empty_background);
        Bitmap bitmap=lruCache.get(data.getId());
        if(bitmap!=null){
            holder.img.setImageBitmap(bitmap);
        }
        else{
            new getChannelImage(data.getId(),holder.img,this).execute();
        }
    }

    @Override
    public int getItemCount() {
        if(activityDatas==null)
            return 0;
        return activityDatas.size();
    }

    public ClassifyActiviyData getItem(int position){
        return activityDatas.get(position);
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public void setOnItemClickListen(OnItemClickListener listenr){
        this.mOnItemClickListener=listenr;
    }

    class getChannelInformation extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            activityDatas= HandleMainFragment.GetChannelInformation(channel);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }


    static class getChannelImage extends AsyncTask<Void,Void,Bitmap>{
        SQLiteDatabase db= MySqliteHandler.INSTANCE.GetReadableDatabase();
        String table="channel_activity_image";
        String selection="imageID=?";
        int id;
        WeakReference<ImageView> weakReferenceImageView;
        WeakReference<ActivityRecyclerViewAdpter> weakReference;
        getChannelImage(int id, ImageView imageView,ActivityRecyclerViewAdpter adpter){
            this.id=id;
            weakReferenceImageView=new WeakReference<>(imageView);
            weakReference=new WeakReference<>(adpter);
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            ActivityRecyclerViewAdpter adpter=weakReference.get();
            if(adpter==null)
                return null;
            Bitmap bitmap;
            String[] selectionArgs=new String[]{String.valueOf(id)};
            Cursor cursor=db.query(table,null,selection,selectionArgs,null,null,null);
            cursor.moveToFirst();
            byte[] imgByte;
            if(!cursor.isAfterLast()) {
                int image = cursor.getColumnIndex("image");
                imgByte = cursor.getBlob(image);
                cursor.close();
                if (imgByte != null) {
                    InputStream in = new ByteArrayInputStream(imgByte);
                    bitmap= HandlePic.handlePic(adpter.context, in, 0);
                    adpter.lruCache.put(id,bitmap);
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
            bitmap=HandlePic.handlePic(adpter.context,in,0);
            adpter.lruCache.put(id,bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null){
                ActivityRecyclerViewAdpter adpter=weakReference.get();
                ImageView imageView=weakReferenceImageView.get();
                if(imageView!=null&&adpter!=null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.startAnimation(adpter.imageAnimation);
                }
            }
        }
    }
}