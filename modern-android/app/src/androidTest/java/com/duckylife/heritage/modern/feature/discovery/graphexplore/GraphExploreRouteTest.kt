package com.duckylife.heritage.modern.feature.discovery.graphexplore

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState
import com.duckylife.heritage.modern.feature.discovery.GraphTab
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GraphExploreRouteTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun centerNodeCardAndTabsAreDisplayed() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState(),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onCenterNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithTag("CenterNodeCard").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.graph_tab_neighbors))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.graph_tab_similar))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.graph_tab_explore))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.graph_tab_evidence))
            .assertIsDisplayed()
    }

    @Test
    fun moreMenuOpensAndShowsRelationExplanationSheet() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState(),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onCenterNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription(context.getString(R.string.graph_explore_menu_more))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.graph_explore_menu_refresh))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.graph_explore_menu_relation_explanation))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.graph_explore_relation_explanation_title))
            .assertIsDisplayed()
    }

    @Test
    fun selectingTabCallsCallback() {
        var selectedTab: GraphTab? = null
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState(),
                    onBack = {},
                    onTabSelected = { selectedTab = it },
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onCenterNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_tab_similar))
            .performClick()
        assertEquals(GraphTab.Similar, selectedTab)
    }

    @Test
    fun evidenceTabShowsAiInferredFilterChip() {
        var toggled = false
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(selectedTab = GraphTab.Evidence),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = { toggled = true },
                    onCenterNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_explore_include_ai_inferred))
            .assertIsDisplayed()
            .performClick()
        assertTrue(toggled)
    }

    @Test
    fun aiInferredHelperTextAppearsWhenSwitchIsOn() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(
                        selectedTab = GraphTab.Evidence,
                        includeAiInferred = true,
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onCenterNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_explore_ai_inferred_hint))
            .assertIsDisplayed()
    }

    @Test
    fun loadingStateShowsSkeleton() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = GraphExploreUiState(
                        neighbors = DiscoverySectionState(isLoading = true),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onCenterNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithTag("NeighborsSkeleton").assertIsDisplayed()
    }

    @Test
    fun errorStateShowsRetryButton() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = GraphExploreUiState(
                        neighbors = DiscoverySectionState(errorKind = ErrorKind.ServerError),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onCenterNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.action_retry)).assertIsDisplayed()
    }

    @Test
    fun errorWithCachedDataShowsInlineRetryAndKeepsContent() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(
                        neighbors = DiscoverySectionState(
                            data = GraphNeighborsResult(
                                centerNodeKey = "article-test",
                                nodes = listOf(
                                    GraphNodeUiModel(
                                        nodeKey = "article-test",
                                        type = GraphNodeType.Article,
                                        id = "article-test-id",
                                        title = "Cached Article",
                                    ),
                                ),
                                edges = emptyList(),
                            ),
                            errorKind = ErrorKind.ServerError,
                        ),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onCenterNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Cached Article").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.action_retry)).assertIsDisplayed()
    }

    @Test
    fun invalidRouteShowsErrorMessage() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = GraphExploreUiState(isInvalidRoute = true),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onCenterNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_explore_invalid_route_title))
            .assertIsDisplayed()
    }

    private fun loadedUiState(): GraphExploreUiState {
        val centerNode = GraphNodeUiModel(
            nodeKey = "article-test",
            type = GraphNodeType.Article,
            id = "article-test-id",
            title = "Article Title",
            subtitle = "Article subtitle",
            category = "Category",
            region = "Region",
        )
        return GraphExploreUiState(
            centerNode = centerNode,
            neighbors = DiscoverySectionState(
                data = GraphNeighborsResult(
                    centerNodeKey = centerNode.nodeKey,
                    nodes = listOf(centerNode),
                    edges = emptyList(),
                ),
            ),
            similar = DiscoverySectionState(data = GraphSimilarResult(emptyList())),
            explore = DiscoverySectionState(
                data = GraphExploreResult(2, emptyList(), emptyList()),
            ),
            evidence = DiscoverySectionState(data = GraphEvidenceResult(emptyList(), emptyList())),
        )
    }
}
