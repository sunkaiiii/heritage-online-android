package com.example.sunkai.heritage.tools

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ParameterizedTypeImpl(private val clz: Class<*>) : ParameterizedType {
    override fun getRawType(): Type = List::class.java

    override fun getOwnerType(): Type? = clz.javaClass

    override fun getActualTypeArguments(): Array<Type> = arrayOf(clz)
}
