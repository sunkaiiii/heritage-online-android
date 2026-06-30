package com.duckylife.heritage.modern.feature.discovery

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiscoveryScreenSpacetimeEntryTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun spacetimeEntryIsVisibleAndClickable() {
        var clicked = false
        composeRule.setContent {
            HeritageTheme {
                DiscoveryScreen(
                    uiState = DiscoveryUiState(),
                    onRefresh = {},
                    onSearchSubmit = {},
                    onTopicClick = {},
                    onLearningPathClick = {},
                    onCollectionClick = {},
                    onRegionAtlasClick = {},
                    onTimelineClick = {},
                    onKnowledgeGraphClick = {},
                    onLearningRoutesClick = {},
                    onSpacetimeClick = { clicked = true },
                    onRankingsClick = {},
                    onSerendipityClick = {},
                    onTrendingItemClick = {},
                    onWeeklyItemClick = {},
                    onTodayItemClick = {},
                    onDeepDiveClick = {},
                    onTaxonomyClick = {},
                    onStoriesClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.discovery_spacetime))
            .assertIsDisplayed()
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun rankingsEntryIsVisibleAndClickable() {
        var clicked = false
        composeRule.setContent {
            HeritageTheme {
                DiscoveryScreen(
                    uiState = DiscoveryUiState(),
                    onRefresh = {},
                    onSearchSubmit = {},
                    onTopicClick = {},
                    onLearningPathClick = {},
                    onCollectionClick = {},
                    onRegionAtlasClick = {},
                    onTimelineClick = {},
                    onKnowledgeGraphClick = {},
                    onLearningRoutesClick = {},
                    onSpacetimeClick = {},
                    onRankingsClick = { clicked = true },
                    onSerendipityClick = {},
                    onTrendingItemClick = {},
                    onWeeklyItemClick = {},
                    onTodayItemClick = {},
                    onDeepDiveClick = {},
                    onTaxonomyClick = {},
                    onStoriesClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.discovery_rankings))
            .assertIsDisplayed()
            .performClick()

        assertTrue(clicked)
    }
}
