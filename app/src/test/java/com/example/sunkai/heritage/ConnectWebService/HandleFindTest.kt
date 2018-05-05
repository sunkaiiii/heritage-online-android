package com.example.sunkai.heritage.ConnectWebService

import com.example.sunkai.heritage.Adapter.ActivityRecyclerViewAdapter
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.value.ERROR
import org.junit.Assert
import org.junit.Test

class HandleFindTest:BaseTestClass() {

    @Test
    fun add_User_Comment_Information() {
    }

    @Test
    fun getUserCommentInformation() {
        assertData(HandleFind.GetUserCommentInformation(0))
        assertData(HandleFind.GetUserCommentInformation(123213214))
    }

    @Test
    fun getUserCommentInformationByUser(){
        assertData(HandleFind.GetUserCommentInformationByUser(55))
        assertEmptyData(HandleFind.GetUserCommentInformationByUser(0))
    }

    @Test
    fun getUserCommentInformaitonByOwn() {
        assertData(HandleFind.GetUserCommentInformaitonByOwn(12))
        assertEmptyData(HandleFind.GetUserCommentInformaitonByOwn(0))
    }

    @Test
    fun getUserCommentIdByUser() {
        assertData(HandleFind.GetUserCommentIdByUser(12).toList())
        assertEmptyData(HandleFind.GetUserCommentIdByUser(0).toList())
    }

    @Test
    fun deleteUserCommentByID() {
        Assert.assertFalse(HandleFind.DeleteUserCommentByID(141214))
    }

    @Test
    fun getUserCommentImageUrl() {
        Assert.assertTrue(HandleFind.GetUserCommentImageUrl(1)!= ERROR)
    }

    @Test
    fun getAllUserCommentInfoByID() {
        Assert.assertSame(UserCommentData::class.java,HandleFind.GetAllUserCommentInfoByID(13,1)!!::class.java)
    }

    @Test
    fun getCommentLikeNumber() {
        Assert.assertTrue(HandleFind.GetCommentLikeNumber(13).toInt()>0)
    }

    @Test
    fun getUserCommentCount() {
        Assert.assertTrue(HandleFind.GetUserCommentCount(9).toInt()>0)
    }

    @Test
    fun getUserCommentReply() {
        assertData(HandleFind.GetUserCommentReply(9))
    }

    @Test
    fun setUserLike() {
        Assert.assertTrue(HandleFind.SetUserLike(12,1))
    }

    @Test
    fun cancelUserLike() {
        Assert.assertTrue(HandleFind.CancelUserLike(12,1))
    }

    @Test
    fun getUserLikeComment() {
        assertData(HandleFind.GetUserLikeComment(55))
    }

    @Test
    fun testSearchUserComment(){
        assertData(HandleFind.SearchUserCommentInfo("非遗"))
    }

}