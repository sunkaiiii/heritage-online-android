package com.example.sunkai.heritage.ConnectWebService;


import com.example.sunkai.heritage.Data.FolkData;
import com.example.sunkai.heritage.Data.OrderData;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunkai on 2017-4-22.
 * 此类封装了有关民间页相关的功能的服务器请求的方法
 */

public class HandleFolk extends BaseSetting {

    @Nullable
    private static List<FolkData> Json_To_FolkList(String json){
        if(error.equals(json)||json==null)
            return null;
        try {
            JSONObject MainActivity = new JSONObject(json);
            JSONArray activities = MainActivity.getJSONArray("folk_information");
            List<FolkData> folkInformations = new ArrayList<>();
            for (int i = 0; i < activities.length(); i++) {
                FolkData data = new FolkData();
                JSONObject activity = (JSONObject) activities.get(i);
                data.setId(Integer.valueOf((String) activity.get("id")));
                data.setTitle((String) activity.get("title"));
                data.setContent((String) activity.get("content"));
                data.setLocation((String) activity.get("location"));
                data.setDivide((String)activity.get("divide"));
                data.setTeacher((String)activity.get("teacher"));
//                data.techTime=(String)activity.get("tech-time");
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
    private static List<OrderData> Json_To_OrderList(String json){
        try {
            JSONObject MainActivity = new JSONObject(json);
            JSONArray activities = MainActivity.getJSONArray("UserOrderInfo");
            List<OrderData> folkInformations = new ArrayList<>();
            for (int i = 0; i < activities.length(); i++) {
                OrderData data = new OrderData();
                JSONObject activity = (JSONObject) activities.get(i);
                data.setId(((int) activity.get("id")));
                data.setUserID((int) activity.get("userID"));
                data.setOrderID((int) activity.get("orderID"));
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
    private static FolkData Json_To_SingleFolkData(String json){
        try {
            JSONObject folkData = new JSONObject(json);
            FolkData data=new FolkData();
            data.setId(Integer.valueOf((String) folkData.get("id")));
            data.setTitle((String) folkData.get("title"));
            data.setContent((String) folkData.get("content"));
            data.setLocation((String)folkData.get("location"));
            return data;
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    public static int GetFolkCount(){
        methodName="Get_Folk_Count";
        soapAction=namespace+"/"+methodName;
        HttpTransportSE transport=new HttpTransportSE(url);
        transport.debug=true;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        SoapSerializationEnvelope envelope=pre_processSoap(soapObject);
        try{
            transport.call(null,envelope);
            SoapObject object=(SoapObject)envelope.bodyIn;
            System.out.println(object.toString());
            if(null==object.getProperty(0).toString()){
                return 0;
            }
            String result=object.getProperty(0).toString();
//            System.out.println(result);
            int count=Integer.parseInt(result);
            return count;
        }
        catch (IOException |XmlPullParserException |ClassCastException e){
            e.printStackTrace();
        }
        return 0;
    }
    public static List<FolkData> GetFolkInforMation(){
        methodName="Get_Folk_Information";
        soapAction=namespace+"/"+methodName;
        HttpTransportSE transport=new HttpTransportSE(url);
        transport.debug=true;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        String result=Get_Post(soapObject);
        return Json_To_FolkList(result);
    }
    @Nullable
    public static byte[] GetFolkImage(int id){
        methodName="Get_Folk_Image";
        soapAction=namespace+"/"+methodName;
        HttpTransportSE transport=new HttpTransportSE(url);
        transport.debug=true;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("id",id);
        SoapSerializationEnvelope envelope=pre_processSoap(soapObject);
        try{
            transport.call(null,envelope);
            SoapObject object=(SoapObject)envelope.bodyIn;
            String imgCode=object.getProperty(0).toString();
            byte[] imgByte= Base64.decode(imgCode);
//            System.out.println(imgCode);
            return imgByte;
        }
        catch (IOException |XmlPullParserException e){
            e.printStackTrace();
        }
        return null;
    }
    public static List<FolkData> Search_Folk_Info(String searchInfo){
        methodName="Search_Folk_Info";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("searhInfo",searchInfo);
        String result=Get_Post(soapObject);
        return Json_To_FolkList(result);
    }
    public static boolean Add_User_Order(int userID,int orderID){
        methodName="Add_User_Order";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("orderID",orderID);
        String result=Get_Post(soapObject);
        if(null==result){
            return false;
        }
//           System.out.println(result);
        return success.equals(result);
    }
    public static boolean Cancel_User_Order(int userID,int orderID){
        methodName="Cancel_User_Order";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("orderID",orderID);
        String result=Get_Post(soapObject);
        if(null==result){
            return false;
        }
        return success.equals(result);
    }
    public static int Check_User_Order(int userID,int orderID){
        methodName="Check_Is_Order";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        soapObject.addProperty("orderID",orderID);
        String result=Get_Post(soapObject);
        if(null==result){
            return -1;
        }
        return Integer.parseInt(result);
    }
    @Nullable
    private static List<OrderData> Get_User_Orders_ID(int userID){
        methodName="Get_User_Order";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userID",userID);
        String result=Get_Post(soapObject);
        if(null==result){
            return null;
        }
        return Json_To_OrderList(result);
    }
    public static FolkData Get_User_Order_Information(int id){
        methodName="Get_Folk_Single_Information";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("id",id);
        String result=Get_Post(soapObject);
        return Json_To_SingleFolkData(result);
    }
    @Nullable
    public static List<FolkData> Get_User_Orders(int userID){
        List<OrderData> datas=Get_User_Orders_ID(userID);
        if(null==datas){
            return null;
        }
        List<FolkData> Orders=new ArrayList<>();
        for(int i=0;i<datas.size();i++){
            Orders.add(Get_User_Order_Information(datas.get(i).getOrderID()));
        }
        return Orders;
    }
}
