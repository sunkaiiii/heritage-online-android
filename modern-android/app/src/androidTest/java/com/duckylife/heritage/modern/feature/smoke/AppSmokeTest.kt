package com.duckylife.heritage.modern.feature.smoke

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckylife.heritage.modern.MainActivity
import com.duckylife.heritage.modern.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AppSmokeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // region 底部导航

    @Test
    fun bottomNavShowsThreeTabsAndCanSwitch() {
        // 文章 tab 默认选中，标题可见
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.articles_header_title))
            .assertIsDisplayed()

        // 切换到名录 tab
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.nav_directory))
            .performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.directory_title))
            .assertIsDisplayed()

        // 切换到传承人 tab
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.nav_inheritors))
            .performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.inheritors_title))
            .assertIsDisplayed()
    }

    // endregion

    // region 文章详情

    @Test
    fun articleListClickEntersDetailAndBackReturnsToList() {
        // 在文章列表点击第一条文章
        composeRule.onNodeWithText("测试新闻")
            .performClick()

        // 进入详情页 — 标题栏显示 "文章详情"
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.article_detail_title))
            .assertIsDisplayed()

        // 返回
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.action_back))
            .performClick()

        // 回到文章列表
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.articles_header_title))
            .assertIsDisplayed()
    }

    // endregion

    // region 名录筛选

    @Test
    fun directoryFilterSheetOpensAndCloses() {
        // 切换到名录 tab
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.nav_directory))
            .performClick()

        // 打开筛选面板
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.filter_button))
            .performClick()

        // 筛选面板标题可见
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.filter_title))
            .assertIsDisplayed()

        // 点击背景关闭（下滑或点外部）— 这里点清空筛选按钮让面板关闭
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.filter_clear))
            .performClick()

        // 面板已关闭，列表标题再次可见
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.directory_title))
            .assertIsDisplayed()
    }

    // endregion

    // region 收藏链路

    @Test
    fun favoriteFlowFromDetailToMyPage() {
        // 进入文章详情
        composeRule.onNodeWithText("测试新闻")
            .performClick()

        // 点击收藏按钮
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.action_favorite))
            .performClick()

        // 返回文章列表
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.action_back))
            .performClick()

        // 打开设置
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.nav_settings))
            .performClick()

        // 设置页标题可见
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.settings_title))
            .assertIsDisplayed()

        // 点击 "收藏与最近浏览"
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.my_page_entry))
            .performClick()

        // 收藏页签显示收藏的文章
        composeRule.onNodeWithText("测试新闻详情")
            .assertIsDisplayed()
    }

    // endregion

    // region 设置入口

    @Test
    fun settingsPageShowsThemeAndLanguageGroups() {
        // 打开设置
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.nav_settings))
            .performClick()

        // 设置标题可见
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.settings_title))
            .assertIsDisplayed()

        // 主题分组可见
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.settings_theme_mode_title))
            .assertIsDisplayed()

        // 语言分组可见
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.settings_language_mode_title))
            .assertIsDisplayed()
    }

    // endregion
}
