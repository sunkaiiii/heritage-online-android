package com.example.sunkai.heritage.Activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView

import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Fragment.MainFragment
import com.example.sunkai.heritage.Fragment.PersonFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.Fragment.FindFragment
import com.example.sunkai.heritage.Fragment.FolkFragment

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
     * 在Tab布局上显示消息图标的控件
     */
    private lateinit var mainImage: ImageView

    /**
     * 在Tab布局上显示联系人图标的控件
     */
    private lateinit var folkImage: ImageView

    /**
     * 在Tab布局上显示动态图标的控件
     */
    private lateinit var findImage: ImageView

    /**
     * 在Tab布局上显示设置图标的控件
     */
    private lateinit var personImage: ImageView

    /**
     * 在Tab布局上显示消息标题的控件
     */
    private lateinit var mainText: TextView

    /**
     * 在Tab布局上显示联系人标题的控件
     */
    private lateinit var folkText: TextView

    /**
     * 在Tab布局上显示动态标题的控件
     */
    private lateinit var findText: TextView

    /**
     * 在Tab布局上显示设置标题的控件
     */
    private lateinit var personText: TextView

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
        setTabSelection(0)


    }

    private fun initViews() {
        mainLayout = findViewById(R.id.main_layout)
        folkLayout = findViewById(R.id.folk_layout)
        findLayout = findViewById(R.id.find_layout)
        personLayout = findViewById(R.id.person_layout)
        mainImage = findViewById(R.id.main_image)
        folkImage = findViewById(R.id.folk_image)
        findImage = findViewById(R.id.find_image)
        personImage = findViewById(R.id.person_image)
        mainText = findViewById(R.id.main_text)
        folkText = findViewById(R.id.folk_text)
        findText = findViewById(R.id.find_text)
        personText = findViewById(R.id.person_text)
        mainLayout.setOnClickListener(this)
        folkLayout.setOnClickListener(this)
        findLayout.setOnClickListener(this)
        personLayout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_layout ->
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0)
            R.id.folk_layout ->
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1)
            R.id.find_layout ->
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2)
            R.id.person_layout ->
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(3)
            else -> {
            }
        }
    }

    private fun setTabSelection(index: Int) {
        clearSelection()

        val transaction = fragmentManager!!.beginTransaction()

        hideFragments(transaction)

        when (index) {
            0 -> {
                mainImage.setImageResource(R.drawable.ic_home_brown_500_24dp)
                mainText.setTextColor(Color.BLACK)
                if (null == mainFragment) {
                    mainFragment = MainFragment()
                    transaction.add(R.id.content, mainFragment)
                } else {
                    transaction.show(mainFragment)
                }
            }
            1 -> {
                folkImage.setImageResource(R.drawable.ic_people_brown_500_24dp)
                folkText.setTextColor(Color.BLACK)
                if (null == folkFragment) {
                    folkFragment = FolkFragment()
                    transaction.add(R.id.content, folkFragment)
                } else {
                    transaction.show(folkFragment)
                }
            }
            2 -> {
                findImage.setImageResource(R.drawable.ic_search_brown_500_24dp)
                findText.setTextColor(Color.BLACK)
                if (null == findFragment) {
                    findFragment = FindFragment()
                    transaction.add(R.id.content, findFragment)
                } else {
                    transaction.show(findFragment)
                }
            }
            3 -> {
                personImage.setImageResource(R.drawable.ic_assignment_ind_brown_500_24dp)
                personText.setTextColor(Color.BLACK)
                if (null == personFragment) {
                    personFragment = PersonFragment()
                    transaction.add(R.id.content, personFragment)
                } else {
                    transaction.show(personFragment)
                }
            }
            else -> {
                personImage.setImageResource(R.drawable.ic_assignment_ind_brown_500_24dp)
                personText.setTextColor(Color.BLACK)
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

    private fun clearSelection() {
        mainImage.setImageResource(R.drawable.ic_home_grey_500_24dp)
        mainText.setTextColor(Color.parseColor("#82858b"))
        folkImage.setImageResource(R.drawable.ic_people_grey_500_24dp)
        folkText.setTextColor(Color.parseColor("#82858b"))
        findImage.setImageResource(R.drawable.ic_search_grey_500_24dp)
        findText.setTextColor(Color.parseColor("#82858b"))
        personImage.setImageResource(R.drawable.ic_assignment_ind_grey_500_24dp)
        personText.setTextColor(Color.parseColor("#82858b"))
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
