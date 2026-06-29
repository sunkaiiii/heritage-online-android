package com.duckylife.heritage.modern.feature.research

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageItemUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportItemUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.state.AsyncState
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResearchLibraryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val packages = listOf(
        ResearchPackageItemUiModel(
            packageId = "pkg-1",
            title = "非遗剪纸资料包",
            subtitle = "测试资料包",
            createdAt = "2026-06-01",
            status = ResearchTaskStatus.Succeeded,
            isClickable = true,
            artifactCount = 3,
            includesContent = true,
            includesEvidence = true,
            includesAiResults = false,
            includesAiInferred = false,
        ),
        ResearchPackageItemUiModel(
            packageId = "pkg-2",
            title = "准备中的资料包",
            subtitle = null,
            createdAt = null,
            status = ResearchTaskStatus.Running,
            isClickable = false,
            artifactCount = 0,
            includesContent = false,
            includesEvidence = false,
            includesAiResults = false,
            includesAiInferred = false,
        ),
    )

    private val reports = listOf(
        ResearchReportItemUiModel(
            reportId = "rpt-1",
            packageId = "pkg-1",
            title = "剪纸研究报告",
            status = ResearchTaskStatus.Succeeded,
            createdAt = "2026-06-02",
            modelName = "gpt-4o",
        ),
    )

    @Test
    fun displaysPackagesAndSwitchesToReports() {
        composeTestRule.setContent {
            HeritageTheme {
                ResearchLibraryScreen(
                    uiState = ResearchLibraryUiState(
                        selectedTab = ResearchLibraryTab.Packages,
                        packages = AsyncState(data = packages),
                        reports = AsyncState(data = reports),
                    ),
                    onBack = {},
                    onRefresh = {},
                    onTabSelected = {},
                    onPackageClick = {},
                    onReportClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("非遗剪纸资料包").assertIsDisplayed()

        composeTestRule.onNodeWithText(context.getString(R.string.research_reports_tab))
            .performClick()

        composeTestRule.onNodeWithText("剪纸研究报告").assertIsDisplayed()
    }

    @Test
    fun clickSucceededPackage_emitsPackageId() {
        var clickedId: String? = null
        composeTestRule.setContent {
            HeritageTheme {
                ResearchLibraryScreen(
                    uiState = ResearchLibraryUiState(
                        selectedTab = ResearchLibraryTab.Packages,
                        packages = AsyncState(data = packages),
                    ),
                    onBack = {},
                    onRefresh = {},
                    onTabSelected = {},
                    onPackageClick = { clickedId = it },
                    onReportClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("非遗剪纸资料包")
            .assertHasClickAction()
            .performClick()

        assertEquals("pkg-1", clickedId)
    }

    @Test
    fun errorState_showsRetryButton() {
        var retryCount = 0
        composeTestRule.setContent {
            HeritageTheme {
                ResearchLibraryScreen(
                    uiState = ResearchLibraryUiState(
                        selectedTab = ResearchLibraryTab.Packages,
                        packages = AsyncState(errorKind = ErrorKind.NetworkUnavailable),
                    ),
                    onBack = {},
                    onRefresh = { retryCount++ },
                    onTabSelected = {},
                    onPackageClick = {},
                    onReportClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.action_retry))
            .assertIsDisplayed()
            .performClick()

        assertEquals(1, retryCount)
    }

    @Test
    fun nonSucceededPackage_doesNotEmitClickAndNoRetryButton() {
        var clickedId: String? = null
        composeTestRule.setContent {
            HeritageTheme {
                ResearchLibraryScreen(
                    uiState = ResearchLibraryUiState(
                        selectedTab = ResearchLibraryTab.Packages,
                        packages = AsyncState(data = packages),
                    ),
                    onBack = {},
                    onRefresh = {},
                    onTabSelected = {},
                    onPackageClick = { clickedId = it },
                    onReportClick = {},
                )
            }
        }

        // Running package is displayed but not clickable.
        composeTestRule.onNodeWithText("准备中的资料包")
            .assertIsDisplayed()
            .assertHasNoClickAction()
        assertEquals(null, clickedId)

        // No per-card retry/cancel button appears in non-error list state.
        composeTestRule.onNodeWithText(context.getString(R.string.action_retry))
            .assertDoesNotExist()
    }

    @Test
    fun emptyPackagesState_showsEmptyMessage() {
        composeTestRule.setContent {
            HeritageTheme {
                ResearchLibraryScreen(
                    uiState = ResearchLibraryUiState(
                        selectedTab = ResearchLibraryTab.Packages,
                        packages = AsyncState(data = emptyList()),
                    ),
                    onBack = {},
                    onRefresh = {},
                    onTabSelected = {},
                    onPackageClick = {},
                    onReportClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.research_empty_title))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.research_empty_message))
            .assertIsDisplayed()
    }
}
