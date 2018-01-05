package com.example.sunkai.heritage.Data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/*
 * Created by sunkai on 2017/5/31.
 */

class MySqlLite(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table if not exists user_login_info("
                + "id integer primary key,"
                + "user_id integer,"
                + "user_name varchar,"
                + "user_password varchar)"
        )
        db.execSQL("insert into user_login_info (id,user_id,user_name,user_password) select 1,'0','0','0' where not exists(select * from user_login_info where id=1)")
        db.execSQL("create table if not exists main_activity_image("
                + "id integer primary key," +
                "imageID integer,"
                + "image mediumblob)")
        db.execSQL("create table if not exists channel_activity_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)")
        db.execSQL("create table if not exists folk_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)")
        db.execSQL("create table if not exists find_activity_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)")
        db.execSQL("create table if not exists find_comment_image(" +
                "id integer primary key," +
                "imageID integer," +
                "comment_time varchar,"+
                "image mediumblob)")
        db.execSQL("create table if not exists person_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob," +
                "update_time varchar)")

        db.execSQL("create table if not exists find_fragment_activity(" +
                "id integer primary key," +
                "imageID integer," +
                "title varchar," +
                "content varchar," +
                "image mediumblob)")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE find_comment_image")
        db.execSQL("create table if not exists user_login_info("
                + "id integer primary key,"
                + "user_id integer,"
                + "user_name varchar,"
                + "user_password varchar)"
        )
        db.execSQL("insert into user_login_info (id,user_id,user_name,user_password) select 1,'0','0','0' where not exists(select * from user_login_info where id=1)")
        db.execSQL("create table if not exists main_activity_image("
                + "id integer primary key," +
                "imageID integer,"
                + "image mediumblob)")
        db.execSQL("create table if not exists channel_activity_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)")
        db.execSQL("create table if not exists folk_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)")
        db.execSQL("create table if not exists find_activity_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob)")
        db.execSQL("create table if not exists find_comment_image(" +
                "id integer primary key," +
                "imageID integer," +
                "comment_time varchar,"+
                "image mediumblob)")
        db.execSQL("create table if not exists person_image(" +
                "id integer primary key," +
                "imageID integer," +
                "image mediumblob," +
                "update_time varchar)")

        db.execSQL("create table if not exists find_fragment_activity(" +
                "id integer primary key," +
                "imageID integer," +
                "title varchar," +
                "content varchar," +
                "image mediumblob)")
    }
}
