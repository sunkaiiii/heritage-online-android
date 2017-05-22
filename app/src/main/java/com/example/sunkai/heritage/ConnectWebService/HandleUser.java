package com.example.sunkai.heritage.ConnectWebService;

/**
 * Created by sunkai on 2017/3/22.
 * 此类封装了关于登录页有关的服务器请求的方法
 */
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class HandleUser  extends BaseSetting{
    @NonNull
    public static Boolean Sign_in(String userName, String PassWord){
        methodName="Sign_In";
        soapAction = namespace + "/"+methodName;
        HttpTransportSE transport=new HttpTransportSE(url);
        transport.debug=true;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userName",userName);
        soapObject.addProperty("PassWord",PassWord);
        SoapSerializationEnvelope envelope=pre_processSoap(soapObject);
        try{
            transport.call(null,envelope);
            SoapObject object=(SoapObject)envelope.bodyIn;
            System.out.println(object.toString());
            String result=object.getProperty(0).toString();
            if(success.equals(result)){
                return true;
            }
            else{
                return false;
            }
        }
        catch (IOException|XmlPullParserException e){
            e.printStackTrace();
        }
        return false;
    }
    public static int User_Regist(String userName,String passWord,String findPasswordQuestion,String findPassWordAnswer){
        methodName="User_Regist";
        soapAction = namespace + "/"+methodName;
        HttpTransportSE transport=new HttpTransportSE(url);
        transport.debug=true;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userName",userName);
        soapObject.addProperty("passWord",passWord);
        soapObject.addProperty("findPasswordQuestion",findPasswordQuestion);
        soapObject.addProperty("findPassWordAnswer",findPassWordAnswer);
        SoapSerializationEnvelope envelope=pre_processSoap(soapObject);
        try{
            transport.call(null,envelope);
            SoapObject object=(SoapObject)envelope.bodyIn;
            System.out.println(object.toString());
            String result=object.getProperty(0).toString();
            if(success.equals(result)){
                return 1;
            }
            else if("hadUser".equals(result)){
                return 0;
            }
            else{
                return -1;
            }
        }
        catch (IOException|XmlPullParserException e){
            e.printStackTrace();
        }
        return -1;
    }
    public static int Get_User_ID(String userName){
        methodName="Get_User_ID";
        soapAction = namespace + "/"+methodName;
        HttpTransportSE transport=new HttpTransportSE(url);
        transport.debug=true;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userName",userName);
        SoapSerializationEnvelope envelope=pre_processSoap(soapObject);
        try{
            transport.call(null,envelope);
            SoapObject object=(SoapObject)envelope.bodyIn;
            System.out.println(object.toString());
            String result=object.getProperty(0).toString();
            if(Integer.parseInt(result)>0){
                return Integer.parseInt(result);
            }
            else{
                return -1;
            }
        }
        catch (IOException|XmlPullParserException e){
            e.printStackTrace();
        }
        return -1;
    }
    @Nullable
    public static String Find_Password_Question(String userName){
        methodName="Find_Password_Question";
        soapAction = namespace + "/"+methodName;
        HttpTransportSE transport=new HttpTransportSE(url);
        transport.debug=true;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userName",userName);
        SoapSerializationEnvelope envelope=pre_processSoap(soapObject);
        try{
            transport.call(null,envelope);
            SoapObject object=(SoapObject)envelope.bodyIn;
            System.out.println(object.toString());
            String result=object.getProperty(0).toString();
            return result;
        }
        catch (IOException|XmlPullParserException e){
            e.printStackTrace();
        }
        return null;
    }
    public static boolean Check_Question_Answer(String userName,String questionAnswer){
        methodName="Check_Question_Answer";
        soapAction = namespace + "/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userName",userName);
        soapObject.addProperty("questionAnswer",questionAnswer);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean Change_Password(String userName,String Password){
        methodName="Change_Password";
        soapAction=namespace+"/"+methodName;
        SoapObject soapObject=new SoapObject(namespace,methodName);
        soapObject.addProperty("userName",userName);
        soapObject.addProperty("Password",Password);
        String result=Get_Post(soapObject);
        if(success.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
}
