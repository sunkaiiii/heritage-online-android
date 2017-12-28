package com.example.sunkai.heritage.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sunkai.heritage.Adapter.FindFragmentRecyclerViewAdpter;
import com.example.sunkai.heritage.R;

import java.io.ByteArrayOutputStream;

public class UserOwnTiezi extends AppCompatActivity {

    private RecyclerView myOwnList;
    private FindFragmentRecyclerViewAdpter adapter;
    private ActionBar actionBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_own_tiezi);
        myOwnList=(RecyclerView) findViewById(R.id.user_own_list);
        adapter=new FindFragmentRecyclerViewAdpter(this,3);
        setAdpterClick(adapter);
//        setAdpterLongClick(adapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        myOwnList.setLayoutManager(layoutManager);
        myOwnList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        myOwnList.setHasFixedSize(true);
        myOwnList.setAdapter(adapter);
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);
    }
    private void setAdpterClick(FindFragmentRecyclerViewAdpter adpter) {
        adpter.setOnItemClickListen((view, position) -> {
            Intent intent = new Intent(this, UserCommentDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data",adpter.getItem(position));
            bundle.putInt("position", position);
            ImageView imageView = (ImageView) view.findViewById(R.id.fragment_find_litview_img);
            imageView.setDrawingCacheEnabled(true);
            Drawable drawable = imageView.getDrawable();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            intent.putExtra("bitmap", out.toByteArray());
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
//    private void setAdpterLongClick(FindFragmentRecyclerViewAdpter adapter){
//        adapter.setOnItemLongClickListener(((view, position) -> {
//            Toast.makeText(UserOwnTiezi.this,String.valueOf(position)+"被长点击",Toast.LENGTH_SHORT).show();
//        }));
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
