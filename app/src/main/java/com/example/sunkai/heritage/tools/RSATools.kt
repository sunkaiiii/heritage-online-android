package com.example.sunkai.heritage.tools

import android.util.Base64
import com.example.sunkai.heritage.value.ERROR
import java.io.InputStreamReader
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher



/**
 * 处理RSA加密有关的类
 * Created by sunkai on 2018/1/26.
 */


private const val TRANSFORMATION="RSA/None/PKCS1Padding"


fun infoToRSA(infos: String): String? {
    val encrtData= encryptionPassWord(infos)
    return if(ERROR==encrtData) {
        null
    } else encrtData
}

fun encryptionPassWord(password: String):String{
    val byte= readPublicKeyFromFile(password)
    byte?.let{
        return Base64.encodeToString(byte,Base64.DEFAULT)
    }
    return ERROR
}


/** 使用公钥加密  */
private fun encryptByPublicKey(data: ByteArray, publicKey: ByteArray): ByteArray {
    // 得到公钥对象
    val keySpec = X509EncodedKeySpec(publicKey)
    val keyFactory = KeyFactory.getInstance("RSA")
    val pubKey = keyFactory.generatePublic(keySpec)
    // 加密数据
    val cp = Cipher.getInstance(TRANSFORMATION)
    cp.init(Cipher.ENCRYPT_MODE, pubKey)
    return cp.doFinal(data)
}

private fun readPublicKeyFromFile(data:String):ByteArray?{
//读取公钥文件
    val input = GlobalContext.instance.resources.assets.open("public.pem")
    val bufferSize = 1024
    val buffer = CharArray(bufferSize)
    val out = StringBuilder()
    val `in` = InputStreamReader(input, "UTF-8")
    while (true) {
        val rsz = `in`.read(buffer, 0, buffer.size)
        if (rsz < 0)
            break
        out.append(buffer, 0, rsz)
    }
    val outString=out.toString()
    try {
        //加密
        return encryptByPublicKey(data.toByteArray(), Base64.decode(outString,Base64.DEFAULT))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null

}