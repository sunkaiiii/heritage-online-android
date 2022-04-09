package com.example.sunkai.heritage.activity

import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.databinding.ActivityMainBinding
import com.example.sunkai.heritage.tools.*
import dagger.hilt.android.AndroidEntryPoint

/**
 * 此类用于处理登陆
 */

@AndroidEntryPoint
class MainActivity : BaseGlideActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setIgnoreToolbar(true)
    }

    override fun changeSpecificViewTheme() {
        val drawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, arrayOf(
                getTertiaryDark(), getTertiary(), getTertiaryLight()
            ).toIntArray()
        )
        drawable.orientation = GradientDrawable.Orientation.BL_TR
        binding.activityMainNavigationView.getHeaderView(0)
            .findViewById<View>(R.id.navigationViewTopHeaderLayout).background = drawable
    }

    private fun initViews() {
        binding.activityMainNavigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_about_us -> navigateToAboutUsPage()
//                R.id.navigation_collection -> navigateToMyCollectionPage()
            }
            binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        //设置底部导航
        binding.bottomNavigationButton.setupWithNavController(navController)
    }


    fun showNavigationDrawerLayout() {
        binding.activityMainDrawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (binding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    private fun navigateToAboutUsPage() {
        navController.navigate(R.id.main_view_to_about_us_fragment)
    }


    private fun navigateToMyCollectionPage() {
        navController.navigate(R.id.main_view_to_my_collection_fragment)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        reloadThemeColor()
        recreate()
    }


}
