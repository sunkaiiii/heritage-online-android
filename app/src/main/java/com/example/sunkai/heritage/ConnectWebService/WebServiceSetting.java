package com.example.sunkai.heritage.ConnectWebService;

/**
 * Created by sunkai on 2017/3/22.
 */

/**
 * 此类是所有连接Webservice的类的基类，用于配置连接Webservice的IP地址和命名空间
 */



public class WebServiceSetting {
    protected static String namespace = "http://Handle";
    protected static String url = "http://btbudinner.win" +
            ":8088/services/Heritage?wsdl";
//    protected static String url = "http://10.66.1.217" +
//        ":8088/services/Heritage?wsdl";
//    protected static String url = "http://10.100.241.124" +
//            ":8088/services/Heritage?wsdl";
//        protected static String url = "http://192.168.1.8" +
//            ":8088/services/Heritage?wsdl";
}
