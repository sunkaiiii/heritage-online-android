package com.example.sunkai.heritage;

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import android.support.design.widget.FloatingActionButton;
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
    Animation imageAnimation;
    int what;
    LruCache<Integer,Bitmap> lruCache;

    /**
     *
     * @param context 传入的context
     * @param what what值不同代表着加载的页面不同，1为全部的帖子，2为已关注人的帖子，3为我的帖子
     */
    public findFragmentAdapter(Context context, int what){
        this.context=context;
        imageAnimation= AnimationUtils.loadAnimation(context,R.anim.image_apear);
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
    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        if(listView==null){
            listView=(ListView)parent;
        }
        Holder vh;
        LayoutInflater inflater= LayoutInflater.from(context);
        View view;
        if(convertView==null) {
            view = inflater.inflate(R.layout.fragment_find_listview_layout, null);
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
            view.setTag(vh);
        }
        else{
            view=convertView;
            vh=(Holder)view.getTag();
        }
        userCommentData data=datas.get(position);
        GetCommentImage(data,vh.img);
        GetUserImage(data);
        if(datas.get(position).getUserLike()){
           SetLike(vh.like,vh.likeImage,Integer.parseInt(data.getCommentLikeNum()));
        }
        else{
            CancelLike(vh.like,vh.likeImage,Integer.parseInt(data.getCommentLikeNum()));
        }
        vh.comment.setText(data.getCommentReplyNum());
        vh.name_text.setText(data.getUserName());
        if(what==2){
            vh.addfocusImage.setVisibility(View.GONE);
            vh.addfocusText.setVisibility(View.GONE);
        }
        if(what==3){
            hideSomeElement(vh,data);
        }
        imageButtonclick likeClick=new imageButtonclick(data.getId(),position,vh.likeImage,vh.like);
        vh.likeImage.setOnClickListener(likeClick);
        vh.like.setOnClickListener(likeClick);
        vh.userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp);
        vh.userImage.setTag(data.getUser_id()+" "+data.getId());
        if(data.getUser_id()== userID){
            vh.addfocusText.setVisibility(View.INVISIBLE);
            vh.addfocusImage.setVisibility(View.INVISIBLE);
        }
        else{
            addFocusButtonClick addFocusButtonClick=new addFocusButtonClick(position);
            vh.addfocusText.setOnClickListener(addFocusButtonClick);
            vh.addfocusImage.setOnClickListener(addFocusButtonClick);
            if(data.getUserFocusUser()){
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

    private boolean changeLikeImageState(boolean isLike,ImageView imageView,TextView textView,int position)
    {
        int likeNumber=Integer.parseInt(datas.get(position).getCommentLikeNum());
        likeNumber=isLike?likeNumber+1:likeNumber-1;
        datas.get(position).setCommentLikeNum(String.valueOf(likeNumber));
        return isLike?SetLike(textView,imageView,likeNumber):CancelLike(textView,imageView,likeNumber);
    }
    private boolean SetLike(TextView textView,ImageView imageView,int count) {
        textView.setText(String.valueOf(count));
        textView.setTextColor(Color.rgb(172,70,46));
        imageView.setImageResource(R.drawable.like_islike);
        return true;
    }
    private boolean CancelLike(TextView textView,ImageView imageView,int count){
        textView.setText(String.valueOf(count));
        textView.setTextColor(Color.DKGRAY);
        imageView.setImageResource(R.drawable.good_unpress);
        return true;
    }
    private void GetCommentImage(userCommentData data,ImageView imageView){
        Bitmap bitmap=lruCache.get(data.getId());
        imageView.setTag(data.getId());
        if(null!=bitmap) {
            imageView.setImageBitmap(bitmap);
        }
        else{
            new GetCommentImage(data.getId(),this).execute();
        }
    }
    private void GetUserImage(userCommentData data){
        new GetUserImage(data.getUser_id(),this,data.getId()).execute();
    }
    private void hideSomeElement(Holder vh,userCommentData data){
        vh.like.setVisibility(View.GONE);
        vh.comment.setVisibility(View.INVISIBLE);
        vh.commentImage.setVisibility(View.INVISIBLE);
        vh.likeImage.setVisibility(View.INVISIBLE);
        vh.likeImage.setVisibility(View.INVISIBLE);
        vh.name_text.setText(data.getCommentTitle());
    }
    private void login(){
        Toast.makeText(context,"没有登录",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(context,LoginActivity.class);
        intent.putExtra("isInto",1);
        context.startActivity(intent);
    }


    private class Holder{
        ImageView img;
        TextView like,comment,addfocusText,name_text;
        ImageView likeImage,commentImage,addfocusImage;
        RoundedImageView userImage;
    }


    class imageButtonclick implements View.OnClickListener{
        int commentID;
        int position;
        WeakReference<ImageView> imageViewWeakReference;
        WeakReference<TextView> textViewWeakReference;
        private final int LIKE=1;
        private final int CANCEL=2;
        private final int ERROR=0;
        private imageButtonclick(int commentID,int position,ImageView imageView,TextView textView){
            this.commentID=commentID;
            this.position=position;
            imageViewWeakReference=new WeakReference<>(imageView);
            textViewWeakReference=new WeakReference<>(textView);
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
                login();
                return;
            }
            if(datas.get(position).getUserLike()){
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
                    SetOrCancelLikeHandler.sendEmptyMessage(LIKE);
                }
                else{
                    SetOrCancelLikeHandler.sendEmptyMessage(ERROR);
                }
            }
        };
        Runnable cancelLike=new Runnable() {
            @Override
            public void run() {
                boolean result=HandleFind.Cancel_User_Like(userID,commentID);
                if(result){
                    SetOrCancelLikeHandler.sendEmptyMessage(CANCEL);
                }
                else{
                    SetOrCancelLikeHandler.sendEmptyMessage(ERROR);
                }
            }
        };

        Handler SetOrCancelLikeHandler=new Handler(context.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                ImageView imageView=imageViewWeakReference.get();
                TextView textView=textViewWeakReference.get();
                if(msg.what!=ERROR&&imageView!=null&&textView!=null){
                    datas.get(position).setUserLike((LIKE==msg.what));
                    if(!changeLikeImageState(datas.get(position).getUserLike(),imageView,textView,position)){
                        new GetInformation(findFragmentAdapter.this).execute();
                    }
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
                login();
                return;
            }
            new Thread(addOrCancelFocus).start();
        }
        Runnable addOrCancelFocus=new Runnable(){
            @Override
            public void run() {
                boolean result=data.getUserFocusUser()?HandlePerson.Cancel_Focus(userID,data.getUser_id()):HandlePerson.Add_Focus(userID,data.getUser_id());
                setDataState(result);
                handler.sendEmptyMessage(data.getUserFocusUser()?1:2);
            }
        };
        private void setDataState(boolean result){
            if(result){
                datas.get(position).setUserFocusUser(!datas.get(position).getUserFocusUser());
                data.setUserFocusUser(!data.getUserFocusUser());
            }
            for(int i=0;i<datas.size();i++){
                if(datas.get(i).getUser_id()==data.getUser_id()){
                    datas.get(i).setUserFocusUser(!datas.get(i).getUserFocusUser());
                }
            }
        }

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
        new GetReplyCount(commentID,position,this).execute();
    }

    public void reFreshList(){
        new GetInformation(this).execute();
    }

    static class GetReplyCount extends AsyncTask<Void,Void,Integer>{
        int commentID;
        int position;
        WeakReference<findFragmentAdapter> findFragmentAdapterWeakReference;
        private GetReplyCount(int commentID,int position,findFragmentAdapter adapter){
            this.commentID=commentID;
            this.position=position;
            findFragmentAdapterWeakReference=new WeakReference<>(adapter);
        }
        @Override
        protected Integer doInBackground(Void... voids) {
            return HandleFind.Get_User_Comment_Count(commentID);
        }
        @Override
        protected void onPostExecute(Integer count) {
            findFragmentAdapter adapter=findFragmentAdapterWeakReference.get();
            if(adapter==null)
                return;
            adapter.datas.get(position).setCommentReplyNum(String.valueOf(count));
            adapter.notifyDataSetChanged();
        }
    }

    static class GetInformation extends AsyncTask<Void,Void,Void>{

        WeakReference<findFragmentAdapter> findFragmentAdapterWeakReference;

        private GetInformation(findFragmentAdapter adapter){
            findFragmentAdapterWeakReference=new WeakReference<>(adapter);
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
            Intent intent=new Intent("android.intent.action.animationStop");
            adapter.context.sendBroadcast(intent);
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
            imageView.startAnimation(findFragmentAdapter.imageAnimation);
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
            if(result==null)
                return null;
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