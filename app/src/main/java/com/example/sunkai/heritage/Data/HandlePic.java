package com.example.sunkai.heritage.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.io.InputStream;

/**
 * Created by 70472 on 2017/3/4.
 * 此类用于处理图片，包括压缩图片以及缩放图片
 *
 * HandlePic有两个重载方法，picID用于处理在src当中的已有的图片，InputStream用于处理将byte[]转换的从服务器中读取的图片
 * compressBitmapToFile用于将传入的bitmap压缩大小，width为w，height为h
 */

public class HandlePic {
    public static Bitmap handlePic(Context context, int picID,int size){
        InputStream is=context.getResources().openRawResource(picID);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=false;
        options.inSampleSize=size;
        Bitmap bitmap= BitmapFactory.decodeStream(is,null,options);
        return bitmap;
    }
    public static Bitmap handlePic(Context context, InputStream is,int size){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=false;
        options.inSampleSize=size;
        Bitmap bitmap= BitmapFactory.decodeStream(is,null,options);
        return bitmap;
    }
    public static Bitmap compressBitmapToFile(Bitmap bmp,int w,int h){
        int height;
        int width;
        if(bmp.getHeight()>bmp.getWidth()){
            height=w;
            width=h;
        }
        else{
            height=h;
            width=w;
        }
        Bitmap result = Bitmap.createBitmap(width, height , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, width, height);
        canvas.drawBitmap(bmp, null, rect, null);
        return result;
    }
}
