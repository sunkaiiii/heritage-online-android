package com.example.sunkai.heritage.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by sunkai on 2017/3/5.
 * 废弃的用于异步加载首页内容的类
 */

public class BitmapWorkerTask extends AsyncTask<Integer,Void,Bitmap> {
    private final WeakReference<ImageView> imageViewWeakReference;
    public int data=0;
    private Context context;
    public BitmapWorkerTask(ImageView imageView,Context context){
        this.context=context;
        imageViewWeakReference=new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Integer... params){
        data=params[0];
        return HandlePic.handlePic(context,data,0);
    }
//    @Override
//    protected void onPostExecute(Bitmap bitmap){
//        if(null!=imageViewWeakReference&&null!=bitmap){
//            final ImageView imageView=imageViewWeakReference.get();
//            if(imageView!=null){
//                imageView.setImageBitmap(bitmap);
//            }
//        }
//    }
    @Override
    protected void onPostExecute(Bitmap bitmap){
//        if(isCancelled()){
//            bitmap=null;
//        }
        if(imageViewWeakReference!=null&&bitmap!=null){
            final ImageView imageView=imageViewWeakReference.get();
            final BitmapWorkerTask bitmapWorkerTask=getBitmapWorkerTask(imageView);
            if(this==bitmapWorkerTask&&imageView!=null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView){
        if(null!=imageView){
            final Drawable drawable=imageView.getDrawable();
            if(drawable instanceof AsyncDrawable){
                final AsyncDrawable asyncDrawable=(AsyncDrawable)drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}
