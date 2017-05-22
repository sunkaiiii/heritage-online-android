package com.example.sunkai.heritage.Data;

import java.io.Serializable;

/**
 * Created by sunkai on 2017-4-24.
 * 此类用于存储用户他发帖的信息
 * 实现了Serializable可以传入至bundle中
 */

public class userCommentData implements Serializable {
    public int id,inListPosition,user_id;
    public String commentTime,commentTitle,commentContent,commentLikeNum,commentReplyNum,userName;
    public byte[] userCommentIamge;
    public byte[] userImage;
    public boolean isUserLike=false;
    public boolean isUserFocusUser=false;
}
