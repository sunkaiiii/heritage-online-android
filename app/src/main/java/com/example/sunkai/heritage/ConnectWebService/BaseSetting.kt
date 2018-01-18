package com.example.sunkai.heritage.ConnectWebService

import com.google.gson.Gson
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException

/**
 * Created by sunkai on 2018/1/9.
 * 此类封装了一些类的初始化的方法
 *
 */

open class BaseSetting {
    companion object {
        var methodName: String? = null
        var soapAction: String? = null
        val success = "Success"
        val error = "Error"
        val url = "http://btbudinner.win" + ":8088/services/Heritage?wsdl"
        val namespace="http://Handle"

        //定义扩展方法，简单化Gson的使用
        inline fun <reified T:Any> Gson.fromJsonToList(s: String, clazz: Class<Array<T>>): List<T> {
            val arr = Gson().fromJson(s, clazz)
            return arr.toList()
        }
        fun pre_processSoap(soapObject: SoapObject): SoapSerializationEnvelope {
            val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
            envelope.dotNet = false
            envelope.bodyOut = soapObject
            envelope.setOutputSoapObject(soapObject)
            return envelope
        }
        fun Get_Post(soapObject: SoapObject): String? {
            try {
                val transport = HttpTransportSE(url)
                transport.debug = true
                val envelope = pre_processSoap(soapObject)
                transport.call(null, envelope)
                if (envelope.bodyIn is SoapObject) {
                    val `object` = envelope.bodyIn as SoapObject
                    println(`object`.toString())
                    return if (null == `object`.getProperty(0) || null == `object`.getProperty(0).toString()) {
                        null
                    } else {
                        `object`.getProperty(0).toString()
                    }
                } else {
                    transport.call(null, envelope)
                    if (envelope.bodyIn is SoapObject) {
                        val `object` = envelope.bodyIn as SoapObject
                        println(`object`.toString())
                        return if (null == `object`.getProperty(0).toString()) {
                            null
                        } else {
                            `object`.getProperty(0).toString()
                        }
                    }
                    return null
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }

            return null
        }
    }
}
