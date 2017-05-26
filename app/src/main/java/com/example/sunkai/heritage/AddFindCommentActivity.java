package com.example.sunkai.heritage;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
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
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sunkai.heritage.ConnectWebService.HandleFind;
import com.example.sunkai.heritage.Data.HandlePic;

import org.kobjects.base64.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * 此类是处理发帖活动的类
 */

public class AddFindCommentActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText add_comment_title;
    private EditText add_comment_content;
    private ImageView add_comment_image;

    private static final int REQUEST_PICK_IMAGE = 1; //相册选取
    private static final int REQUEST_CAPTURE = 2;  //拍照
    private static final int REQUEST_PICTURE_CUT = 3;  //剪裁图片
    private static final int REQUEST_PERMISSION = 4;  //权限请求
    private Button take_btn, album_btn;
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private Uri imageUri;//原图保存地址
    private boolean isClickCamera;
    private String imagePath;
    private ActionBar actionBack;

    private boolean isSavePicture=false;//图片上传状态

    AlertDialog.Builder builder;//选择对话框
    AlertDialog ad;

    private boolean isHadImage=false;//用户是否添加图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_find_comment);
        initView();
    }

    private void initView() {
        add_comment_title = (EditText) findViewById(R.id.add_comment_title);
        add_comment_content = (EditText) findViewById(R.id.add_comment_content);
        add_comment_image = (ImageView) findViewById(R.id.add_comment_image);
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);
        add_comment_image.setOnClickListener(this);
        mPermissionsChecker = new PermissionsChecker(this);
        /**
         * 标志位0，防止返回find时刷新页面
         */
        setResult(0,getIntent());
    }

    private void submit() {
        // validate
        String title = add_comment_title.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = add_comment_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isHadImage){
            Toast.makeText(this, "请添加图片", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * 判断是否正在上传图片，防止重复上传
         */
        if(isSavePicture){
            return;
        }

        isSavePicture=true;
        new Thread(addCommentInformation).start();

    }

    Runnable addCommentInformation=new Runnable() {
        @Override
        public void run() {
            String title = add_comment_title.getText().toString().trim();
            String content = add_comment_content.getText().toString().trim();
            /**
             * 从Imageview中获取Bitmap
             */
            add_comment_image.setDrawingCacheEnabled(true);
            Bitmap bitmap=add_comment_image.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60 ,baos);
            byte[] imgbyte=baos.toByteArray();
            String imageCode= Base64.encode(imgbyte);
            boolean result=HandleFind.Add_User_Comment_Information(LoginActivity.userID,title,content,imageCode);
            if(result){
                addCommentInformationHandler.sendEmptyMessage(1);
            }
            else{
                addCommentInformationHandler.sendEmptyMessage(0);
            }
        }
    };

    Handler addCommentInformationHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                isSavePicture=false;
                Toast.makeText(AddFindCommentActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                setResult(1,getIntent());
                finish();
            }
            else{
                isSavePicture=false;
                Toast.makeText(AddFindCommentActivity.this,"出现错误，请稍后再试",Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save_comment:
                submit();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_comment_image:
                builder=new AlertDialog.Builder(AddFindCommentActivity.this).setTitle("请选择");
                final ListView choice=new ListView(this);
                ArrayAdapter arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
                arrayAdapter.add("拍照");
                arrayAdapter.add("从图库中选择");
                final android.app.AlertDialog dialog;
                choice.setAdapter(arrayAdapter);
                builder.setView(choice);
                ad = builder.show();
                choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                    }
                });
        }
    }


    /**
     * 打开系统相机
     */
    private void openCamera() {
        File file = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            /**
             * 安卓7.0需要动态申请权限，要重新进行一次适配
             */
            imageUri = FileProvider.getUriForFile(AddFindCommentActivity.this, "com.example.sunkai.heritage.fileprovider", file);//通过FileProvider创建一个content类型的Uri
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
        PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION,
                PERMISSIONS);
    }

    /**
     *安卓4.4之后，权限管理更加严格，需要用心的方式解析图片的Uri
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        imageUri = data.getData();
        if (DocumentsContract.isDocumentUri(this, imageUri)) {
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

//        cropPhoto();
        setImage();
    }

    /**
     *安卓4.4之前可以直接从uri获取图片的路径
     */
    private void handleImageBeforeKitKat(Intent intent) {
        imageUri = intent.getData();
        imagePath = getImagePath(imageUri, null);
//        cropPhoto();
        setImage();
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection老获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //重写onCreateOptionsMenu方法，在顶部的bar中显示菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_coment_menu, menu);
        return true;
    }

    private void setImage(){
        Bitmap bitmap = null;
        try {
            if (isClickCamera) {

                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } else {
                bitmap = BitmapFactory.decodeFile(imagePath);
            }
            add_comment_image.setImageDrawable(null);
            System.gc();
            bitmap=HandlePic.compressBitmapToFile(bitmap,640,480);
            add_comment_image.setImageBitmap(bitmap);
            isHadImage=true;
//            System.out.println("孙楷最帅"+bitmap.getWidth()+","+bitmap.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 当选择图片之后，根据安卓版本的不同以及选择的方式的不同，执行不同的方法
         */
        switch (requestCode) {
            case REQUEST_PICK_IMAGE://从相册选择
                if(data==null){
                    return;
                }
                if (Build.VERSION.SDK_INT >= 19) {
                    handleImageOnKitKat(data);
                } else {
                    handleImageBeforeKitKat(data);
                }
                break;
            case REQUEST_CAPTURE://拍照
                if (resultCode == RESULT_OK) {
//                    cropPhoto();
                    setImage();
                }
                break;
            /**
             * 发帖页面暂时认为不需要剪裁图片
             */
            case REQUEST_PICTURE_CUT://裁剪完成

                break;
            case REQUEST_PERMISSION://权限请求
                if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    finish();
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
}
