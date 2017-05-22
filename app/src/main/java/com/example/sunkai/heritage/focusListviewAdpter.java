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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandlePerson;
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.focusData;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by sunkai on 2017-5-2.
 */

public class focusListviewAdpter extends BaseAdapter {
    List<focusData> datas;
    Context context;
    int what;

    /**
     *
     * @param context
     * @param datas 关注、粉丝的数据
     * @param what  1为关注，2为粉丝3为查询页面
     */
    focusListviewAdpter(Context context,List<focusData> datas,int what){
        this.context=context;
        this.datas=datas;
        this.what=what;
        if(what==2){
            for(int i=0;i<datas.size();i++){
                datas.get(i).isCheck=false;
            }
        }
        new Thread(checkFolloweachOther).start();
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
    public View getView(final int position, View convertView, ViewGroup parent){
        Holder vh;
        if(convertView==null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.focus_listview_layout, null);
            vh = new Holder();
            vh.userName = (TextView) convertView.findViewById(R.id.user_name);
            vh.userIntrodeuce = (TextView) convertView.findViewById(R.id.user_introduce);
            vh.userImage = (RoundedImageView) convertView.findViewById(R.id.user_head_image);
            vh.focusBtn = (Button) convertView.findViewById(R.id.focus_btn);
            convertView.setTag(vh);
        }
        else
        {
            vh=(Holder) convertView.getTag();
        }
        final focusData data=datas.get(position);
        String userName=data.name;
        vh.userName.setText(userName);
        if(null==data.userImage){
            vh.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp);
        }
        else{
            InputStream in=new ByteArrayInputStream(data.userImage);
            Bitmap bitmap= HandlePic.handlePic(context,in,0);
            vh.userImage.setImageBitmap(bitmap);
        }
        if(data.followeachother){
            vh.focusBtn.setText("互相关注");
        }
        else{
            if(data.isCheck){
                vh.focusBtn.setText("已关注");
            }
            else{
                vh.focusBtn.setText("未关注");
            }
        }
        final Button btn=vh.focusBtn;
        /**
         * 在点击关注、取关的时候，页面文字改变，提示用户正在响应，并禁止按钮点击以防止错误的发生
         */
        vh.focusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFocus handleFocus=new handleFocus(data,position,btn);
                btn.setText("操作中");
                btn.setEnabled(false);
                if(data.isCheck){
                    handleFocus.CancelFollow();
                }
                else{
                    handleFocus.AddFollow();
                }
            }
        });
        return convertView;
    }
    class Holder{
        TextView userName,userIntrodeuce;
        RoundedImageView userImage;
        Button focusBtn;
    }
    class handleFocus{
        focusData data;
        int position;
        Button btn;
        handleFocus(focusData data,int position,Button btn){
            this.data=data;
            this.position=position;
            this.btn=btn;
        }

        /**
         * 关注、取关
         */
        void CancelFollow(){
            new Thread(CancelFollow).start();
        }
        void AddFollow(){
            new Thread(AddFollow).start();
        }
        Runnable AddFollow=new Runnable() {
            @Override
            public void run() {
                boolean result=false;
                if(what==1||what==3) {
                    result = HandlePerson.Add_Focus(data.focusUserid, data.focusFansID);
                }
                else if(what==2){
                    result=HandlePerson.Add_Focus(data.focusFansID,data.focusUserid);
                }
                if(result){
                    AddFollowHandler.sendEmptyMessage(1);
                }
                else{
                    AddFollowHandler.sendEmptyMessage(0);
                }
            }
        };
        Runnable CancelFollow=new Runnable() {
            @Override
            public void run() {
                boolean result=false;
                if(what==1||what==3) {
                    result = HandlePerson.Cancel_Focus(data.focusUserid, data.focusFansID);
                }
                else if(what==2){
                    result = HandlePerson.Cancel_Focus(data.focusFansID,data.focusUserid);
                }
                if(result){
                    CancelFollowHandler.sendEmptyMessage(1);
                }
                else{
                    CancelFollowHandler.sendEmptyMessage(0);
                }
            }
        };
        Handler CancelFollowHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                btn.setEnabled(true);
                if(msg.what==1){
                    datas.get(position).isCheck=false;
                    Toast.makeText(context,"取消关注成功",Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    /**
                     * 用户取关成功，发送广播给personFragment，使其重新加载粉丝、关注数据
                     */
                    datas.get(position).followeachother=false;
                    Intent intent=new Intent("android.intent.action.focusAndFansCountChange");
                    intent.putExtra("message","change");
                    context.sendBroadcast(intent);
//                    new Thread(checkFolloweachOther).start();
                }
                else{
                    Toast.makeText(context,"操作失败，请稍后再试",Toast.LENGTH_SHORT).show();
                }
            }
        };
        Handler AddFollowHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                btn.setEnabled(true);
                if(msg.what==1){
                    datas.get(position).isCheck=true;
                    Toast.makeText(context,"关注成功",Toast.LENGTH_SHORT).show();
                    new Thread(checkFolloweachOther).start();
                    /**
                     * 用户关注成功，发送广播给personFragment，使其重新加载粉丝、关注数据
                     * 因为是关注用户，重新运行查看互关进程，判断是否为互相关注
                     */
                    notifyDataSetChanged();
                    Intent intent=new Intent("android.intent.action.focusAndFansCountChange");
                    intent.putExtra("message","change");
                    context.sendBroadcast(intent);
                }
                else{
                    Toast.makeText(context,"操作失败，请稍后再试",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
    Runnable checkFolloweachOther=new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<datas.size();i++){
                focusData data=datas.get(i);
                datas.get(i).followeachother= HandlePerson.Check_Follow_Eachohter(data.focusUserid,data.focusFansID);
                if(datas.get(i).followeachother&&(what==2||what==3)){
                    datas.get(i).isCheck=true;
                }
                checkFolloweachotherHandler.sendEmptyMessage(1);
            }
        }
    };
    Handler checkFolloweachotherHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            notifyDataSetChanged();
            if(what==3){
                new Thread(isUserFollow).start();
            }
        }
    };

    Runnable isUserFollow=new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<datas.size();i++){
                focusData data=datas.get(i);
                datas.get(i).isCheck=HandlePerson.is_User_Follow(data.focusUserid,data.focusFansID);
            }
            isUserFollowHandler.sendEmptyMessage(1);
        }
    };
    Handler isUserFollowHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            notifyDataSetChanged();
        }
    };
}
