package com.example.sunkai.heritage.ConnectWebService;

import android.support.annotation.Nullable;

import com.example.sunkai.heritage.Data.FindActivityData;
import com.example.sunkai.heritage.Data.commentReplyData;
import com.example.sunkai.heritage.Data.userCommentData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunkai on 2017-4-24.
 * 此类封装了发现页相关服务器请求的方法
 */

public class HandleFind extends BaseSetting {
    @Nullable
    private static List<userCommentData> Json_To_UserCommentData(String json){
        try {
            if(null==json){
                return null;
            }
            JSONObject MainActivity = new JSONObject(json);
            JSONArray activities = MainActivity.getJSONArray("user_comment_information");
            List<userCommentData> folkInformations = new ArrayList<>();
            for (int i = 0; i < activities.length(); i++) {
                userCommentData data = new userCommentData();
                JSONObject activity = (JSONObject) activities.get(i);
                data.id =(int) activity.get("id");
                data.user_id=(int)activity.get("user_id");
                data.commentTime = (String) activity.get("comment_time");
                data.commentTitle = (String) activity.get("comment_title");
                data.commentContent = (String) activity.get("comment_content");
                data.commentLikeNum=(String)activity.get("comment_num");
                data.commentReplyNum=(String)activity.get("reply_num");
                data.userName=(String)activity.get("user_name");
                folkInformations.add(data);
            }
            return folkInformations;
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    private static List<commentReplyData> Json_To_UserCommentReplyData(String json){
        try{
            JSONObject reply=new JSONObject(json);
            JSONArray replys=reply.getJSONArray("reply_information");
            List<commentReplyData> datas=new ArrayList<>();
            for(int i=0;i<replys.length();i++){
                commentReplyData data=new commentReplyData();
                JSONObject oneReply=(JSONObject)replys.get(i);
                data.replyId=(int)oneReply.get("reply_id");
                data.replyTime=(String)oneReply.get("reply_time");
                data.replyContent=(String)oneReply.get("reply_content");
                data.userName=(String)oneReply.get("user_name");
                datas.add(data);
            }
            return datas;

        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    private static List<FindActivityData> Json_To_FindAcitivityID(List<FindActivityData> datas, String json){
        try{
            JSONObject js=new JSONObject(json);
            JSONArray replys=js.getJSONArray("id");
            for(int i=0;i<replys.length();i++){
                FindActivityData data=new FindActivityData();
                data.id=(int)replys.get(i);
                datas.add(data);
            }
            return datas;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    private static FindActivityData Json_To_FindActivityInformation(int id,String json){
        try{
            FindActivityData getdata=new FindActivityData();
            getdata.id=id;
            JSONObject js=new JSONObject(json);
            getdata.title=js.getString("title");
            getdata.content=js.getString("content");
            String imgCode=js.getString("image");
            getdata.image=Base64.decode(imgCode);
            return getdata;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    public static List<FindActivityData> Get_Find_Activity_ID(List<FindActivityData> datas){
        methodName="Get_Find_Activity_ID";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        String result=Get_Post(soapObject);
        return Json_To_FindAcitivityID(datas,result);
    }
    public static FindActivityData Get_Find_Activity_Information(int id){
        methodName="Get_Find_Activity_Information";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("id",id);
        String result=Get_Post(soapObject);
        return Json_To_FindActivityInformation(id,result);
    }
    public static boolean Add_User_Comment_Information(int user_id,String comment_title,String comment_content,String comment_image) {
        methodName="Add_User_Comment_Information";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("user_id",user_id);
        soapObject.addProperty("comment_title",comment_title);
        soapObject.addProperty("comment_content",comment_content);
        soapObject.addProperty("comment_image",comment_image);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static List<userCommentData> Get_User_Comment_Information(){
        methodName="Get_User_Comment_Information";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        String result=Get_Post(soapObject);
        return Json_To_UserCommentData(result);
    }
    public static List<userCommentData> Get_User_Comment_Information_By_User(int userID){
        methodName="Get_User_Comment_Information_By_User";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return Json_To_UserCommentData(result);
    }
    public static List<userCommentData> Get_User_Comment_Information_By_Own(int userID){
        methodName="Get_User_Comment_Information_By_Own";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return Json_To_UserCommentData(result);
    }
    @Nullable
    public static byte[] Get_User_Comment_Image(int id){
        methodName="Get_User_Comment_Image";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("id",id);
        String result=Get_Post(soapObject);
        if(null==result||error.equals(result)){
            return null;
        }
        byte[] data= Base64.decode(result);
        return data;
    }
    public static boolean Get_User_Is_Like(int userID,int commentID){
        methodName="Get_User_Is_Like";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("commentID",commentID);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean Set_User_Like(int userID,int commentID){
        methodName="Set_User_Like";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("commentID",commentID);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean Cancel_User_Like(int userID,int commentID){
        methodName="Cancel_User_Like";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("commentID",commentID);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static List<commentReplyData> Get_User_Comment_Reply(int commentID){
        methodName="Get_User_Comment_Reply";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("commentID",commentID);
        String result=Get_Post(soapObject);
        return Json_To_UserCommentReplyData(result);
    }
    public static int Add_User_Comment_Reply(int userID,int commentID,String replyContent){
        try{
            Thread.sleep(200);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        methodName="Add_User_Comment_Reply";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("commentID",commentID);
        soapObject.addProperty("replyContent",replyContent);
        String result=Get_Post(soapObject);
        if(error.equals(result)){
            return 0;
        }
        else{
            return Integer.parseInt(result);
        }
    }
    public static boolean Update_User_Comment_Reply(int replyID,String replyContent){
        methodName="Update_User_Comment_Reply";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("replyID",replyID);
        soapObject.addProperty("replyContent",replyContent);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean Delete_User_Comment_Reply(int replyID){
        methodName="Update_User_Comment_Reply";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("replyID",replyID);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static int Get_User_Comment_Count(int commentID){
        methodName="Get_User_Comment_Count";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("commentID",commentID);
        String result=Get_Post(soapObject);
        if(error.equals(result)){
            return 0;
        }
        else{
            return Integer.parseInt(result);
        }
    }
}
