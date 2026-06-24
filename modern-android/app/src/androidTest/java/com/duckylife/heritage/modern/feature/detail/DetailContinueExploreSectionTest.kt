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
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentRefDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.feature.detail.intelligence.ContentIntelligenceUiState
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailContinueExploreSectionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(resId: Int, vararg args: Any): String = context.getString(resId, *args)

    @Test
    fun allActionsVisible_whenDataAvailable() {
        val uiState = ContentIntelligenceUiState(
            graphSection = IntelligenceSection(
                SectionStatus.Ready,
                GraphNeighborsDto(
                    nodes = listOf(
                        GraphNodeDto(nodeKey = "n1", type = GraphNodeType.Article),
                        GraphNodeDto(nodeKey = "n2", type = GraphNodeType.Article),
                    ),
                ),
            ),
            recommendationSection = IntelligenceSection(
                SectionStatus.Ready,
                listOf(ContentRefDto(type = GraphNodeType.Article, id = "r1")),
            ),
            learningRoutesAvailable = true,
        )

        composeRule.setContent {
            HeritageTheme {
                DetailContinueExploreSection(
                    uiState = uiState,
                    onGraphClick = {},
                    onSimilarClick = {},
                    onLearningRoutesClick = {},
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.explore_actions_title))
            .assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.explore_action_graph))
            .assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.explore_action_similar))
            .assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.explore_action_learning_routes))
            .assertIsDisplayed()
    }

    @Test
    fun hidesUnavailableActions() {
        val uiState = ContentIntelligenceUiState(
            graphSection = IntelligenceSection(SectionStatus.Unavailable, GraphNeighborsDto()),
            recommendationSection = IntelligenceSection(SectionStatus.Ready, emptyList()),
            learningRoutesAvailable = false,
        )

        composeRule.setContent {
            HeritageTheme {
                DetailContinueExploreSection(
                    uiState = uiState,
                    onGraphClick = {},
                    onSimilarClick = {},
                    onLearningRoutesClick = {},
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.explore_actions_title))
            .assertDoesNotExist()
        composeRule.onNodeWithText(string(R.string.explore_action_graph))
            .assertDoesNotExist()
        composeRule.onNodeWithText(string(R.string.explore_action_similar))
            .assertDoesNotExist()
        composeRule.onNodeWithText(string(R.string.explore_action_learning_routes))
            .assertDoesNotExist()
    }

    @Test
    fun clickActions_invokeCallbacks() {
        val calls = mutableListOf<String>()
        val uiState = ContentIntelligenceUiState(
            graphSection = IntelligenceSection(
                SectionStatus.Ready,
                GraphNeighborsDto(nodes = listOf(GraphNodeDto(nodeKey = "n1", type = GraphNodeType.Article))),
            ),
            recommendationSection = IntelligenceSection(
                SectionStatus.Ready,
                listOf(ContentRefDto(type = GraphNodeType.Article, id = "r1")),
            ),
            learningRoutesAvailable = true,
        )

        composeRule.setContent {
            HeritageTheme {
                DetailContinueExploreSection(
                    uiState = uiState,
                    onGraphClick = { calls += "graph" },
                    onSimilarClick = { calls += "similar" },
                    onLearningRoutesClick = { calls += "routes" },
                )
            }
        }

        composeRule.onNodeWithText(string(R.string.explore_action_graph))
            .performClick()
        composeRule.onNodeWithText(string(R.string.explore_action_similar))
            .performClick()
        composeRule.onNodeWithText(string(R.string.explore_action_learning_routes))
            .performClick()

        assertEquals(listOf("graph", "similar", "routes"), calls)
    }
}
