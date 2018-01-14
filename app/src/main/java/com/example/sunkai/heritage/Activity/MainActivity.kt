package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener

import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Fragment.MainFragment
import com.example.sunkai.heritage.Fragment.PersonFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.Fragment.FindFragment
import com.example.sunkai.heritage.Fragment.FolkFragment
import com.example.sunkai.heritage.tools.BottomNavigationViewHelper

/**
 * 此类用于处理登陆
 */

class MainActivity : AppCompatActivity(), OnClickListener {
    /**
     * 用于展示消息的Fragment
     */
    private var mainFragment: MainFragment?=null

    /**
     * 用于展示联系人的Fragment
     */
    private var folkFragment: FolkFragment?=null

    /**
     * 用于展示动态的Fragment
     */
    private var findFragment: FindFragment?=null

    /**
     * 用于展示设置的Fragment
     */
    private var personFragment: PersonFragment?=null

    /**
     * 消息界面布局
     */
    private lateinit var mainLayout: View

    /**
     * 联系人界面布局
     */
    private lateinit var folkLayout: View

    /**
     * 动态界面布局
     */
    private lateinit var findLayout: View

    /**
     * 设置界面布局
     */
    private lateinit var personLayout: View

    /**
     * 底部的tabBottom
     */

    private lateinit var bottomNavigation:BottomNavigationView


    /**
     * 用于对Fragment进行管理
     */
    private var fragmentManager: android.support.v4.app.FragmentManager? = null


    internal lateinit var ad: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        fragmentManager = supportFragmentManager
        setTabSelection(0,R.id.main_layout)
    }

    private fun initViews() {
        mainLayout = findViewById(R.id.main_layout)
        folkLayout = findViewById(R.id.folk_layout)
        findLayout = findViewById(R.id.find_layout)
        personLayout = findViewById(R.id.person_layout)
        bottomNavigation=findViewById(R.id.below_button)
        mainLayout.setOnClickListener(this)
        folkLayout.setOnClickListener(this)
        findLayout.setOnClickListener(this)
        personLayout.setOnClickListener(this)
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_layout ->
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0,v.id)
            R.id.folk_layout ->
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1,v.id)
            R.id.find_layout ->
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2,v.id)
            R.id.person_layout ->
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(3,v.id)
            else -> {
            }
        }
    }

    private fun setTabSelection(index: Int,clickID:Int) {

        val transaction = fragmentManager!!.beginTransaction()

        hideFragments(transaction)

        bottomNavigation.selectedItemId=clickID //底部按钮状态切换

        when (index) {
            0 -> {
                if (null == mainFragment) {
                    mainFragment = MainFragment()
                    transaction.add(R.id.content, mainFragment)
                } else {
                    transaction.show(mainFragment)
                }
            }
            1 -> {
                if (null == folkFragment) {
                    folkFragment = FolkFragment()
                    transaction.add(R.id.content, folkFragment)
                } else {
                    transaction.show(folkFragment)
                }
            }
            2 -> {
                if (null == findFragment) {
                    findFragment = FindFragment()
                    transaction.add(R.id.content, findFragment)
                } else {
                    transaction.show(findFragment)
                }
            }
            3 -> {
                if (null == personFragment) {
                    personFragment = PersonFragment()
                    transaction.add(R.id.content, personFragment)
                } else {
                    transaction.show(personFragment)
                }
            }
            else -> {
                if (null == personFragment) {
                    personFragment = PersonFragment()
                    transaction.add(R.id.content, personFragment)
                } else {
                    transaction.show(personFragment)
                }
            }
        }
        transaction.commit()
    }


    private fun hideFragments(transaction: FragmentTransaction) {
        if (null != mainFragment) {
            transaction.hide(mainFragment)
        }
        if (null != folkFragment) {
            transaction.hide(folkFragment)
        }
        if (null != findFragment) {
            transaction.hide(findFragment)
        }
        if (null != personFragment) {
            transaction.hide(personFragment)
        }
    }


    //重写onKeyDown方法，监听返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            val builder = AlertDialog.Builder(this@MainActivity).setTitle("退出?").setPositiveButton("退出") { _, _ ->
                ad.dismiss()
                finish()
            }.setNegativeButton("取消") { _, _ -> ad.dismiss() }
            ad = builder.create()
            ad.show()
            return super.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        MySqliteHandler.Close()
    }
}
