package com.example.sunkai.heritage.ConnectWebService;

import android.support.annotation.Nullable;

import com.example.sunkai.heritage.Data.FocusData;
import com.example.sunkai.heritage.Activity.LoginActivity;
import com.example.sunkai.heritage.Data.OtherPersonData;

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
    private static List<FocusData> Json_To_focusData(String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            JSONArray info=(JSONArray)jsonObject.get("Follow_Information");
            List<FocusData> datas=new ArrayList<>();
            for(int i=0;i<info.length();i++){
                FocusData data=new FocusData();
                JSONObject jsondata=(JSONObject)info.get(i);
                data.setFocusUserid(Integer.parseInt(jsondata.getString("focus_focusID")));
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
    private static List<FocusData> Json_To_SearchData(String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            JSONArray searInfo=(JSONArray)jsonObject.get("searchInfo");
            List<FocusData> datas=new ArrayList<>();
            for(int i=0;i<searInfo.length();i++){
                JSONObject jsonData=(JSONObject)searInfo.get(i);
                FocusData data=new FocusData();
                data.setFolloweachother(false);
                data.setCheck(false);
                data.setName(jsonData.getString("user_name"));
                data.setFocusFansID(Integer.parseInt(jsonData.getString("id")));
                data.setFocusUserid(LoginActivity.Companion.getUserID());
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
    private static OtherPersonData Json_To_OtherPersonData(int userID,String json){
        if(json==null)
            return null;
        try{
            JSONObject jsonObject=new JSONObject(json);
            String userName=jsonObject.getString("userName");
            int focusNumber=jsonObject.getInt("focusNumber");
            int fansNumber=jsonObject.getInt("fansNumber");
            int permisison=jsonObject.getInt("permission");
            return new OtherPersonData(userID,focusNumber,fansNumber,userName,permisison);
        }catch (JSONException e){
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
        return success.equals(result);
    }
    public static String Get_User_Image(int userID){
        methodName="Get_User_Image";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        if(error.equals(result))
            return null;
        return result;
    }
    public static int Get_Follow_Number(int userID){
        methodName="Get_Follow_Number";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        if(error.equals(result)||result==null){
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
        if(error.equals(result)||result==null){
            return 0;
        }
        return Integer.parseInt(result);
    }
    public static List<FocusData> Get_Follow_Information(int userID){
        methodName="Get_Follow_Information";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return Json_To_focusData(result);
    }
    public static List<FocusData> Get_Fans_Information(int userID){
        methodName="Get_Fans_Information";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return Json_To_focusData(result);
    }
    public static OtherPersonData Get_User_All_Info(int userID){
        methodName="Get_User_All_Info";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return Json_To_OtherPersonData(userID,result);
    }
    public static boolean Add_Focus(int userID,int focusID){
        methodName="Add_Focus";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("focusID",focusID);
        String result=Get_Post(soapObject);
        return success.equals(result);
    }
    public static boolean Cancel_Focus(int userID,int focusID){
        methodName="Cancel_Focus";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("focusID",focusID);
        String result=Get_Post(soapObject);
        return success.equals(result);
    }
    public static boolean Check_Follow_Eachohter(int userID,int focusID){
        methodName="Check_Follow_Eachohter";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("focusID",focusID);
        String result=Get_Post(soapObject);
        return success.equals(result);
    }
    public static List<FocusData> Get_Search_UserInfo(String name){
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
        return success.equals(result);
    }
    public static String Get_User_Update_Time(int userID){
        methodName="Get_User_Update_Time";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return result;
    }
    public static int Get_User_Permission(int userID){
        methodName="Get_User_Permission";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        return Integer.parseInt(result);
    }

    public static boolean Set_User_Permission(int userID,int permission){
        methodName="Set_User_Permission";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("permission",permission);
        String result=Get_Post(soapObject);
        return success.equals(result);
    }
}
