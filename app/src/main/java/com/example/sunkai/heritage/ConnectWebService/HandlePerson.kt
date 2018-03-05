package com.example.sunkai.heritage.ConnectWebService

import android.util.Log
import com.example.sunkai.heritage.Data.FollowInformation
import com.example.sunkai.heritage.Data.SearchUserInfo
import com.example.sunkai.heritage.Data.UserInfo
import com.google.gson.Gson
import okhttp3.FormBody
import org.kobjects.base64.Base64

/**个人中心相关的类
 * Created by sunkai on 2018/2/22.
 */
object HandlePerson : BaseSetting() {
    fun GetFollowNumber(userID: Int): Int {
        val getUrl = "$URL/GetFollowNumber?userID=$userID"
        val result = PutGet(getUrl)
        return if (result == ERROR) 0 else result.toInt()
    }

    fun GetFansNumber(userID: Int): Int {
        val getUrl = "$URL/GetFansNumber?userID=$userID"
        val result = PutGet(getUrl)
        return if (result == ERROR) 0 else result.toInt()
    }

    fun GetUserPermission(userID: Int): Int {
        val getUrl = "$URL/GetUserPermission?userID=$userID"
        val result = PutGet(getUrl)
        return if (result == ERROR) -1 else result.toInt()
    }

    fun GetUserFocusAndFansViewPermission(userID: Int): Int {
        val getUrl = "$URL/GetUserFocusAndFansViewPermission?userID=$userID"
        val result = PutGet(getUrl)
        return if (result == ERROR) -1 else result.toInt()
    }

    fun SetUserPermission(userID: Int, permission: Int): Boolean {
        val getUrl = "$URL/SetUserPermission?userID=$userID&permission=$permission"
        val result = PutGet(getUrl)
        return SUCCESS == result
    }

    fun SetUserFocusAndFansViewPermission(userID: Int, permission: Int): Boolean {
        val getUrl = "$URL/SetUserFocusAndFansViewPermission?userID=$userID&permission=$permission"
        val result = PutGet(getUrl)
        return SUCCESS == result
    }

    fun GetUserAllInfo(userID: Int): UserInfo? {
        val getUrl = "$URL/GetUserAllInfo?userID=$userID"
        val result = PutGet(getUrl)
        Log.d("GetUserAllInfo",result)
        if (result == ERROR) {
            return null
        }
        try {
            return Gson().fromJson(result, UserInfo::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    fun GetFollowInformation(userID: Int): List<FollowInformation> {
        val getUrl = "$URL/GetFollowInformation?userID=$userID"
        val result = PutGet(getUrl)
        Log.d("getFollowInfo",result)
        if (result == ERROR) {
            return arrayListOf()
        }
        try{
            return Gson().fromJsonToList(result,Array<FollowInformation>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetFansInformation(userID: Int):List<FollowInformation>{
        val getUrl="$URL/GetFansInformation?userID=$userID"
        val result = PutGet(getUrl)
        Log.d("getFansInfo",result)
        if (result == ERROR) {
            return arrayListOf()
        }
        try{
            return Gson().fromJsonToList(result,Array<FollowInformation>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    //这两个方法目前服务端还没有对应的方法，预计会在未来解决
    fun GetOtherFollowInformation(userID: Int,otherUserID:Int):List<FollowInformation>{
        val datas= GetFollowInformation(otherUserID)
        datas.map {
            //要把对应的id改为登陆用户的id
            it.focusFocusID=userID
            it.checked= IsUserFollow(userID,it.focusFansID) }
        return datas
    }

    fun GetOtherFansInformation(userID: Int,otherUserID: Int):List<FollowInformation>{
        val datas= GetFansInformation(otherUserID)
        datas.forEach {
            //要把对应的id改为登陆用户的id
            it.focusFocusID=userID
            it.checked= IsUserFollow(userID,it.focusFansID) }
        return datas
    }

    fun AddFocus(userID: Int,focusID:Int):Boolean{
        val getUrl="$URL/AddFocus?userID=$userID&focusID=$focusID"
        val result=PutGet(getUrl)
        return SUCCESS==result
    }

    fun CancelFocus(userID: Int,focusID: Int):Boolean{
        val getUrl="$URL/CancelFocus?userID=$userID&focusID=$focusID"
        val result=PutGet(getUrl)
        return SUCCESS==result
    }

    fun IsUserFollow(userID: Int,fansID:Int):Boolean{
        val gerUrl="$URL/IsUserFollow?userID=$userID&fansID=$fansID"
        return SUCCESS==PutGet(gerUrl)
    }

    fun CheckFollowEachother(userID: Int,focusID: Int):Boolean{
        val getUrl="$URL/CheckFollowEachother?userID=$userID&focusID=$focusID"
        val result=PutGet(getUrl)
        return SUCCESS==result
    }

    fun GetSearchUserInfo(searchName:String):List<SearchUserInfo>{
        val getUrl="$URL/GetSearchUserInfo?searchName=$searchName"
        val result=PutGet(getUrl)
        if(result== ERROR){
            return arrayListOf()
        }
        try{
            return Gson().fromJsonToList(result,Array<SearchUserInfo>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetUserImageURL(userID: Int):String?{
        val getUrl="$URL/GetUserImage?userID=$userID"
        val result=PutGet(getUrl)
        return if(result== ERROR||result.isEmpty()){
            null
        }else{
            URL+result
        }
    }

    fun UpdateUserImage(userID: Int,imageByteArray: ByteArray):Boolean{
        val imageString=Base64.encode(imageByteArray)
        val postUrl="$URL/UpdateUserImage"
        val formBody=FormBody.Builder().add("userID",userID.toString()).add("image",imageString).build()
        val result=PutPost(postUrl,formBody)
        return result== SUCCESS
    }
}