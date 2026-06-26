package com.duckylife.heritage.modern.feature.discovery

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiscoveryScreenGraphEntryTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun knowledgeGraphEntryIsVisibleAndClickable() {
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
                    onKnowledgeGraphClick = { clicked = true },
                    onLearningRoutesClick = {},
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

        composeRule.onNodeWithText(context.getString(R.string.knowledge_graph_hub_title))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.knowledge_graph_hub_subtitle))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.knowledge_graph_hub_title))
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun learningRoutesEntryIsVisibleAndClickable() {
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
                    onLearningRoutesClick = { clicked = true },
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

        composeRule.onNodeWithText(context.getString(R.string.learning_routes_title))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.discovery_learning_routes_subtitle))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.learning_routes_title))
            .performClick()

        assertTrue(clicked)
    }
}
