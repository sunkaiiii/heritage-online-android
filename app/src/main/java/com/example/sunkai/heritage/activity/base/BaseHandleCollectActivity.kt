package com.example.sunkai.heritage.activity.base

import android.view.Menu
import android.view.MenuItem
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.interfaces.HandleCollect
import com.example.sunkai.heritage.tools.MakeToast.toast

/**
 * 可以收藏的Activity页面基类
 * Created by sunkai on 2018/3/14.
 */
abstract class BaseHandleCollectActivity : BaseGlideActivity(), HandleCollect {
    private var menuItem: Menu? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_collect_menu, menu)
        menuItem = menu
        getCollectInfo()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.user_collect -> {
                val findItem = menuItem?.findItem(R.id.user_collect) ?: return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //判断是否已经收藏了这个页面，如果已经收藏，则更新按钮状态
    private fun getCollectInfo() {
        val item = menuItem?.findItem(R.id.user_collect) ?: return
        val id = getID() ?: return
    }

    //TODO 重做收藏，改为本地
    override fun handleCollect(item: MenuItem) {

    }

    override fun checkIsCollect(userID: Int, typeName: String, typeID: Int): Boolean {
        return false
    }

    override fun setItemState(item: MenuItem, success: Boolean, checked: Boolean, showToast: Boolean) {
        item.isEnabled = true
        if (success) {
            item.setIcon(if (checked) R.drawable.ic_bookmark_white_24dp else R.drawable.ic_bookmark_border_white_24dp)
            val toastString = if (checked) "添加收藏成功" else "取消收藏成功"
            if (showToast) {
                toast(toastString)
            }
        } else {
            toast("出现问题，请稍后再试")
        }
    }


    abstract fun getType(): String
    abstract fun getID(): Int?
}