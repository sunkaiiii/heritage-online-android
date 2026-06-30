package com.duckylife.heritage.modern.feature.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.data.IntelligenceSection
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.feature.detail.intelligence.ContentIntelligenceUiState
import com.duckylife.heritage.modern.feature.detail.intelligence.DetailIntelligenceSection
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailIntelligenceSectionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(resId: Int, vararg args: Any): String = context.getString(resId, *args)

    @Test
    fun shortSummaryAndHighlights_expandAndCollapseWork() {
        val card = AiCardDto(
            hasAi = true,
            summary = "这是一段非常完整的智能解读摘要，用来验证展开之后可以显示全部文本。",
            shortSummary = "这是一段简短摘要。",
            highlights = listOf("亮点一", "亮点二", "亮点三", "亮点四"),
            keywords = listOf("剪纸", "民俗"),
        )
        val uiState = ContentIntelligenceUiState(
            aiSection = IntelligenceSection(SectionStatus.Ready, card),
        )

        composeRule.setContent {
            HeritageTheme {
                DetailIntelligenceSection(
                    uiState = uiState,
                    onKeywordClick = {},
                )
            }
        }

        composeRule.onNodeWithText("这是一段简短摘要。").assertIsDisplayed()
        composeRule.onNodeWithText("亮点三").assertIsDisplayed()
        composeRule.onNodeWithText("亮点四").assertDoesNotExist()

        composeRule.onNodeWithText(string(R.string.intelligence_expand))
            .performClick()
        composeRule.onNodeWithText("这是一段非常完整的智能解读摘要，用来验证展开之后可以显示全部文本。")
            .assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.intelligence_collapse))
            .performClick()
        composeRule.onNodeWithText("这是一段简短摘要。").assertIsDisplayed()
    }

    @Test
    fun highlights_showAllExpandsRemaining() {
        val card = AiCardDto(
            hasAi = true,
            summary = "summary",
            highlights = listOf("a", "b", "c", "d"),
        )
        val uiState = ContentIntelligenceUiState(
            aiSection = IntelligenceSection(SectionStatus.Ready, card),
        )

        composeRule.setContent {
            HeritageTheme {
                DetailIntelligenceSection(
                    uiState = uiState,
                    onKeywordClick = {},
                )
            }
        }

        composeRule.onNodeWithText("d").assertDoesNotExist()
        composeRule.onNodeWithText(string(R.string.intelligence_show_all_n, 4))
            .performClick()
        composeRule.onNodeWithText("d").assertIsDisplayed()
    }

    @Test
    fun keywordClick_invokesCallback() {
        val clicked = mutableListOf<String>()
        val card = AiCardDto(
            hasAi = true,
            summary = "summary",
            keywords = listOf("民俗"),
        )
        val uiState = ContentIntelligenceUiState(
            aiSection = IntelligenceSection(SectionStatus.Ready, card),
        )

        composeRule.setContent {
            HeritageTheme {
                DetailIntelligenceSection(
                    uiState = uiState,
                    onKeywordClick = { clicked += it },
                )
            }
        }

        composeRule.onNodeWithText("民俗").performClick()
        assertEquals(listOf("民俗"), clicked)
    }

    @Test
    fun missingAi_showsLowKeyMessage() {
        val uiState = ContentIntelligenceUiState(
            aiSection = IntelligenceSection(SectionStatus.Missing, AiCardDto()),
        )

        composeRule.setContent {
            HeritageTheme {
                DetailIntelligenceSection(
                    uiState = uiState,
                    onKeywordClick = {},
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.intelligence_not_available))
            .assertIsDisplayed()
    }

    @Test
    fun unavailableAi_showsRetryAndInvokesCallback() {
        var retried = false
        val uiState = ContentIntelligenceUiState(
            aiSection = IntelligenceSection(SectionStatus.Unavailable, AiCardDto()),
        )

        composeRule.setContent {
            HeritageTheme {
                DetailIntelligenceSection(
                    uiState = uiState,
                    onKeywordClick = {},
                    onRetry = { retried = true },
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.intelligence_unavailable))
            .assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.intelligence_retry))
            .performClick()
        assertTrue(retried)
    }
}
