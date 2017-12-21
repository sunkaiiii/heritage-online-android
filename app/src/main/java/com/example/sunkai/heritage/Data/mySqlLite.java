package com.example.sunkai.heritage.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunkai on 2017/5/31.
 */

public class mySqlLite extends SQLiteOpenHelper{
    public mySqlLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context,name,factory,version);
    }
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table if not exists user_login_info("
                +"id integer primary key,"
                +"user_id integer,"
                +"user_name varchar,"
                +"user_password varchar)"
        );
        db.execSQL("insert into user_login_info (id,user_id,user_name,user_password) select 1,'0','0','0' where not exists(select * from user_login_info where id=1)");
        db.execSQL("create table if not exists main_activity_image("
            +"id integer primary key," +
                "imageID integer,"
            +"image mediumblob)");
        db.execSQL("create table if not exists channel_activity_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)");
        db.execSQL("create table if not exists folk_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)");
        db.execSQL("create table if not exists find_activity_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)");
        db.execSQL("create table if not exists find_comment_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)");
        db.execSQL("create table if not exists person_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob," +
                "update_time varchar)");

        db.execSQL("create table if not exists find_fragment_activity("+
                "id integer primary key,"+
                "imageID integer,"+
                "title varchar,"+
                "content varchar,"+
                "image mediumblob)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table if not exists user_login_info("
                +"id integer primary key,"
                +"user_id integer,"
                +"user_name varchar,"
                +"user_password varchar)"
        );
        db.execSQL("insert into user_login_info (id,user_id,user_name,user_password) select 1,'0','0','0' where not exists(select * from user_login_info where id=1)");
        db.execSQL("create table if not exists main_activity_image("
                +"id integer primary key," +
                "imageID integer,"
                +"image mediumblob)");
        db.execSQL("create table if not exists channel_activity_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)");
        db.execSQL("create table if not exists folk_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)");
        db.execSQL("create table if not exists find_activity_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)");
        db.execSQL("create table if not exists find_comment_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)");
        db.execSQL("create table if not exists person_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob," +
                "update_time varchar)");

        db.execSQL("create table if not exists find_fragment_activity("+
                "id integer primary key,"+
                "imageID integer,"+
                "title varchar,"+
                "content varchar,"+
                "image mediumblob)");
    }
}
