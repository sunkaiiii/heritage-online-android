package com.example.sunkai.heritage;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandlePerson;
import com.example.sunkai.heritage.Data.HandlePic;
import com.makeramen.roundedimageview.RoundedImageView;

import org.kobjects.base64.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * 此类是用于处理个人中心页面
 */


public class personFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout orderLinear,myOwnTiezi;
    private TextView userName,follow,followNumber,fans,fansNumber;
    private RoundedImageView userImage;

    AlertDialog.Builder builder;
    AlertDialog ad;

    Uri outputUri;//剪裁后的图片的uri


    private static final int IS_INTO_LOGIN=1;
    private static final int REQUEST_PICK_IMAGE = 11; //相册选取
    private static final int REQUEST_CAPTURE = 12;  //拍照
    private static final int REQUEST_PICTURE_CUT = 13;  //剪裁图片
    private static final int REQUEST_PERMISSION = 14;  //权限请求
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private Uri imageUri;//原图保存地址
    private boolean isClickCamera;
    private String imagePath;
    public personFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment personFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static personFragment newInstance(String param1, String param2) {
        personFragment fragment = new personFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.fragment_person, container, false);
        orderLinear=(LinearLayout)view.findViewById(R.id.fragment_person_oder_linner);
        myOwnTiezi=(LinearLayout)view.findViewById(R.id.fragment_person_my_tiezi);
        orderLinear.setOnClickListener(this);
        myOwnTiezi.setOnClickListener(this);
        userName=(TextView)view.findViewById(R.id.sign_name_textview);
        if(LoginActivity.userName==null){
            userName.setText("没有登录");
        }
        else{
            userName.setText(LoginActivity.userName);
        }
        follow=(TextView)view.findViewById(R.id.person_follow);
        followNumber=(TextView)view.findViewById(R.id.person_follow_number);
        fans=(TextView)view.findViewById(R.id.person_fans);
        fansNumber=(TextView)view.findViewById(R.id.person_fans_number);
        userImage=(RoundedImageView)view.findViewById(R.id.sign_in_icon);
        follow.setOnClickListener(this);
        followNumber.setOnClickListener(this);
        fans.setOnClickListener(this);
        fansNumber.setOnClickListener(this);
        userImage.setOnClickListener(this);
        mPermissionsChecker = new PermissionsChecker(getActivity());
        GetUserInfo();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.focusAndFansCountChange");
        getActivity().registerReceiver(focusAndFansCountChange,intentFilter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.refreInfomation");
        getActivity().registerReceiver(refreshInfo,intentFilter);
        return view;
    }

    /**
     * 开始获取用户的粉丝和关注的信息
     * 先获取关注信息
     */
    private void GetUserInfo(){
        new Thread(getFollowCount).start();
    }
    private void toLogin(){
        Toast.makeText(getActivity(),"没有登录",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getActivity(),LoginActivity.class);
        intent.putExtra("isInto",1);
        startActivityForResult(intent,1);
    }

    @Override
    public void onClick(View v){
        Intent intent;
        if(LoginActivity.userID==0){
            toLogin();
            return;
        }
        switch (v.getId()){
            case R.id.fragment_person_oder_linner:
                intent=new Intent(getActivity(),MyOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.fragment_person_my_tiezi:
                intent=new Intent(getActivity(),UserOwnTiezi.class);
                startActivity(intent);
                break;
            case R.id.person_follow:
            case R.id.person_follow_number:
                intent=new Intent(getActivity(),focusInformation.class);
                intent.putExtra("information","focus");
                startActivity(intent);
                break;
            case R.id.person_fans:
            case R.id.person_fans_number:
                intent=new Intent(getActivity(),focusInformation.class);
                intent.putExtra("information","fans");
                startActivity(intent);
                break;
            case R.id.sign_in_icon:
                builder=new AlertDialog.Builder(getActivity()).setTitle("请选择");
                final ListView choice=new ListView(getActivity());
                ArrayAdapter arrayAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);
                arrayAdapter.add("拍照");
                arrayAdapter.add("从图库中选择");
                final android.app.AlertDialog dialog;
                choice.setAdapter(arrayAdapter);
                builder.setView(choice);
                ad = builder.show();

                /**
                 * 在获取图片或者拍照的时候，判断系统版本，如果是安卓6.0以上，要动态申请权限
                 */
                choice.setOnItemClickListener((parent, view, position, id) -> {
                    if(position==0)
                    {
                        ad.dismiss();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                                startPermissionsActivity();
                            } else {
                                openCamera();
                            }
                        } else {
                            openCamera();
                        }
                        isClickCamera = true;
                    }
                    else if(position==1)
                    {
                        ad.dismiss();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                                startPermissionsActivity();
                            } else {
                                selectFromAlbum();
                            }
                        } else {
                            selectFromAlbum();
                        }
                        isClickCamera = false;
                    }
                });
                break;
            default:
                break;
        }
    }
    /**
     * 打开系统相机
     */
    private void openCamera() {
        File file = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(getActivity(), "com.example.sunkai.heritage.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * 从相册选择
     */
    private void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }


//    /**
//     * 裁剪
//     */
//    private void cropPhoto() {
//        File file = new FileStorage().createCropFile();
//        Uri outputUri = Uri.fromFile(file);//缩略图保存地址
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//        intent.setDataAndType(imageUri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true);
//        startActivityForResult(intent, REQUEST_PICTURE_CUT);
//    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(getActivity(), REQUEST_PERMISSION,
                PERMISSIONS);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        if(data==null){
            return;
        }
        imageUri = data.getData();
        if (DocumentsContract.isDocumentUri(getActivity(), imageUri)) {
            //如果是document类型的uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(imageUri);
            if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(imageUri, null);
        } else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = imageUri.getPath();
        }

        cropPhoto();
