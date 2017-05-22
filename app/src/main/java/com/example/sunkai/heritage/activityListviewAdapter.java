package com.example.sunkai.heritage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment;
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.classifyActiviyData;

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
    Bitmap[] bitmap;
    int[] apearCount;//图片出现次数计数器

    /**
     *
     * @param context
     * @param activityDatas 由首页getCount出来加入了相应数量的空类的List
     * @param channel   传入的类别，viewpaer的位置不同此值不同，用于加载不同的分类内容
     */
    public activityListviewAdapter(Context context,List<classifyActiviyData> activityDatas,String channel){
        this.context=context;
        this.activityDatas=activityDatas;
        this.channel=channel;
        /**
         * 加载图片动画，在图片第一次加载完成之后播放一次淡出的动画
         * 初始化完成之后开启新线程获取活动信息
         */
        bitmap=new Bitmap[activityDatas.size()];
        imageAnimation= AnimationUtils.loadAnimation(context,R.anim.image_apear);
        apearCount=new int[activityDatas.size()];
        for(int i=0;i<apearCount.length;i++){
            apearCount[i]=0;
        }
        new Thread(getChannelInformation).start();
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
//        int img=data.imgID;
        vh.textView.setText(text);
        vh.img.setImageBitmap(bitmap[position]);
        if(apearCount[position]==1) {
            vh.img.startAnimation(imageAnimation);
            apearCount[position]++;
        }
        return convertView;
    }
    private class Holder{
        ImageView img;
        TextView textView;
    }

//    @本地测试数据
// Runnable handlePic=new Runnable() {
//        @Override
//        public void run() {
//            for(int i=0;i<activityDatas.size();i++){
//                bitmap[i]=HandlePic.handlePic(context,activityDatas.get(i).imgID,3);
//                Message msg=new Message();
//                msg.what=1;
//                handler.sendMessage(msg);
//            }
//            Message msg=new Message();
//            msg.what=1;
//            handler.sendMessage(msg);
//        }
//    };
//    Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg){
//            if(msg.what==1){
//                notifyDataSetChanged();
//            }
//        }
//    };


    Runnable getChannelInformation=new Runnable() {
        @Override
        public void run() {
            activityDatas= HandleMainFragment.GetChannelInformation(channel);
            Message msg=new Message();
            getChannelInformationHandler.sendMessage(msg);
        }
    };

    Handler getChannelInformationHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            notifyDataSetChanged();
            /**
             * 在加载完文字信息之后再加载图片
             */
            new Thread(getChannelImage).start();
        }
    };

    Runnable getChannelImage=new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<activityDatas.size();i++){
                byte[] imgByte=HandleMainFragment.GetChannelImage(activityDatas.get(i).id);
                Message msg=new Message();
                if(null==imgByte){
                    msg.what=0;
                    getChannelImageHandler.handleMessage(msg);
                }
                else {
                    activityDatas.get(i).activityImage = imgByte;
                    InputStream is = new ByteArrayInputStream(imgByte);
                    bitmap[i] = HandlePic.handlePic(context, is, 0);
                    apearCount[i]++;
                    msg.what = 1;
                    getChannelImageHandler.sendMessage(msg);
                }
            }
        }
    };
    Handler getChannelImageHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                notifyDataSetChanged();
            }
        }
    };
}
