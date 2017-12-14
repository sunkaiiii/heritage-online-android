package com.example.sunkai.heritage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import com.example.sunkai.heritage.Data.HandlePic;
import com.example.sunkai.heritage.Data.MySqliteHandler;
import com.example.sunkai.heritage.Data.userCommentData;
import com.makeramen.roundedimageview.RoundedImageView;

import org.kobjects.base64.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.example.sunkai.heritage.LoginActivity.userID;

/**
 * Created by 70472 on 2017/3/4.
 * 此类是find页面listview的adpter
 */
public class findFragmentAdapter extends BaseAdapter{
    private Context context;
    private List<userCommentData> datas;
    private ListView listView=null;
//    Animation imageAnimation;
    int what;
    LruCache<Integer,Bitmap> lruCache;

    /**
     *
     * @param context 传入的context
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
        new GetInformation(this).execute();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(listView==null){
            listView=(ListView)parent;
        }
        Holder vh;
        LayoutInflater inflater= LayoutInflater.from(context);
        View view;
        if(convertView==null) {
            view = inflater.inflate(R.layout.fragment_find_listview_layout, null);
        }
        else{
            view=convertView;
        }
        vh=new Holder();
        vh.img=(ImageView)view.findViewById(R.id.fragment_find_litview_img);
        vh.comment=(TextView)view.findViewById(R.id.testview_comment);
        vh.like=(TextView)view.findViewById(R.id.textview_like);
        vh.likeImage=(ImageView)view.findViewById(R.id.imageView4);
        vh.commentImage=(ImageView)view.findViewById(R.id.fragment_find_comment);
        vh.addfocusImage=(ImageView)view.findViewById(R.id.add_focus_img);
        vh.addfocusText=(TextView) view.findViewById(R.id.add_focus_text);
        vh.name_text=(TextView)view.findViewById(R.id.name_text);
        vh.userImage=(RoundedImageView)view.findViewById(R.id.user_list_image);
        userCommentData data=datas.get(position);
        vh.img.setTag(data.id);
        Bitmap bitmap=lruCache.get(data.id);
        if(null!=bitmap) {
            vh.img.setImageBitmap(bitmap);
        }
        else{
            new GetCommentImage(data.id,this).execute();
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
        vh.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp);
        vh.userImage.setTag(data.user_id+" "+data.id);
        new GetUserImage(data.user_id,this,data.id).execute();
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
        return view;
    }

    private class Holder{
        ImageView img;
        TextView like,comment,addfocusText,name_text;
        ImageView likeImage,commentImage,addfocusImage;
        RoundedImageView userImage;
    }


    class imageButtonclick implements View.OnClickListener{
        boolean isUserLike=false;
        int commentID=0;
        private imageButtonclick(boolean isUserLike,int commentID){
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
                    new Handler(context.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg) {
                            notifyDataSetChanged();
                        }
                    }.sendEmptyMessage(0);
                }
                else{
                    for(int i=0;i<datas.size();i++){
                        getdatas.get(i).userImage=datas.get(i).userImage;
                        getdatas.get(i).isUserFocusUser=datas.get(i).isUserFocusUser;
                    }
                    datas=getdatas;
                    new Handler(context.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg) {
                            notifyDataSetChanged();
                        }
                    }.sendEmptyMessage(0);
                }
            }
        };

        Handler SetOrCancelLikeHandler=new Handler(context.getMainLooper()){
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

        Handler handler=new Handler(context.getMainLooper()){
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
        final Handler getReplyHandler=new Handler(context.getMainLooper()){
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
        new GetInformation(this).execute();
    }

    static class GetInformation extends AsyncTask<Void,Void,Void>{

        WeakReference<findFragmentAdapter> findFragmentAdapterWeakReference;

        private GetInformation(findFragmentAdapter adapter){
            findFragmentAdapterWeakReference=new WeakReference<findFragmentAdapter>(adapter);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            findFragmentAdapter adapter=findFragmentAdapterWeakReference.get();
            if(null==adapter)
                return null;
            final List<userCommentData> getdatas;
            if(adapter.what==1){
                getdatas= HandleFind.Get_User_Comment_Information(userID);
            }
            else if(adapter.what==2){
                getdatas= HandleFind.Get_User_Comment_Information_By_User(userID);
            }
            else if(adapter.what==3){
                getdatas=HandleFind.Get_User_Comment_Information_By_Own(userID);
            }
            else{
                getdatas=new ArrayList<>();
            }
            adapter.datas=getdatas;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            findFragmentAdapter adapter=findFragmentAdapterWeakReference.get();
            if(null==adapter)
                return;
            adapter.notifyDataSetChanged();
        }
    }
    static class GetCommentImage extends AsyncTask<Void,Void,Bitmap> {
        int id;
        WeakReference<findFragmentAdapter> findFragmentAdapterWeakReference;

        private GetCommentImage(int id,findFragmentAdapter findFragmentAdapter) {
            this.id = id;
            findFragmentAdapterWeakReference=new WeakReference<>(findFragmentAdapter);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            findFragmentAdapter findFragmentAdapter=findFragmentAdapterWeakReference.get();
            if(findFragmentAdapter==null)
                return null;
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
                Bitmap bitmap = HandlePic.handlePic(findFragmentAdapter.context, in, 0);
                findFragmentAdapter.lruCache.put(id, bitmap);
                cursor.close();
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
            Bitmap bitmap = HandlePic.handlePic(findFragmentAdapter.context, new ByteArrayInputStream(bytes), 0);
            findFragmentAdapter.lruCache.put(id, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            findFragmentAdapter findFragmentAdapter=findFragmentAdapterWeakReference.get();
            if(findFragmentAdapter==null)
                return;
            ImageView imageView=(ImageView)findFragmentAdapter.listView.findViewWithTag(id);
            imageView.setImageBitmap(bitmap);
        }
    }

    static class GetUserImage extends AsyncTask<Void,Void,Bitmap>{
        int id;
        int commandID;
        WeakReference<findFragmentAdapter> findFragmentAdapterWeakReference;
        private GetUserImage(int id,findFragmentAdapter adapter,int commandID){
            this.id=id;
            findFragmentAdapterWeakReference=new WeakReference<>(adapter);
            this.commandID=commandID;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            findFragmentAdapter adapter=findFragmentAdapterWeakReference.get();
            if(adapter==null)
                return null;
            SQLiteDatabase db=MySqliteHandler.INSTANCE.GetReadableDatabase();
            Cursor cursor;
            String table = "person_image";
            String selection = "imageID=?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            cursor = db.query(table, null, selection, selectionArgs, null, null, null);
            String result;
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                int imageIndex = cursor.getColumnIndex("image");
                byte[] image = cursor.getBlob(imageIndex);
                cursor.close();
                return HandlePic.handlePic(adapter.context, new ByteArrayInputStream(image), 0);

            }
            byte[] image;
            result = HandlePerson.Get_User_Image(id);
            image = Base64.decode(result);
            ContentValues contentValues = new ContentValues();
            contentValues.put("imageID", id);
            contentValues.put("image", image);
            db = MySqliteHandler.INSTANCE.GetWritableDatabase();
            db.insert(table, null, contentValues);
            return HandlePic.handlePic(adapter.context,new ByteArrayInputStream(image),0);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            findFragmentAdapter adapter=findFragmentAdapterWeakReference.get();
            if(bitmap==null||adapter==null)
                return;
            ImageView imageView=(ImageView)adapter.listView.findViewWithTag(id+" "+commandID);
            imageView.setImageBitmap(bitmap);
        }
    }
}