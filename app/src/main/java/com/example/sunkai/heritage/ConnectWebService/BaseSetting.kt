package com.example.sunkai.heritage.ConnectWebService

import android.os.Build
import com.example.sunkai.heritage.value.HOST
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/*
 * Created by sunkai on 2018/1/30.
 */

abstract class BaseSetting {
    companion object {
        const val SUCCESS = "SUCCESS"
        const val ERROR = "ERROR"
//        const val URL = "http://btbudinner.win:8080"
        const val URL=HOST
//        const val URL="https://10.0.2.2:8080"
//        const val URL="https://10.20.254.82:8080"
    }
    //定义扩展方法，简单化Gson的使用
    fun <T> fromJsonToList(s: String, clazz: Class<Array<T>>): List<T> {
        val arr = Gson().fromJson(s, clazz)
        return arr.toList()
    }

    fun PutGet(url:String):String{
        val request=Request.Builder().url(url).build()
        val client=OkHttpClient.Builder().sslSocketFactory(getSSLSocketFactory()).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build()
        try {
            val response = client.newCall(request).execute()
            return response.body()?.string() ?: ERROR
        }catch (e:IOException){
            e.printStackTrace()
        }
        return ERROR
    }

    fun PutPost(url:String,form:FormBody):String{
        val request=Request.Builder().url(url).post(form).build()
        val client=OkHttpClient.Builder().sslSocketFactory(getSSLSocketFactory()).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build()
        try {
            val response = client.newCall(request).execute()
            return response?.body()?.string() ?: ERROR
        }catch (e:IOException){
            e.printStackTrace()
        }
        return ERROR
    }

    @Throws(Exception::class)
    fun getSSLSocketFactory(): SSLSocketFactory {
        //创建一个不验证证书链的证书信任管理器。
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String) {
            }
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts,
                java.security.SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        return sslContext
                .socketFactory
    }
}