//        setImage();
    }

    private void handleImageBeforeKitKat(Intent intent) {
        imageUri = intent.getData();
        imagePath = getImagePath(imageUri, null);
        cropPhoto();
//        setImage();
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection老获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void setImage(){
        Bitmap bitmap = null;
        try {
            /**
             * 在经过剪裁之后，outputUri会被赋值
             * 将uri指向的内容复制给bitmap
             */
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(outputUri));
            userImage.setImageDrawable(null);
            System.gc();
            bitmap=HandlePic.compressBitmapToFile(bitmap,192,192);
            userImage.setImageBitmap(bitmap);
//            System.out.println("孙楷最帅"+bitmap.getWidth()+","+bitmap.getHeight());
            builder=new AlertDialog.Builder(getActivity()).setTitle("上传中").setView(R.layout.update_image_builder);
            ad=builder.create();
            ad.show();
            new Thread(updateUserImage).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        /**
     * 裁剪
     */

    private void cropPhoto() {
        File file = new FileStorage().createCropFile();
        outputUri = Uri.fromFile(file);//缩略图保存地址
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);//剪裁输出的uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);//人脸检测
        startActivityForResult(intent, REQUEST_PICTURE_CUT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IS_INTO_LOGIN:
                userName.setText(LoginActivity.userName);
                GetUserInfo();
                break;
            case REQUEST_PICK_IMAGE://从相册选择
                if (Build.VERSION.SDK_INT >= 19) {
                    handleImageOnKitKat(data);
                } else {
                    handleImageBeforeKitKat(data);
                }
                break;
            case REQUEST_CAPTURE://拍照
                if (resultCode == getActivity().RESULT_OK) {
//                    cropPhoto();
                    cropPhoto();
                }
                break;
            case REQUEST_PICTURE_CUT://裁剪完成
                setImage();
                break;
            case REQUEST_PERMISSION://权限请求
                if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    getActivity().finish();
                } else {
                    if (isClickCamera) {
                        openCamera();
                    } else {
                        selectFromAlbum();
                    }
                }
                break;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(focusAndFansCountChange);
        getActivity().unregisterReceiver(refreshInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_search_user:
                intent=new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.action_setting:
                if(LoginActivity.userID==0){
                    toLogin();
                    break;
                }
                intent=new Intent(getActivity(),SettingActivity.class);
                userImage.setDrawingCacheEnabled(true);
                intent.putExtra("userImage",userImage.getDrawingCache());
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    BroadcastReceiver focusAndFansCountChange=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("change".equals(intent.getStringExtra("message"))){
                new Thread(getFollowCount).start();
            }
            if("sigh_out".equals(intent.getStringExtra("message"))){
                intent=new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    };

    Runnable getFollowCount=new Runnable() {
        @Override
        public void run() {
            int count= HandlePerson.Get_Follow_Number(LoginActivity.userID);
            getFollowCountHandler.sendEmptyMessage(count);
        }
    };
    Handler getFollowCountHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            followNumber.setText(String.valueOf(msg.what));
            new Thread(getFansCount).start();
        }
    };
    Runnable getFansCount=new Runnable() {
        @Override
        public void run() {
            int count=HandlePerson.Get_Fans_Number(LoginActivity.userID);
            getFanseCountHandler.sendEmptyMessage(count);
        }
    };
    Handler getFanseCountHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            fansNumber.setText(String.valueOf(msg.what));
            new Thread(getUserImage).start();
        }
    };
    Runnable getUserImage=new Runnable() {
        @Override
        public void run() {
            String result=HandlePerson.Get_User_Image(LoginActivity.userID);
            if("Error".equals(result)){
                getUserImageHandler.sendEmptyMessage(0);
            }
            else{
                Message msg=new Message();
                Bundle data=new Bundle();
                data.putString("image",result);
                msg.setData(data);
                msg.what=1;
                getUserImageHandler.sendMessage(msg);
            }
        }
    };
    Handler getUserImageHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Bundle data=msg.getData();
                String imageCode=data.getString("image");
                byte[] imageByte= Base64.decode(imageCode);
                InputStream in=new ByteArrayInputStream(imageByte);
                Bitmap bitmap= HandlePic.handlePic(getActivity(),in,0);
                userImage.setImageBitmap(bitmap);
            }
        }
    };


    Runnable updateUserImage=new Runnable() {
        @Override
        public void run() {
            userImage.setDrawingCacheEnabled(true);
            Bitmap bitmap=userImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,40,baos);
            byte[] image=baos.toByteArray();
            String imageCode=Base64.encode(image);
            boolean result=HandlePerson.Update_User_Image(LoginActivity.userID,imageCode);
            if(result){
                updateUserImageHandler.sendEmptyMessage(1);
            }
            else{
                updateUserImageHandler.sendEmptyMessage(0);
            }
        }
    };

    Handler updateUserImageHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ad.dismiss();
            if(msg.what==1){
                Toast.makeText(getActivity(),"更新头像成功",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(),"出现错误，请稍后再试",Toast.LENGTH_SHORT).show();
                userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp);
            }
        }
    };
    BroadcastReceiver refreshInfo=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            userName.setText(LoginActivity.userName);
            GetUserInfo();
        }
    };
}
