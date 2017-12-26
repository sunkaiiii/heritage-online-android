package com.example.sunkai.heritage.ConnectWebService;

import android.support.annotation.Nullable;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by sunkai on 2017/3/23.
 * 此类封装了一些类的初始化的方法
 *
 */

public class BaseSetting extends WebServiceSetting{
    static protected String methodName;
    static protected String soapAction;
    static protected String success="Success";
    static protected String error="Error";
    private static final String TAG = "BaseSetting";
    static protected SoapSerializationEnvelope pre_processSoap(SoapObject soapObject)
    {
        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=false;
        envelope.bodyOut=soapObject;
        envelope.setOutputSoapObject(soapObject);
        return envelope;
    }
    @Nullable
    protected static String Get_Post(SoapObject soapObject){
        try{
            HttpTransportSE transport=new HttpTransportSE(url);
            transport.debug=true;
            SoapSerializationEnvelope envelope=pre_processSoap(soapObject);
            transport.call(null,envelope);
            if(envelope.bodyIn instanceof SoapObject) {
                SoapObject object = (SoapObject) envelope.bodyIn;
                System.out.println(object.toString());
                if (null==object||null==object.getProperty(0)||null == object.getProperty(0).toString()) {
                    return null;
                } else {
                    return object.getProperty(0).toString();
                }
            }
            else{
                transport.call(null,envelope);
                if(envelope.bodyIn instanceof SoapObject){
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    System.out.println(object.toString());
                    if (null == object.getProperty(0).toString()) {
                        return null;
                    } else {
                        return object.getProperty(0).toString();
                    }
                }
                return null;
            }
        }
        catch (IOException |XmlPullParserException |ClassCastException e){
            e.printStackTrace();
        }
        return null;
    }
    static public void Change_IP(String ip){
        url = "http://" +ip+
                ":8088/services/Heritage?wsdl";
    }
}
