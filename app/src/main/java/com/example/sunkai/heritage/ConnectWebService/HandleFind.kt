package com.example.sunkai.heritage.ConnectWebService

import com.example.sunkai.heritage.Data.FindActivityAllData
import com.example.sunkai.heritage.Data.FindActivityData
import com.example.sunkai.heritage.Data.CommentReplyData
import com.example.sunkai.heritage.Data.UserCommentData

import org.json.JSONException
import org.json.JSONObject
import org.kobjects.base64.Base64
import org.ksoap2.serialization.SoapObject

import java.util.ArrayList

/**
 * Created by sunkai on 2018-1-9.
 * 此类封装了发现页相关服务器请求的方法
 */

object HandleFind : BaseSetting() {
    private fun Json_To_UserCommentData(json: String?): List<UserCommentData>? {
        try {
            if (null == json) {
                return null
            }
            val MainActivity = JSONObject(json)
            val activities = MainActivity.getJSONArray("user_comment_information")
            val folkInformations = ArrayList<UserCommentData>()
            for (i in 0 until activities.length()) {
                val data = UserCommentData()
                val activity = activities.get(i) as JSONObject
                data.id = activity.get("id") as Int
                data.user_id = activity.get("user_id") as Int
                data.commentTime = activity.get("comment_time") as String
                data.commentTitle = activity.get("comment_title") as String
                data.commentContent = activity.get("comment_content") as String
                data.commentLikeNum = activity.get("comment_num") as String
                data.commentReplyNum = activity.get("reply_num") as String
                data.userName = activity.get("user_name") as String
                data.isUserFocusUser = BaseSetting.success == activity.get("isFollow")
                data.isUserLike = BaseSetting.success == activity.get("isLike")
                folkInformations.add(data)
            }
            return folkInformations
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    private fun Json_To_UserCommentReplyData(json: String?): List<CommentReplyData>? {
        try {
            if (json == null)
                return null
            val reply = JSONObject(json)
            val replys = reply.getJSONArray("reply_information")
            val datas = ArrayList<CommentReplyData>()
            for (i in 0 until replys.length()) {
                val data = CommentReplyData()
                val oneReply = replys.get(i) as JSONObject
                data.replyId = oneReply.get("reply_id") as Int
                data.replyTime = oneReply.get("reply_time") as String
                data.replyContent = oneReply.get("reply_content") as String
                data.userName = oneReply.get("user_name") as String
                datas.add(data)
            }
            return datas

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    private fun Json_To_FindAcitivityID(datas: MutableList<FindActivityData>, json: String?): List<FindActivityData>? {
        if (BaseSetting.error == json || json == null)
            return null
        try {
            val js = JSONObject(json)
            val replys = js.getJSONArray("id")
            for (i in 0 until replys.length()) {
                val data = FindActivityData()
                data.id = replys.get(i) as Int
                datas.add(data)
            }
            return datas
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    private fun Json_To_FindActivityInformation(id: Int, json: String?): FindActivityData? {
        try {
            val getdata = FindActivityData()
            getdata.id = id
            if (json == null)
                return null
            val js = JSONObject(json)
            getdata.title = js.getString("title")
            getdata.content = js.getString("content")
            val imgCode = js.getString("image")
            getdata.image = Base64.decode(imgCode)
            return getdata
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    private fun Json_To_FindComment_ID(json: String?): List<Int>? {
        try {
            val js = JSONObject(json)
            val jsonArray = js.getJSONArray("members")
            val ids = ArrayList<Int>()
            for (i in 0 until jsonArray.length()) {
                ids.add(jsonArray.getInt(i))
            }
            return ids
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }

    }

    private fun Json_To_FindActivityAllInformation(json: String?): FindActivityAllData? {
        try {
            val js = JSONObject(json)
            val data = FindActivityAllData()
            data.id = js.getInt("id")
            data.userName = js.getString("userName")
            data.userID = js.getInt("userID")
            data.coment_time = js.getString("comment_time")
            data.comment_title = js.getString("comment_title")
            data.comment_content = js.getString("comment_content")
            data.replyCount = js.getString("replyCount")
            data.isUserLike = BaseSetting.success == js.getString("isUserLike")
            data.likeNumber = js.getString("likeNumber")
            data.isUserFlow = BaseSetting.success == js.getString("isUserFllow")
            data.imgCode = js.getString("image")
            return data
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }

    }

    fun Get_Find_Activity_ID(datas: List<FindActivityData>): List<FindActivityData>? {
        BaseSetting.methodName = "Get_Find_Activity_ID"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_FindAcitivityID(datas.toMutableList(), result)
    }

    fun Get_Find_Activity_Information(id: Int): FindActivityData? {
        BaseSetting.methodName = "Get_Find_Activity_Information"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("id", id)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_FindActivityInformation(id, result)
    }

    fun Add_User_Comment_Information(user_id: Int, comment_title: String, comment_content: String, comment_image: String): Boolean {
        BaseSetting.methodName = "Add_User_Comment_Information"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("user_id", user_id)
        soapObject.addProperty("comment_title", comment_title)
        soapObject.addProperty("comment_content", comment_content)
        soapObject.addProperty("comment_image", comment_image)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Delete_User_Comment_By_ID(commentID: Int): Boolean {
        BaseSetting.methodName = "Delete_User_Comment_By_ID"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("commentID", commentID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Update_User_Comment_Informaiton(data: UserCommentData): Boolean {
        BaseSetting.methodName = "Update_User_Comment_Informaiton"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("commentID", data.id)
        soapObject.addProperty("comment_title", data.commentTitle)
        soapObject.addProperty("comment_content", data.commentContent)
        soapObject.addProperty("comment_image", Base64.encode(data.userImage!!))
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Get_User_Comment_Information(userID: Int): List<UserCommentData>? {
        BaseSetting.methodName = "Get_User_Comment_Information"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_UserCommentData(result)
    }

    fun Get_User_Comment_Information_By_User(userID: Int): List<UserCommentData>? {
        BaseSetting.methodName = "Get_User_Comment_Information_By_User"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_UserCommentData(result)
    }

    fun Get_User_Comment_Information_By_Own(userID: Int): List<UserCommentData>? {
        BaseSetting.methodName = "Get_User_Comment_Information_By_Own"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_UserCommentData(result)
    }

    fun Get_User_Comment_ID(): List<Int>? {
        BaseSetting.methodName = "Get_User_Comment_ID"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.error == result) null else Json_To_FindComment_ID(result)
    }

    fun Get_User_Comment_ID_By_User(userID: Int): List<Int>? {
        BaseSetting.methodName = "Get_User_Comment_ID_By_User"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.error == result) null else Json_To_FindComment_ID(result)
    }

    fun Get_All_User_Coment_Info_By_ID(userID: Int, commentID: Int): FindActivityAllData? {
        BaseSetting.methodName = "Get_All_User_Coment_Info_By_ID"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("user", userID)
        soapObject.addProperty("commentID", commentID)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.error == result) null else Json_To_FindActivityAllInformation(result)
    }

    fun Get_User_Comment_Image(id: Int): ByteArray? {
        BaseSetting.methodName = "Get_User_Comment_Image"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("id", id)
        val result = BaseSetting.Get_Post(soapObject)
        return if (null == result || BaseSetting.error == result) {
            null
        } else Base64.decode(result)
    }

    fun Get_User_Is_Like(userID: Int, commentID: Int): Boolean {
        BaseSetting.methodName = "Get_User_Is_Like"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("commentID", commentID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Set_User_Like(userID: Int, commentID: Int): Boolean {
        BaseSetting.methodName = "Set_User_Like"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("commentID", commentID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Cancel_User_Like(userID: Int, commentID: Int): Boolean {
        BaseSetting.methodName = "Cancel_User_Like"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("commentID", commentID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Get_User_Comment_Reply(commentID: Int): List<CommentReplyData>? {
        BaseSetting.methodName = "Get_User_Comment_Reply"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("commentID", commentID)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_UserCommentReplyData(result)
    }

    fun Add_User_Comment_Reply(userID: Int, commentID: Int, replyContent: String, intentString: String): Int {
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        BaseSetting.methodName = "Add_User_Comment_Reply"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("commentID", commentID)
        soapObject.addProperty("replyContent", replyContent)
        soapObject.addProperty("intentString", intentString)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.error == result) {
            0
        } else {
            Integer.parseInt(result)
        }
    }

    fun Update_User_Comment_Reply(replyID: Int, replyContent: String): Boolean {
        BaseSetting.methodName = "Update_User_Comment_Reply"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("replyID", replyID)
        soapObject.addProperty("replyContent", replyContent)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Delete_User_Comment_Reply(replyID: Int): Boolean {
        BaseSetting.methodName = "Update_User_Comment_Reply"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("replyID", replyID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Get_User_Comment_Count(commentID: Int): Int {
        BaseSetting.methodName = "Get_User_Comment_Count"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("commentID", commentID)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.error == result) {
            0
        } else {
            Integer.parseInt(result)
        }
    }
}
