package com.example.sunkai.heritage.ConnectWebService

import com.example.sunkai.heritage.Data.FindActivityData
import okhttp3.FormBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.kobjects.base64.Base64
import org.ksoap2.serialization.SoapObject

/*
 * Created by sunkai on 2018/2/1.
 */
object HandleFindNew:BaseSettingNew() {
    private fun Json_To_FindAcitivityID(json: String?): List<FindActivityData>? {
        try {
            val datas=ArrayList<FindActivityData>()
            val replys = JSONArray(json)
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
    private fun Json_To_FindActivityInformation(id: Int, json: String): FindActivityData? {
        try {
            val getdata = FindActivityData()
            getdata.id = id
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
    fun Get_Find_Activity_ID():List<FindActivityData>?{
        val getUrl= URL+"/GetFindActivityID"
        val result=PutGet(getUrl)
        if(ERROR!=result){
            return Json_To_FindAcitivityID(result)
        }
        return null
    }

    fun Get_Find_Activity_Information(id:Int):FindActivityData?{
        val getUrl= URL+"/GetFindActivityInformation?id="+id.toString()
        val result=PutGet(getUrl)
        if(ERROR!=result){
            return Json_To_FindActivityInformation(id,result)
        }
        return null
    }

    fun Add_User_Comment_Information(user_id: Int, comment_title: String, comment_content: String, comment_image: String): Boolean {
        val postUrl=URL+"/AddUserCommentInformation"
        val form=FormBody.Builder()
                .add("userID",user_id.toString())
                .add("commentTitle",comment_title)
                .add("commentContent",comment_content)
                .add("commentImage",comment_image)
                .build()
        val result=PutPost(postUrl,form)
        return SUCCESS==result
    }

}