package com.example.sunkai.heritage.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.adapter.SettingListSelectThemeColorAdapter
import com.example.sunkai.heritage.databinding.ActivityMainBinding
import com.example.sunkai.heritage.tools.getDarkThemeColor
import com.example.sunkai.heritage.tools.getLightThemeColor
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.tools.reloadThemeColor
import com.example.sunkai.heritage.value.THEME_COLOR_ARRAYS
import dagger.hilt.android.AndroidEntryPoint

/**
 * 此类用于处理登陆
 */

@AndroidEntryPoint
class MainActivity : BaseGlideActivity() {

    private lateinit var navController: NavController
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setIgnoreToolbar(true)
    }

    override fun changeSpecificViewTheme() {
        val drawable=GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,arrayOf(getDarkThemeColor(), getThemeColor(), getLightThemeColor()).toIntArray())
        drawable.orientation=GradientDrawable.Orientation.BL_TR
        binding.activityMainNavigationView.getHeaderView(0).findViewById<View>(R.id.navigationViewTopHeaderLayout).background=drawable
    }

    private fun initViews() {
        val themeColor = getThemeColor()
        val midGrey = ContextCompat.getColor(this@MainActivity, R.color.midGrey)
        val colors = arrayOf(themeColor, midGrey).toIntArray()
        val states = arrayOf(arrayOf(android.R.attr.state_checked).toIntArray(), arrayOf(-android.R.attr.state_checked).toIntArray())
        val colorStateList = ColorStateList(states, colors)
        binding.bottomNavigationButton.itemTextColor = colorStateList
        binding.bottomNavigationButton.itemIconTintList = colorStateList
        binding.activityMainNavigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_about_us -> navigateToAboutUsPage()
            }
            binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }
        val themeColorRecyclerView = binding.activityMainNavigationView.getHeaderView(0).findViewById<RecyclerView>(R.id.navigationViewHeaderRecyclerView)
        themeColorRecyclerView.post {
            val themeColorAdapter = SettingListSelectThemeColorAdapter(themeColorRecyclerView.context, THEME_COLOR_ARRAYS.toList(), Glide.with(themeColorRecyclerView))
            val spanCount=(themeColorRecyclerView.width/SettingListSelectThemeColorAdapter.IMAGE_VIEW_SIZE)-1
            themeColorRecyclerView.layoutManager= GridLayoutManager(this,spanCount)
            themeColorRecyclerView.adapter=themeColorAdapter
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        //设置底部导航
        binding.bottomNavigationButton.setupWithNavController(navController)
    }

    fun showNavigationDrawerLayout(){
        binding.activityMainDrawerLayout.openDrawer(GravityCompat.START)
    }
    override fun onBackPressed() {
        if(binding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    private fun navigateToAboutUsPage() {
        val intent = Intent(this, AboutUSActivity::class.java)
        startActivity(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        reloadThemeColor()
        recreate()
    }


}
