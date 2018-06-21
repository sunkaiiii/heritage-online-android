package com.example.sunkai.heritage.ConnectWebService

import android.util.Base64
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Data.*
import com.example.sunkai.heritage.value.MINI_REPLY
import com.google.gson.Gson
import okhttp3.FormBody

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

    fun GetUserAllInfo(userID: Int,thisUserID:Int= LoginActivity.userID): UserInfo? {
        val getUrl = "$URL/GetUserAllInfo?userID=$userID"
        val result = PutGet(getUrl)
        if (result == ERROR) {
            return null
        }
        try {
            val data= Gson().fromJson(result, UserInfo::class.java)
            if(data.id==thisUserID){
                data.checked=false
            }else{
                data.checked= IsUserFollow(thisUserID,userID)
            }
            return data
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    fun GetFollowInformation(userID: Int): List<FollowInformation> {
        val getUrl = "$URL/GetFollowInformation?userID=$userID"
        val result = PutGet(getUrl)
        if (result == ERROR) {
            return arrayListOf()
        }
        try{
            return fromJsonToList(result, Array<FollowInformation>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetFansInformation(userID: Int):List<FollowInformation>{
        val getUrl="$URL/GetFansInformation?userID=$userID"
        val result = PutGet(getUrl)
        if (result == ERROR) {
            return arrayListOf()
        }
        try{
            return fromJsonToList(result, Array<FollowInformation>::class.java)
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
        if(userID==focusID)
            return false
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

    fun GetSearchUserInfo(searchName:String,id:Int):List<SearchUserInfo>{
        val getUrl="$URL/GetSearchUserInfo?searchName=$searchName"
        val result=PutGet(getUrl)
        if(result== ERROR){
            return arrayListOf()
        }
        try{
            val searchInfo= fromJsonToList(result, Array<SearchUserInfo>::class.java)
            searchInfo.forEach { it.checked= IsUserFollow(id,it.id) }
            return searchInfo
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
        val imageString= Base64.encodeToString(imageByteArray,Base64.DEFAULT)
        val postUrl="$URL/UpdateUserImage"
        val formBody=FormBody.Builder().add("userID",userID.toString()).add("image",imageString).build()
        val result=PutPost(postUrl,formBody)
        return result== SUCCESS
    }

    fun AddUserCollection(userID: Int,collectionType:String,typeID:Int):Boolean{
        val getUrl="$URL/AddUserCollection?userID=$userID&typeID=$typeID&type=$collectionType"
        return SUCCESS==PutGet(getUrl)
    }

    fun CancelUserCollect(userID: Int,collectionType:String,typeID:Int):Boolean{
        val getUrl="$URL/CancelUserCollect?userID=$userID&typeID=$typeID&type=$collectionType"
        return SUCCESS==PutGet(getUrl)
    }

    fun CheckIsCollection(userID: Int,collectionType:String,typeID:Int):Boolean{
        val getUrl="$URL/CheckIsCollection?userID=$userID&typeID=$typeID&type=$collectionType"
        val result=PutGet(getUrl)
        return SUCCESS==result
    }

    private fun GetUserCollection(userID: Int,collectionType: String):String{
        val getUrl="$URL/GetUserCollection?userID=$userID&type=$collectionType"
        val result=PutGet(getUrl)
        return result
    }

    fun GetMainCollection(userID: Int,collectionType: String):List<FolkNewsLite>{
        val result=GetUserCollection(userID,collectionType)
        if(ERROR==result)
            return arrayListOf()
        try{
            return fromJsonToList(result, Array<FolkNewsLite>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetFocusOnHeritageCollection(userID: Int,collectionType: String):List<BottomFolkNewsLite>{
        val result=GetUserCollection(userID,collectionType)
        if(ERROR==result)
            return arrayListOf()
        try{
            return fromJsonToList(result, Array<BottomFolkNewsLite>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetFolkColelction(userID: Int,collectionType: String):List<ClassifyDivideData>{
        val result=GetUserCollection(userID,collectionType)
        if(ERROR==result)
            return arrayListOf()
        try{
            return fromJsonToList(result, Array<ClassifyDivideData>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetFindCollection(userID: Int,collectionType: String):List<UserCommentData>{
        val result=GetUserCollection(userID,collectionType)
        if(ERROR==result)
            return arrayListOf()
        try{
            val findResult= fromJsonToList(result, Array<UserCommentData>::class.java)
            findResult.forEach { it.miniReplys=HandleFind.GetUserCommentReply(it.id, MINI_REPLY) }
            return findResult
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }
}