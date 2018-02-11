package com.example.sunkai.heritage.Data

import java.io.Serializable
import kotlin.jvm.internal.Intrinsics

/**
 * Created by sunkai on 2018/1/18.
 */
class FolkDataLite(val id:Int,
                   val divide:String,
                   val title:String,
                   val img:String,
                   val apply_location:String,
                   val category:String):Serializable
