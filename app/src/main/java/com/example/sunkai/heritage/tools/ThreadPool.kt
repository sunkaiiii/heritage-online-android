package com.example.sunkai.heritage.tools

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 通过线程池来进行线程操作（来自阿里巴巴Android开发手册提示）
 * Created by sunkai on 2018/3/5.
 */
    private val NUMBER_OF_CORES=Runtime.getRuntime().availableProcessors()
    private val KEEP_ALIVE_TIME=1.toLong()
    private val KEEP_ALIVE_TIME_UNIT=TimeUnit.SECONDS
    private val taskQueue=LinkedBlockingDeque<Runnable>()
val ThreadPool=ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES*2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, taskQueue)