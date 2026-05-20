package com.duckylife.heritage.modern.feature.smoke

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.duckylife.heritage.modern.MainActivity
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.settings.AppLanguageMode
import com.duckylife.heritage.modern.core.settings.AppThemeMode
import com.duckylife.heritage.modern.core.settings.ThemeSettingsRepository
import com.duckylife.heritage.modern.core.testing.fake.TestFakeRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.After
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
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var themeSettingsRepository: ThemeSettingsRepository

    private lateinit var targetContext: Context
    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setup() {
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        hiltRule.inject()
        resetAppSettings()
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeRule.waitForIdle()
    }

    @After
    fun tearDown() {
        scenario?.close()
        scenario = null
        clearApplicationLocales()
    }

    // region 底部导航

    @Test
    fun bottomNavShowsThreeTabsAndCanSwitch() {
        composeRule.onNodeWithText(string(R.string.articles_latest_title))
            .assertIsDisplayed()

        clickContentDescription(R.string.nav_directory)
        composeRule.onNodeWithText(string(R.string.directory_title))
            .assertIsDisplayed()

        clickContentDescription(R.string.nav_inheritors)
        composeRule.onNodeWithText(string(R.string.inheritors_title))
            .assertIsDisplayed()
    }

    // endregion

    // region 文章详情

    @Test
    fun articleListClickEntersDetailAndBackReturnsToList() {
        clickTestArticle()

        composeRule.onNodeWithText(string(R.string.article_detail_title))
            .assertIsDisplayed()

        clickContentDescription(R.string.action_back)

        composeRule.onNodeWithText(string(R.string.articles_latest_title))
            .assertIsDisplayed()
    }

    // endregion

    // region 名录筛选

    @Test
    fun directoryFilterSheetOpensAndCloses() {
        clickContentDescription(R.string.nav_directory)

        clickContentDescription(R.string.filter_button)

        composeRule.onNodeWithText(string(R.string.filter_title))
            .assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.filter_clear))
            .performClick()

        composeRule.onNodeWithText(string(R.string.directory_title))
            .assertIsDisplayed()
    }

    // endregion

    // region 收藏链路

    @Test
    fun favoriteFlowFromDetailToMyPage() {
        clickTestArticle()

        clickContentDescription(R.string.action_favorite)

        clickContentDescription(R.string.action_back)

        clickContentDescription(R.string.nav_settings)

        composeRule.onNodeWithText(string(R.string.settings_title))
            .assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.my_page_entry))
            .performClick()

        waitUntilTextExists(TestFakeRepository.TestArticleDetailTitle)
        composeRule.onNodeWithText(TestFakeRepository.TestArticleDetailTitle)
            .assertIsDisplayed()
    }

    // endregion

    // region 设置入口

    @Test
    fun settingsPageShowsThemeAndLanguageGroups() {
        clickContentDescription(R.string.nav_settings)

        composeRule.onNodeWithText(string(R.string.settings_title))
            .assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.settings_theme_mode_title))
            .assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.settings_language_mode_title))
            .assertIsDisplayed()
    }

    // endregion

    // region 名录详情收藏

    @Test
    fun directoryDetailFavoriteShowsInMyPageAndJumpsBack() {
        // 进入名录列表并点击进入详情
        clickContentDescription(R.string.nav_directory)
        clickItemByTitle(TestFakeRepository.TestDirectoryTitle)

        // 确认已进入名录详情
        composeRule.onNodeWithText(string(R.string.directory_detail_title))
            .assertIsDisplayed()

        // 点击收藏
        clickContentDescription(R.string.action_favorite)

        // 返回名录列表
        clickContentDescription(R.string.action_back)

        // 设置入口只在文章首页 header，先切回文章 tab 再打开设置
        clickContentDescription(R.string.nav_articles)
        clickContentDescription(R.string.nav_settings)
        composeRule.onNodeWithText(string(R.string.my_page_entry)).performClick()

        // 验证名录详情标题出现在收藏页签
        waitUntilTextExists(TestFakeRepository.TestDirectoryDetailTitle)
        composeRule.onNodeWithText(TestFakeRepository.TestDirectoryDetailTitle)
            .assertIsDisplayed()
    }

    // endregion

    // region 传承人关联名录

    @Test
    fun inheritorDetailRelatedProjectNavigatesToDirectoryDetail() {
        // 切换到传承人列表并点击进入详情
        clickContentDescription(R.string.nav_inheritors)
        clickItemByTitle(TestFakeRepository.TestInheritorName)

        // 确认进入传承人详情
        composeRule.onNodeWithText(string(R.string.inheritor_detail_title))
            .assertIsDisplayed()

        // 滚动并点击关联项目 "中医诊疗法"
        scrollToAndClick("中医诊疗法")

        // 确认跳转到名录详情
        composeRule.onNodeWithText(string(R.string.directory_detail_title))
            .assertIsDisplayed()
    }

    // endregion

    // region 文章关联跳转

    @Test
    fun articleDetailRelatedArticleNavigation() {
        // 进入文章详情
        clickTestArticle()

        composeRule.onNodeWithText(string(R.string.article_detail_title))
            .assertIsDisplayed()

        // 滚动到相关文章区并点击 "关联文章"
        scrollToAndClick("关联文章")

        // 确认进入另一个文章详情
        composeRule.onNodeWithText(string(R.string.article_detail_title))
            .assertIsDisplayed()

        // 返回应回到原始详情
        clickContentDescription(R.string.action_back)
        composeRule.onNodeWithText(string(R.string.article_detail_title))
            .assertIsDisplayed()
    }

    // endregion

    // region 主题语言切换不闪退

    @Test
    fun settingsThemeAndLanguageSwitchDoesNotCrash() {
        // 进入设置
        clickContentDescription(R.string.nav_settings)
        composeRule.onNodeWithText(string(R.string.settings_title))
            .assertIsDisplayed()

        // 切换到深色主题
        composeRule.onNodeWithText(string(R.string.theme_mode_dark)).performClick()
        composeRule.waitForIdle()

        // 切换回浅色
        composeRule.onNodeWithText(string(R.string.theme_mode_light)).performClick()
        composeRule.waitForIdle()

        // 切换到英文 — activity 重建，等待设置页英文文案出现
        composeRule.onNodeWithText(string(R.string.language_mode_english)).performClick()
        composeRule.waitForIdle()
        // 验证英文设置页出现（点击英文后 settings 仍在 render）
        composeRule.onNodeWithText("Light").assertIsDisplayed()

        // 切换回简体中文
        composeRule.onNodeWithText(string(R.string.language_mode_simplified_chinese)).performClick()
        composeRule.waitForIdle()
        // 验证中文设置页恢复
        composeRule.onNodeWithText(string(R.string.theme_mode_light)).assertIsDisplayed()
    }

    // endregion

    // region helpers

    private fun resetAppSettings() {
        clearApplicationLocales()
        runBlocking {
            themeSettingsRepository.setThemeMode(AppThemeMode.System)
            themeSettingsRepository.setLanguageMode(AppLanguageMode.System)
        }
    }

    private fun clearApplicationLocales() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            targetContext
                .getSystemService(LocaleManager::class.java)
                .applicationLocales = LocaleList.forLanguageTags("")
        }
    }

    private fun clickTestArticle() {
        composeRule.onNodeWithText(string(R.string.articles_latest_title))
            .assertIsDisplayed()
        composeRule.onNode(
            hasScrollAction() and hasAnyDescendant(hasText(string(R.string.articles_latest_title))),
            useUnmergedTree = true,
        ).performScrollToNode(hasText(TestFakeRepository.TestArticleTitle))
        waitUntilTextExists(TestFakeRepository.TestArticleTitle)
        composeRule.onAllNodesWithText(TestFakeRepository.TestArticleTitle).onFirst().performClick()
    }

    private fun clickItemByTitle(title: String) {
        waitUntilTextExists(title)
        composeRule.onAllNodesWithText(title).onFirst().performClick()
    }

    private fun scrollToAndClick(text: String) {
        composeRule.onNode(
            hasScrollAction(),
            useUnmergedTree = true,
        ).performScrollToNode(hasText(text))
        waitUntilTextExists(text)
        composeRule.onAllNodesWithText(text).onFirst().performClick()
    }

    private fun clickContentDescription(@StringRes resId: Int) {
        composeRule.onNodeWithContentDescription(
            label = string(resId),
            useUnmergedTree = true,
        ).performClick()
    }

    private fun waitUntilTextExists(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun string(@StringRes resId: Int): String = targetContext.getString(resId)

    // endregion
}
