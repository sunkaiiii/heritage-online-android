package com.example.sunkai.heritage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleFind;
import com.example.sunkai.heritage.ConnectWebService.HandlePerson;
import com.example.sunkai.heritage.Data.FindActivityAllData;
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.MySqliteHandler;
import com.example.sunkai.heritage.Data.userCommentData;
import com.makeramen.roundedimageview.RoundedImageView;

import org.kobjects.base64.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.sunkai.heritage.LoginActivity.userID;
import static com.example.sunkai.heritage.value.HandlerValue.GET_INFO_DONE;

/**
 * Created by 70472 on 2017/3/4.
 * 此类是find页面listview的adpter
 */
public class findFragmentAdapter extends BaseAdapter{

    private Context context;
    private List<userCommentData> datas;
    private ListView listView=null;
    int[] apearCount;
//    Animation imageAnimation;
    int what;
    LruCache<Integer,Bitmap> lruCache;

    /**
     *
     * @param context
     * @param what what值不同代表着加载的页面不同，1为全部的帖子，2为已关注人的帖子，3为我的帖子
     */
    public findFragmentAdapter(Context context,int what){
        this.context=context;
//        imageAnimation= AnimationUtils.loadAnimation(context,R.anim.image_apear);
        this.what=what;
        int avilableMemory=(int)Runtime.getRuntime().maxMemory()/8;
        int cacheSzie=avilableMemory/4;
        lruCache=new LruCache<Integer,Bitmap>(cacheSzie){
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getByteCount();
            }
        };
        new Thread(getInformationThread).start();
    }
    public int getCount() {
        if(null!=datas)
            return datas.size();
        return 0;
    }
    public Object getItem(int position) {
        return datas.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public List<userCommentData> getDatas(){
        return this.datas;
    }
    public void resetApear(){
        apearCount=new int[datas.size()];
        for(int i=0;i<apearCount.length;i++){
            apearCount[i]=0;
        }
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if(listView==null){
            listView=(ListView)parent;
        }
        Holder vh;
        LayoutInflater inflater= LayoutInflater.from(context);
        convertView=inflater.inflate(R.layout.fragment_find_listview_layout,null);
        vh=new Holder();
        vh.img=(ImageView)convertView.findViewById(R.id.fragment_find_litview_img);
        vh.comment=(TextView)convertView.findViewById(R.id.testview_comment);
        vh.like=(TextView)convertView.findViewById(R.id.textview_like);
        vh.likeImage=(ImageView)convertView.findViewById(R.id.imageView4);
        vh.commentImage=(ImageView)convertView.findViewById(R.id.fragment_find_comment);
        vh.addfocusImage=(ImageView)convertView.findViewById(R.id.add_focus_img);
        vh.addfocusText=(TextView) convertView.findViewById(R.id.add_focus_text);
        vh.name_text=(TextView)convertView.findViewById(R.id.name_text);
        vh.userImage=(RoundedImageView)convertView.findViewById(R.id.user_list_image);
        userCommentData data=datas.get(position);
        vh.img.setTag(data.id);
        Bitmap bitmap=lruCache.get(data.id);
        if(null!=bitmap) {
            vh.img.setImageBitmap(bitmap);
        }
        else{
            new GetCommentImage(data.id).execute();
        }
        if(null!=apearCount&&apearCount[position]==0) {
//            vh.img.startAnimation(imageAnimation);
            apearCount[position]++;
        }
        if(datas.get(position).isUserLike){
            vh.like.setTextColor(Color.rgb(172,70,46));
            vh.likeImage.setImageResource(R.drawable.like_islike);
        }
        else{
            vh.like.setTextColor(Color.DKGRAY);
            vh.likeImage.setImageResource(R.drawable.good_unpress);
        }
        vh.like.setText(data.commentLikeNum);
        vh.comment.setText(data.commentReplyNum);
        vh.name_text.setText(data.userName);
        if(what==3){
            vh.like.setVisibility(View.GONE);
            vh.comment.setVisibility(View.INVISIBLE);
            vh.commentImage.setVisibility(View.INVISIBLE);
            vh.likeImage.setVisibility(View.INVISIBLE);
            vh.likeImage.setVisibility(View.INVISIBLE);
            vh.name_text.setText(data.commentTitle);
        }
        imageButtonclick likeClick=new imageButtonclick(data.isUserLike,data.id);
        vh.likeImage.setOnClickListener(likeClick);
        vh.like.setOnClickListener(likeClick);
        if(what==2){
            vh.addfocusImage.setVisibility(View.GONE);
            vh.addfocusText.setVisibility(View.GONE);
        }
        if(null==data.userImage){
            vh.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp);
        }
        else{
            InputStream in=new ByteArrayInputStream(data.userImage);
            Bitmap bitmap2=HandlePic.handlePic(context,in,0);
            vh.userImage.setImageBitmap(bitmap2);
        }
        if(data.user_id== userID){
            vh.addfocusText.setVisibility(View.INVISIBLE);
            vh.addfocusImage.setVisibility(View.INVISIBLE);
        }
        else{
            addFocusButtonClick addFocusButtonClick=new addFocusButtonClick(position);
            vh.addfocusText.setOnClickListener(addFocusButtonClick);
            vh.addfocusImage.setOnClickListener(addFocusButtonClick);
            if(data.isUserFocusUser){
                vh.addfocusText.setText("已关注");
                vh.addfocusText.setTextColor(Color.rgb(184,184,184));
                vh.addfocusImage.setImageResource(R.drawable.ic_remove_circle_grey_400_24dp);
            }
            else{

                vh.addfocusText.setText("加关注");
                vh.addfocusImage.setImageResource(R.drawable.ic_add_circle_black_24dp);
            }
        }
        return convertView;
    }
    class Holder{
        ImageView img;
        TextView like,comment,addfocusText,name_text;
        ImageView likeImage,commentImage,addfocusImage;
        RoundedImageView userImage;
    }


    class imageButtonclick implements View.OnClickListener{
        boolean isUserLike=false;
        int commentID=0;
        public imageButtonclick(boolean isUserLike,int commentID){
            this.isUserLike=isUserLike;
            this.commentID=commentID;
        }
        public void onClick(View v){
            switch (v.getId()){
                case R.id.textview_like:
                case R.id.imageView4:
                    SetOrCancelLike();
                    break;
                default:
                    break;
            }
        }
        private void SetOrCancelLike(){
            if(userID==0){
                Toast.makeText(context,"没有登录",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,LoginActivity.class);
                intent.putExtra("isInto",1);
                context.startActivity(intent);
                return;
            }
            if(isUserLike){
                new Thread(cancelLike).start();
            }
            else{
                new Thread(setLike).start();
            }
        }
        Runnable setLike=new Runnable() {
            @Override
            public void run() {
                boolean result=HandleFind.Set_User_Like(userID,commentID);
                if(result){
                    SetOrCancelLikeHandler.sendEmptyMessage(1);
                }
                else{
                    SetOrCancelLikeHandler.sendEmptyMessage(0);
                }
            }
        };
        Runnable cancelLike=new Runnable() {
            @Override
            public void run() {
                boolean result=HandleFind.Cancel_User_Like(userID,commentID);
                if(result){
                    SetOrCancelLikeHandler.sendEmptyMessage(1);
                }
                else{
                    SetOrCancelLikeHandler.sendEmptyMessage(0);
                }
            }
        };
        Runnable getInformationwithNoImage=new Runnable() {
            @Override
            public void run() {
                final List<userCommentData> getdatas= HandleFind.Get_User_Comment_Information(userID);
                if(null==getdatas){
                    new getInformationHandler(findFragmentAdapter.this).sendEmptyMessage(0);
                }
                else{
                    for(int i=0;i<datas.size();i++){
                        getdatas.get(i).userImage=datas.get(i).userImage;
                        getdatas.get(i).isUserFocusUser=datas.get(i).isUserFocusUser;
                    }
                    datas=getdatas;
                    new getInformationHandler(findFragmentAdapter.this).sendEmptyMessage(2);
                }
            }
        };

        Handler SetOrCancelLikeHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==1){
                    new Thread(getInformationwithNoImage).start();
                    notifyDataSetChanged();
                }
                else{
                    Toast.makeText(context,"出现错误，请稍后再试",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
    class addFocusButtonClick implements View.OnClickListener{
        userCommentData data;
        int position;
        addFocusButtonClick(int position){
            this.position=position;
            this.data=datas.get(position);
        }

        @Override
        public void onClick(View v) {
            if(userID==0){
                Toast.makeText(context,"没有登录",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,LoginActivity.class);
                intent.putExtra("isInto",1);
                context.startActivity(intent);
                return;
            }
            if(data.isUserFocusUser){
                new Thread(cancelFocus).start();
            }
            else{
                new Thread(addFocus).start();
            }
        }
        Runnable addFocus=new Runnable() {
            @Override
            public void run() {
                boolean result= HandlePerson.Add_Focus(userID,data.user_id);
                if(result){
                    datas.get(position).isUserFocusUser=true;
                    data.isUserFocusUser=true;
                }
                for(int i=0;i<datas.size();i++){
                    if(datas.get(i).user_id==data.user_id){
                        datas.get(i).isUserFocusUser=true;
                    }
                }
                handler.sendEmptyMessage(1);
            }
        };

        Runnable cancelFocus=new Runnable() {
            @Override
            public void run() {
                boolean result=HandlePerson.Cancel_Focus(userID,data.user_id);
                if(result){
                    datas.get(position).isUserFocusUser=false;
                    data.isUserFocusUser=false;
                }
                for(int i=0;i<datas.size();i++){
                    if(datas.get(i).user_id==data.user_id){
                        datas.get(i).isUserFocusUser=false;
                    }
                }

                handler.sendEmptyMessage(2);
            }
        };

        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
                if(msg.what==1) {
                    Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show();
                }
                else if(msg.what==2){
                    Toast.makeText(context,"取消关注成功",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
    public void getReplyCount(final int commentID,final int position){
        final Handler getReplyHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what>0){
                    datas.get(position).commentReplyNum=String.valueOf(msg.what);
                    notifyDataSetChanged();
                }
            }
        };
        Runnable getReplyCount=new Runnable() {
            @Override
            public void run() {
                int count=HandleFind.Get_User_Comment_Count(commentID);
                getReplyHandler.sendEmptyMessage(count);
            }
        };
        new Thread(getReplyCount).start();
    }

    public void reFreshList(){
        new Thread(getInformationThread).start();
    }


    Runnable getInformationThread=new Runnable() {
        @Override
        public void run() {
            final List<userCommentData> getdatas;
            if(what==1){
                getdatas= HandleFind.Get_User_Comment_Information(userID);
            }
            else if(what==2){
                getdatas= HandleFind.Get_User_Comment_Information_By_User(userID);
            }
            else if(what==3){
                getdatas=HandleFind.Get_User_Comment_Information_By_Own(userID);
            }
            else{
                getdatas=new ArrayList<>();
            }
            if(null==getdatas){
                new getInformationHandler(findFragmentAdapter.this).sendEmptyMessage(0);
            }
            else{
                datas=getdatas;
                new getInformationHandler(findFragmentAdapter.this).sendEmptyMessage(GET_INFO_DONE);
            }
        }
    };

    class GetCommentImage extends AsyncTask<Void,Void,Bitmap> {
        int id;

        public GetCommentImage(int id) {
            this.id = id;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            SQLiteDatabase db = MySqliteHandler.INSTANCE.GetReadableDatabase();
            String table = "find_comment_image";
            String selection = "imageID=?";
            Cursor cursor;
            String[] selectionArgs = new String[]{String.valueOf(id)};
            cursor = db.query(table, null, selection, selectionArgs, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                int imageIndex = cursor.getColumnIndex("image");
                byte[] img = cursor.getBlob(imageIndex);
                InputStream in = new ByteArrayInputStream(img);
                Bitmap bitmap = HandlePic.handlePic(context, in, 0);
                lruCache.put(id, bitmap);
                return bitmap;
            }
            byte[] bytes = HandleFind.Get_User_Comment_Image(id);
            if (bytes == null)
                return null;
            ContentValues contentValues = new ContentValues();
            contentValues.put("imageID", id);
            contentValues.put("image", bytes);
            db = MySqliteHandler.INSTANCE.GetWritableDatabase();
            db.insert(table, null, contentValues);
            Bitmap bitmap = HandlePic.handlePic(context, new ByteArrayInputStream(bytes), 0);
            lruCache.put(id, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView=(ImageView)listView.findViewWithTag(id);
            imageView.setImageBitmap(bitmap);
        }
    }

    Runnable getUserImage=new Runnable() {
        @Override
        public void run() {
            SQLiteDatabase db=MySqliteHandler.INSTANCE.GetReadableDatabase();
            Cursor cursor;
            for(int i=0;i<datas.size();i++){
                String table="person_image";
                String selection="imageID=?";
                String[] selectionArgs=new String[]{String.valueOf(datas.get(i).user_id)};
                cursor=db.query(table,null,selection,selectionArgs,null,null,null);
                String result=null;
                String serverUpdateTime=HandlePerson.Get_User_Update_Time(datas.get(i).user_id);
                int ok=-1;
                cursor.moveToFirst();
                if(!cursor.isAfterLast()){
                    int updateTime=cursor.getColumnIndex("update_time");
                    String localUpdateTime=cursor.getString(updateTime);
                    if(localUpdateTime!=null&&localUpdateTime.equals(serverUpdateTime)){
                        result="success";
                        int imageIndex=cursor.getColumnIndex("image");
                        byte[] image=cursor.getBlob(imageIndex);
                        datas.get(i).userImage=image;
                        ok=1;
                    }
                    else{
                        ok=0;
                    }
                }
                cursor.close();
                if(ok==-1){
                    result = HandlePerson.Get_User_Image(datas.get(i).user_id);
                    if(null==result||"Error".equals(result)){
                       continue;
                    }
                    byte[] image=Base64.decode(result);
                    datas.get(i).userImage= image;
                    ContentValues contentValues=new ContentValues();
                    contentValues.put("imageID",datas.get(i).user_id);
                    contentValues.put("image",image);
                    contentValues.put("update_time",serverUpdateTime);
                    db=MySqliteHandler.INSTANCE.GetWritableDatabase();
                    db.insert(table,null,contentValues);
                }
                else if(ok==0){
                    result = HandlePerson.Get_User_Image(datas.get(i).user_id);
                    if(null==result||"Error".equals(result)){
                        continue;
                    }
                    byte[] image=Base64.decode(result);
                    datas.get(i).userImage= image;
                    ContentValues contentValues=new ContentValues();
                    contentValues.put("update_time",serverUpdateTime);
                    contentValues.put("image",image);
                    db= MySqliteHandler.INSTANCE.GetWritableDatabase();
                    String[] wheres={String.valueOf(datas.get(i).user_id)};
                    db.update(table,contentValues,"imageID=?",wheres);
                }
                if(null==result||"Error".equals(result)){
                    getUserImageHandler.sendEmptyMessage(0);
                }
                else{
                    getUserImageHandler.sendEmptyMessage(1);
                }
            }
//            db.close();
        }
    };
    Handler getUserImageHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                notifyDataSetChanged();
            }
        }
    };
}
class getInformationHandler extends Handler{
    private findFragmentAdapter adapter;
    public getInformationHandler(findFragmentAdapter adapter){
        super(Looper.getMainLooper());
        this.adapter=adapter;
    }
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case GET_INFO_DONE:
                adapter.notifyDataSetChanged();
            default:
                break;
        }
    }
}