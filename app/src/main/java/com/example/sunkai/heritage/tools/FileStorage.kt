package com.example.sunkai.heritage.tools

import android.os.Environment
import java.io.File
import java.util.*

/**
 * Created by sunkai on 2017/12/15.
 *
 */
class FileStorage {
    lateinit var cropIconDir:File
    lateinit var iconDir:File
    init {
        if(Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()){
            val external:File=Environment.getExternalStorageDirectory()
            val rootDir:String="/"+"demo"
            cropIconDir=File(external,rootDir+"/crop")
            if(!cropIconDir.exists())
                cropIconDir.mkdirs()
            iconDir=File(external,rootDir+"/icon")
            if(!iconDir.exists())
                iconDir.mkdirs()
        }
    }

    fun createCropFile():File?{
        cropIconDir.let {
            val fileName=UUID.randomUUID().toString()+".png"
            return File(cropIconDir,fileName)
        }
    }
    fun createIconFile():File?{
        iconDir.let {
            val fileName=UUID.randomUUID().toString()+".png"
            return File(iconDir,fileName)
        }
    }
}
