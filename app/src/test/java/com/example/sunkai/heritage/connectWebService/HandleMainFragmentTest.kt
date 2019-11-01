package com.example.sunkai.heritage.connectWebService

import com.example.sunkai.heritage.value.CATEGORIES
import org.junit.Test

import org.junit.Assert.*

class HandleMainFragmentTest : BaseTestClass() {

    @Test
    fun readMainNews() {
        val result = HandleMainFragment.ReadMainNews()
        assertData(result)
        result.forEach {
            assertData(it)
        }
    }

    @Test
    fun getFolkNewsList() {
        CATEGORIES.forEach {
            val result = HandleMainFragment.GetFolkNewsList(it, 0, 20)
            assertData(result)
            result.forEach {
                getFolkNewsInformation(it.id)
            }
        }
    }



    @Test
    fun getMainPageSlideNewsInfo() {
        val result = HandleMainFragment.GetMainPageSlideNewsInfo()
        assertNotNull(result)
        assertData(result!!)
        result.forEach {
            getMainPageSlideDetailInfo(it.content)
        }
    }

    @Test
    fun SearchBottomNewsInfoTest(){
        assertData(HandleMainFragment.SearchBottomNewsInfo("地方"))
    }

    @Test
    fun SearchAllNewsInfoTest(){
        assertData(HandleMainFragment.SearchAllNewsInfo("非遗"))
    }
    

    private fun getFolkNewsInformation(id: Int) {
        println(id)
        assertData(HandleMainFragment.GetFolkNewsInformation(id))
    }

    private fun getMainPageSlideDetailInfo(content: String) {
        println(content)
        assertData(HandleMainFragment.GetMainPageSlideDetailInfo(content))
    }
}