package com.example.sunkai.heritage.connectWebService

import com.example.sunkai.heritage.entity.FolkData
import com.example.sunkai.heritage.value.CLASIIFY_DIVIDE
import org.junit.Assert
import org.junit.Test

class HandleFolkTest:BaseTestClass() {

    @Test
    fun getFolkInforMation() {
        val result=HandleFolk.GetFolkInforMation()
        Assert.assertNotNull(result)
        assertData(result!!)
    }

    @Test
    fun search_Folk_Info() {
        val result=HandleFolk.Search_Folk_Info("山东")
        Assert.assertNotNull(result)
        assertData(result)
    }

    @Test
    fun get_Channel_Folk_Single_Information() {
        Assert.assertSame(FolkData::class.java,HandleFolk.Get_Channel_Folk_Single_Information(1)!!::class.java)
    }

    @Test
    fun get_Main_Divide_Activity_Image_Url() {
        assertData(HandleFolk.Get_Main_Divide_Activity_Image_Url()!!)
    }

    @Test
    fun getChannelInformation() {
        CLASIIFY_DIVIDE.forEach {
            assertData(HandleFolk.GetChannelInformation(it))
        }
    }
}