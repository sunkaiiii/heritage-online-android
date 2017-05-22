package com.example.sunkai.heritage.Data;

import java.io.Serializable;

/**
 * Created by sunkai on 2017/3/27.
 * 此类用于存储首页5个页面的页面信息
 * 实现了Serializable可以传入至bundle中
 */

public class classifyActiviyData implements Serializable{
    public int id;
    public String activityTitle,activitContent,activityChannel;
    public byte[] activityImage;
}
