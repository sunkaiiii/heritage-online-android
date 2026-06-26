package com.duckylife.heritage.modern.feature.discovery.graphexplore

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import coil3.ImageLoader
import javax.inject.Inject
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckylife.heritage.modern.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState
import com.duckylife.heritage.modern.feature.discovery.GraphTab
import com.duckylife.heritage.modern.feature.graph.model.AiInferredEdgeUiModel
import com.duckylife.heritage.modern.feature.graph.model.AiInferredEdgesResult
import com.duckylife.heritage.modern.feature.graph.model.AssociationLevel
import com.duckylife.heritage.modern.feature.graph.model.GraphEdgeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarItemUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GraphExploreRouteTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var imageLoader: ImageLoader

    @Before
    fun init() {
        hiltRule.inject()
    }

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
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
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
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
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
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
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
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
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
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
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
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
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
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
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
                                        title = "Center Article",
                                    ),
                                    GraphNodeUiModel(
                                        nodeKey = "article-cached",
                                        type = GraphNodeType.Article,
                                        id = "article-cached-id",
                                        title = "Cached Article",
                                    ),
                                ),
                                edges = listOf(
                                    GraphEdgeUiModel(
                                        fromNodeKey = "article-test",
                                        toNodeKey = "article-cached",
                                        relationType = GraphRelationType.RelatedTo,
                                    ),
                                ),
                            ),
                            errorKind = ErrorKind.ServerError,
                        ),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
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
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_explore_invalid_route_title))
            .assertIsDisplayed()
    }

    @Test
    fun similarEmptyStateOffersExploreAction() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(
                        selectedTab = GraphTab.Similar,
                        similar = DiscoverySectionState(data = GraphSimilarResult(emptyList())),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_similar_empty_title))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.graph_similar_empty_action))
            .assertIsDisplayed()
    }

    @Test
    fun pathExplainBottomSheetOpensWhenTargetSet() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(
                        selectedTab = GraphTab.Similar,
                        pathExplainSheet = PathExplainSheetState(
                            targetNode = GraphNodeUiModel(
                                nodeKey = "article-target",
                                type = GraphNodeType.Article,
                                id = "a2",
                                title = "Related Article",
                            ),
                            isLoading = true,
                        ),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_path_explain_title))
            .assertIsDisplayed()
        composeRule.onNodeWithText(
            context.getString(R.string.graph_path_explain_subtitle, "Article Title", "Related Article"),
        ).assertIsDisplayed()
    }

    @Test
    fun exploreOverviewFallsBackWhenGraphIsTooLarge() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(
                        selectedTab = GraphTab.Explore,
                        explore = DiscoverySectionState(data = largeExploreResult()),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_explore_overview_view))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.graph_overview_too_large_title))
            .assertIsDisplayed()
    }

    @Test
    fun exploreTabShowsEmptyStateWhenThereAreNoNodes() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(
                        selectedTab = GraphTab.Explore,
                        explore = DiscoverySectionState(data = GraphExploreResult(2, emptyList(), emptyList())),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_explore_empty_title))
            .assertIsDisplayed()
    }

    @Test
    fun exploreOverviewFallsBackWhenThereAreNoEdges() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(
                        selectedTab = GraphTab.Explore,
                        explore = DiscoverySectionState(
                            data = GraphExploreResult(
                                depth = 2,
                                nodes = listOf(
                                    GraphNodeUiModel(
                                        nodeKey = "node-1",
                                        type = GraphNodeType.Article,
                                        id = "node-1",
                                        title = "Node 1",
                                    ),
                                ),
                                edges = emptyList(),
                                centerNodeKey = "node-1",
                            ),
                        ),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_explore_overview_view))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.graph_overview_no_edges_title))
            .assertIsDisplayed()
    }

    @Test
    fun aiInferredEmptyStateIsSeparateFromEvidence() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(
                        selectedTab = GraphTab.Evidence,
                        includeAiInferred = true,
                        evidence = DiscoverySectionState(data = GraphEvidenceResult(emptyList(), emptyList())),
                        aiInferredEdges = DiscoverySectionState(data = AiInferredEdgesResult(emptyList())),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_evidence_empty_title))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.graph_ai_inferred_empty_title))
            .assertIsDisplayed()
    }

    @Test
    fun aiInferredErrorKeepsEvidenceContent() {
        composeRule.setContent {
            HeritageTheme {
                GraphExploreScreen(
                    uiState = loadedUiState().copy(
                        selectedTab = GraphTab.Evidence,
                        includeAiInferred = true,
                        evidence = DiscoverySectionState(
                            data = GraphEvidenceResult(
                                evidence = emptyList(),
                                warnings = emptyList(),
                            ),
                        ),
                        aiInferredEdges = DiscoverySectionState(errorKind = ErrorKind.ServerError),
                    ),
                    onBack = {},
                    onTabSelected = {},
                    onRetry = {},
                    onRefresh = {},
                    onToggleAiInferred = {},
                    onExploreDepthSelected = {},
                    onPathClick = {},
                    onPathExplainDismiss = {},
                    onPathExplainRetry = {},
                    onPathExplainLoadBridge = {},
                    onNodeClick = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.graph_evidence_intro))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.action_retry))
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
        val neighborNode = GraphNodeUiModel(
            nodeKey = "article-related",
            type = GraphNodeType.Article,
            id = "article-related-id",
            title = "Related Article",
            subtitle = "Related subtitle",
        )
        val edge = GraphEdgeUiModel(
            fromNodeKey = centerNode.nodeKey,
            toNodeKey = neighborNode.nodeKey,
            relationType = GraphRelationType.RelatedTo,
            reason = "同属一个主题",
        )
        return GraphExploreUiState(
            centerNode = centerNode,
            neighbors = DiscoverySectionState(
                data = GraphNeighborsResult(
                    centerNodeKey = centerNode.nodeKey,
                    nodes = listOf(centerNode, neighborNode),
                    edges = listOf(edge),
                ),
            ),
            similar = DiscoverySectionState(
                data = GraphSimilarResult(
                    items = listOf(
                        GraphSimilarItemUiModel(
                            node = neighborNode,
                            associationLevel = AssociationLevel.High,
                            reasons = listOf("共享主题"),
                            sharedTopics = listOf("传统技艺"),
                            sharedNeighborCount = 2,
                        ),
                    ),
                ),
            ),
            explore = DiscoverySectionState(
                data = GraphExploreResult(
                    depth = 2,
                    nodes = listOf(centerNode, neighborNode),
                    edges = listOf(edge),
                    centerNodeKey = centerNode.nodeKey,
                ),
            ),
            evidence = DiscoverySectionState(data = GraphEvidenceResult(emptyList(), emptyList())),
        )
    }

    private fun largeExploreResult(): GraphExploreResult {
        val nodes = (0..36).map { index ->
            GraphNodeUiModel(
                nodeKey = "node-$index",
                type = if (index == 0) GraphNodeType.Article else GraphNodeType.Category,
                id = "id-$index",
                title = "Node $index",
            )
        }
        val edges = (1..73).map { index ->
            GraphEdgeUiModel(
                fromNodeKey = "node-0",
                toNodeKey = "node-${((index - 1) % 36) + 1}",
                relationType = GraphRelationType.Topic,
            )
        }
        return GraphExploreResult(
            depth = 2,
            nodes = nodes,
            edges = edges,
            centerNodeKey = "node-0",
        )
    }
}
