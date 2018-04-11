package com.example.sunkai.heritage.ConnectWebService

import org.junit.Assert
import org.junit.Test

class HandleFindTest {

    @Test
    fun add_User_Comment_Information() {
    }

    @Test
    fun getUserCommentInformation() {
        val result=HandleFind.GetUserCommentInformation(0)
        Assert.assertNotNull(result)
        Assert.assertTrue(result.isNotEmpty())
        result.forEach {
            println(it.commentTitle)
        }
        val result2=HandleFind.GetUserCommentInformation(123213214)
        Assert.assertNotNull(result2)
        Assert.assertTrue(result2.isNotEmpty())
    }

    @Test
    fun getUserCommentInformationByUser(){
        val result=HandleFind.GetUserCommentInformationByUser(55)
        Assert.assertNotNull(result)
        Assert.assertTrue(result.isNotEmpty())
        result.forEach {
            println(it.commentTitle)
        }
        val emptyResult=HandleFind.GetUserCommentInformationByUser(0)
        Assert.assertNotNull(emptyResult)
        Assert.assertTrue(emptyResult.size==1)
    }

    @Test
    fun getUserCommentInformaitonByOwn() {
        val result=HandleFind.GetUserCommentInformaitonByOwn(12)
        Assert.assertNotNull(result)
        Assert.assertTrue(result.isNotEmpty())
        result.forEach {
            it.commentTitle
        }
        val emptyResult=HandleFind.GetUserCommentInformaitonByOwn(0)
        Assert.assertNotNull(emptyResult)
        Assert.assertTrue(emptyResult.size==1)
    }

    @Test
    fun getUserCommentIdByUser() {
    }

    @Test
    fun deleteUserCommentByID() {
    }

    @Test
    fun updateUserCommentImage() {
    }

    @Test
    fun updateUserCommentInformaiton() {
    }

    @Test
    fun getUserCommentImageUrl() {
    }

    @Test
    fun getAllUserCommentInfoByID() {
    }

    @Test
    fun getCommentLikeNumber() {
    }

    @Test
    fun getUserCommentCount() {
    }

    @Test
    fun getUserCommentReply() {
    }

    @Test
    fun setUserLike() {
    }

    @Test
    fun cancelUserLike() {
    }

    @Test
    fun addUserCommentReply() {
    }

    @Test
    fun updateUserCommentReply() {
    }

    @Test
    fun deleteUserCommentReply() {
    }

    @Test
    fun getUserLikeComment() {
    }
}