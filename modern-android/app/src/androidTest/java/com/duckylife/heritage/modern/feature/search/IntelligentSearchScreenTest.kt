package com.duckylife.heritage.modern.feature.search

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchScoreBreakdownDto
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntelligentSearchScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun modeSwitch_triggersReferenceAndIntelligentModes() {
        val mode = mutableStateOf(SearchMode.Reference)
        composeRule.setContent {
            HeritageTheme {
                SearchScreen(
                    uiState = SearchUiState(
                        query = "非遗",
                        mode = mode.value,
                    ),
                    onBack = {},
                    onQueryChange = {},
                    onSearch = {},
                    onModeSelected = { mode.value = it },
                    onSuggestionSelected = {},
                    onResultClick = {},
                    onIntelligentResultClick = {},
                    onLoadMore = {},
                    onToggleType = {},
                    onUpdateRegionFilter = {},
                    onUpdateCategoryFilter = {},
                    onUpdateYearFilter = {},
                    onUpdateKindFilter = {},
                    onUpdateHasImageFilter = {},
                    onUpdateIntelligentIncludeAi = {},
                    onUpdateIntelligentIncludeGraph = {},
                    onUpdateIntelligentIncludeHighlights = {},
                    onShowWhyMatch = {},
                    onDismissWhyMatch = {},
                    onClearFilters = {},
                    onClearError = {},
                )
            }
        }

        val intelligentLabel = context.getString(R.string.search_mode_intelligent)
        val referenceLabel = context.getString(R.string.search_mode_reference)

        composeRule.onNodeWithText(intelligentLabel).assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        assertEquals(SearchMode.Intelligent, mode.value)

        composeRule.onNodeWithText(referenceLabel).assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        assertEquals(SearchMode.Reference, mode.value)
    }

    @Test
    fun whyMatchClick_showsExplanationSheet() {
        val item = IntelligentSearchItemDto(
            type = GraphNodeType.Article,
            id = "article-1",
            title = "测试文章",
            scoreBreakdown = IntelligentSearchScoreBreakdownDto(
                titleExact = 1.0,
            ),
        )
        val whyMatch = mutableStateOf<IntelligentSearchItemDto?>(null)
        composeRule.setContent {
            HeritageTheme {
                SearchScreen(
                    uiState = SearchUiState(
                        query = "测试",
                        mode = SearchMode.Intelligent,
                        intelligentResults = listOf(item),
                        whyMatchItem = whyMatch.value,
                    ),
                    onBack = {},
                    onQueryChange = {},
                    onSearch = {},
                    onModeSelected = {},
                    onSuggestionSelected = {},
                    onResultClick = {},
                    onIntelligentResultClick = {},
                    onLoadMore = {},
                    onToggleType = {},
                    onUpdateRegionFilter = {},
                    onUpdateCategoryFilter = {},
                    onUpdateYearFilter = {},
                    onUpdateKindFilter = {},
                    onUpdateHasImageFilter = {},
                    onUpdateIntelligentIncludeAi = {},
                    onUpdateIntelligentIncludeGraph = {},
                    onUpdateIntelligentIncludeHighlights = {},
                    onShowWhyMatch = { whyMatch.value = it },
                    onDismissWhyMatch = { whyMatch.value = null },
                    onClearFilters = {},
                    onClearError = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.intelligent_why_match))
            .assertIsDisplayed()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(context.getString(R.string.intelligent_why_match_title))
            .assertIsDisplayed()
    }
}
