package com.example.sunkai.heritage.Data;

import java.io.Serializable;

/**
 * Created by sunkai on 2017/3/3.
 * 此类用于存放民间页的活动信息
 * 实现了Serializable可以传入至bundle中
 */

public class folkData implements Serializable {
    public Integer id;
    public String title;
    public String content;
    public String location;
    public String divide;
    public String teacher;
    public String techTime;
    public byte[] image;
}
