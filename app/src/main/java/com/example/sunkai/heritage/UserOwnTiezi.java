package com.example.sunkai.heritage;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

public class UserOwnTiezi extends AppCompatActivity {

    private ListView myOwnList;
    private findFragmentAdapter adapter;
    private ActionBar actionBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_own_tiezi);
        myOwnList=(ListView)findViewById(R.id.user_own_list);
        adapter=new findFragmentAdapter(this,3);
        myOwnList.setAdapter(adapter);
        actionBack=getSupportActionBar();
        actionBack.setDisplayHomeAsUpEnabled(true);
    }

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
