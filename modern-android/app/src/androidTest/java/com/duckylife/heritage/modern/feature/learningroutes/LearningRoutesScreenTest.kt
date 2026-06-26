package com.duckylife.heritage.modern.feature.learningroutes

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSummaryUiModel
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningRoutesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun filterChipsAreVisibleAndSelectable() {
        var selectedDifficulty: LearningRouteDifficulty? = null
        composeRule.setContent {
            HeritageTheme {
                LearningRoutesScreen(
                    uiState = LearningRoutesUiState(
                        selectedDifficulty = LearningRouteDifficulty.All,
                        routes = LearningRoutesSectionState(data = emptyList()),
                    ),
                    onBack = {},
                    onRetry = {},
                    onDifficultySelected = { selectedDifficulty = it },
                    onRouteClick = {},
                    onBuildFromSeed = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.learning_route_difficulty_all))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.learning_route_difficulty_beginner))
            .assertIsDisplayed()

        composeRule.onNodeWithText(context.getString(R.string.learning_route_difficulty_deep))
            .performClick()

        assertEquals(LearningRouteDifficulty.Deep, selectedDifficulty)
    }

    @Test
    fun routeCardIsVisibleAndClickable() {
        var clickedRouteId: String? = null
        composeRule.setContent {
            HeritageTheme {
                LearningRoutesScreen(
                    uiState = LearningRoutesUiState(
                        routes = LearningRoutesSectionState(
                            data = listOf(
                                LearningRouteSummaryUiModel(
                                    routeId = "r1",
                                    title = "Test Route",
                                    subtitle = "Subtitle",
                                    description = null,
                                    difficulty = LearningRouteDifficulty.Beginner,
                                    estimatedMinutes = 15,
                                    stepCount = 3,
                                    tags = listOf("Tag"),
                                    coverImageUrl = null,
                                ),
                            ),
                        ),
                    ),
                    onBack = {},
                    onRetry = {},
                    onDifficultySelected = {},
                    onRouteClick = { clickedRouteId = it },
                    onBuildFromSeed = {},
                )
            }
        }

        composeRule.onNodeWithText("Test Route")
            .assertIsDisplayed()
            .performClick()

        assertEquals("r1", clickedRouteId)
    }

    @Test
    fun emptyStateIsDisplayedWhenNoRoutes() {
        composeRule.setContent {
            HeritageTheme {
                LearningRoutesScreen(
                    uiState = LearningRoutesUiState(
                        routes = LearningRoutesSectionState(data = emptyList()),
                    ),
                    onBack = {},
                    onRetry = {},
                    onDifficultySelected = {},
                    onRouteClick = {},
                    onBuildFromSeed = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.learning_routes_empty_title))
            .assertIsDisplayed()
    }

    @Test
    fun buildFromSeedCardIsVisibleWhenSeedPresent() {
        var buildClicked = false
        composeRule.setContent {
            HeritageTheme {
                LearningRoutesScreen(
                    uiState = LearningRoutesUiState(
                        seedType = "article",
                        seedId = "a1",
                        routes = LearningRoutesSectionState(data = emptyList()),
                    ),
                    onBack = {},
                    onRetry = {},
                    onDifficultySelected = {},
                    onRouteClick = {},
                    onBuildFromSeed = { buildClicked = true },
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.learning_routes_build_from_seed_title))
            .assertIsDisplayed()
            .performClick()

        assertTrue(buildClicked)
    }
}
