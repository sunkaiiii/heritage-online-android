package com.example.sunkai.heritage.ConnectWebService;

import android.support.annotation.Nullable;

import com.example.sunkai.heritage.Data.MainActivityData;
import com.example.sunkai.heritage.Data.ClassifyActiviyData;

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
 * Created by sunkai on 2017/3/23.
 * 此类封装了有关首页相关的各类服务器请求的方法
 */

public class HandleMainFragment extends BaseSetting  {
    @Nullable
    public static List<MainActivityData> ReadMainActivity(){
        methodName="Read_Main_Activity";
        soapAction = namespace + "/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        String result=Get_Post(soapObject);
        if(!error.equals(result)&&result!=null) {
            try {
                JSONObject MainActivity = new JSONObject(result);
                JSONArray activities = MainActivity.getJSONArray("main_Activity");
                List<MainActivityData> activityDatas = new ArrayList<>();
                for (int i = 0; i < activities.length(); i++) {
                    MainActivityData data = new MainActivityData();
                    JSONObject activity = (JSONObject) activities.get(i);
                    data.setId(Integer.valueOf((String) activity.get("id")));
                    data.setActivityTitle((String) activity.get("activity_title"));
                    data.setActivityContent((String) activity.get("activity_content"));
                    String imgCode = (String) activity.get("activity_image");
                    data.setActivityImage(Base64.decode(imgCode));
                    activityDatas.add(data);
                }
                return activityDatas;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Nullable
    public static List<ClassifyActiviyData> GetChannelInformation(String channel){
        methodName="Get_Channel_Information";
        soapAction = namespace + "/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("channel",channel);
        String result=Get_Post(soapObject);
        try {
            if (result == null || error.equals(result))
                return null;
            JSONObject MainActivity = new JSONObject(result);
            JSONArray activities = MainActivity.getJSONArray("classify_activity");
            List<ClassifyActiviyData> activityDatas = new ArrayList<>();
            for (int i = 0; i < activities.length(); i++) {
                ClassifyActiviyData data = new ClassifyActiviyData();
                JSONObject activity = (JSONObject) activities.get(i);
                data.setId(Integer.valueOf((String) activity.get("id")));
                data.setActivityTitle((String) activity.get("activity_title"));
                data.setActivityContent((String) activity.get("activity_content"));
                data.setActivityChannel((String) activity.get("activity_channel"));
                activityDatas.add(data);
            }
            return activityDatas;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    public static byte[] GetChannelImage(int id){
        methodName="Get_Channel_Image";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("id",id);
        String imgCode = Get_Post(soapObject);
        if(null==imgCode||error.equals(imgCode)){
            return null;
        }
        byte[] imgByte = Base64.decode(imgCode);
        return imgByte;
    }
}
