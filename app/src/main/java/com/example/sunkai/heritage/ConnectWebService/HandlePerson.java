package com.example.sunkai.heritage.ConnectWebService;

import android.support.annotation.Nullable;

import com.example.sunkai.heritage.Data.focusData;
import com.example.sunkai.heritage.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunkai on 2017-5-2.
 * 此类封装了个人中心页服务器请求的方法
 */


public class HandlePerson extends BaseSetting {

    @Nullable
    private static List<focusData> Json_To_focusData(String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            JSONArray info=(JSONArray)jsonObject.get("Follow_Information");
            List<focusData> datas=new ArrayList<>();
            for(int i=0;i<info.length();i++){
                focusData data=new focusData();
                JSONObject jsondata=(JSONObject)info.get(i);
                data.setFocusFansID(Integer.parseInt(jsondata.getString("focus_focusID")));
                data.setFocusFansID(Integer.parseInt(jsondata.getString("focus_fansID")));
                data.setName(jsondata.getString("USER_NAME"));
                datas.add(data);
            }
            return datas;
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    private static List<focusData> Json_To_SearchData(String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            JSONArray searInfo=(JSONArray)jsonObject.get("searchInfo");
            List<focusData> datas=new ArrayList<>();
            for(int i=0;i<searInfo.length();i++){
                JSONObject jsonData=(JSONObject)searInfo.get(i);
                focusData data=new focusData();
                data.setFolloweachother(false);
                data.setCheck(false);
                data.setName(jsonData.getString("user_name"));
                data.setFocusFansID(Integer.parseInt(jsonData.getString("id")));
                data.setFocusUserid(LoginActivity.userID);
                datas.add(data);
            }
            return datas;
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    public static boolean Update_User_Image(int userID,String image){
        methodName="Update_User_Image";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("image",image);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static String Get_User_Image(int userID){
        methodName="Get_User_Image";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return result;
    }
    public static int Get_Follow_Number(int userID){
        methodName="Get_Follow_Number";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        if(error.equals(result)){
            return 0;
        }
        return Integer.parseInt(result);
    }
    public static int Get_Fans_Number(int userID){
        methodName="Get_Fans_Number";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        if(error.equals(result)){
            return 0;
        }
        return Integer.parseInt(result);
    }
    public static List<focusData> Get_Follow_Information(int userID){
        methodName="Get_Follow_Information";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return Json_To_focusData(result);
    }
    public static List<focusData> Get_Fans_Information(int userID){
        methodName="Get_Fans_Information";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return Json_To_focusData(result);
    }
    public static boolean Add_Focus(int userID,int focusID){
        methodName="Add_Focus";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("focusID",focusID);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean Cancel_Focus(int userID,int focusID){
        methodName="Cancel_Focus";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("focusID",focusID);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean Check_Follow_Eachohter(int userID,int focusID){
        methodName="Check_Follow_Eachohter";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("focusID",focusID);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static List<focusData> Get_Search_UserInfo(String name){
        methodName="Get_Search_UserInfo";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("name",name);
        String result=Get_Post(soapObject);
        return Json_To_SearchData(result);
    }
    public static boolean is_User_Follow(int userID,int fansID){
        methodName="is_User_Follow";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userName",userID);
        soapObject.addProperty("fansName",fansID);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static String Get_User_Update_Time(int userID){
        methodName="Get_User_Update_Time";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return result;
    }
}
