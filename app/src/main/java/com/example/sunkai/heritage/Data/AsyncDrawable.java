package com.example.sunkai.heritage.Data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by sunkai on 2017/3/5.
 * 废弃的异步加载首页图片的类
 */

public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskWeakReference;
    public AsyncDrawable(Resources res,Context context, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
        super(res,bitmap);
        bitmapWorkerTaskWeakReference=new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
    }
    public BitmapWorkerTask getBitmapWorkerTask(){
        return bitmapWorkerTaskWeakReference.get();
    }
}
