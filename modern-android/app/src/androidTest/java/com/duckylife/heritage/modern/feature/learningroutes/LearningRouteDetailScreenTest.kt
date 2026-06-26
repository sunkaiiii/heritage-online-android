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
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteDetailUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSectionUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteStepUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningRouteDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun routeHeaderAndProgressAreDisplayed() {
        composeRule.setContent {
            HeritageTheme {
                LearningRouteDetailScreen(
                    uiState = LearningRouteDetailUiState(
                        route = buildTestRoute(),
                        completedStepIds = setOf("step-1"),
                    ),
                    onBack = {},
                    onRefresh = {},
                    onStepChecked = { _, _ -> },
                    onStepContentClick = { _, _ -> },
                    onLoadNextStep = {},
                    onShowRestartConfirmation = {},
                    onDismissRestartConfirmation = {},
                    onConfirmRestart = {},
                )
            }
        }

        composeRule.onNodeWithText("Test Route").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.learning_route_progress_format, 1, 2))
            .assertIsDisplayed()
        composeRule.onNodeWithText("Section 1").assertIsDisplayed()
        composeRule.onNodeWithText("Step 1").assertIsDisplayed()
        composeRule.onNodeWithText("Step 2").assertIsDisplayed()
    }

    @Test
    fun stepCheckboxTogglesProgress() {
        var checkedStepId: String? = null
        var checkedValue: Boolean? = null

        composeRule.setContent {
            HeritageTheme {
                LearningRouteDetailScreen(
                    uiState = LearningRouteDetailUiState(
                        route = buildTestRoute(),
                    ),
                    onBack = {},
                    onRefresh = {},
                    onStepChecked = { id, checked ->
                        checkedStepId = id
                        checkedValue = checked
                    },
                    onStepContentClick = { _, _ -> },
                    onLoadNextStep = {},
                    onShowRestartConfirmation = {},
                    onDismissRestartConfirmation = {},
                    onConfirmRestart = {},
                )
            }
        }

        composeRule.onNodeWithText("Step 1")
            .assertIsDisplayed()
            .performClick()

        assertEquals("step-1", checkedStepId)
        assertEquals(true, checkedValue)
    }

    @Test
    fun completedRouteShowsRestartButton() {
        composeRule.setContent {
            HeritageTheme {
                LearningRouteDetailScreen(
                    uiState = LearningRouteDetailUiState(
                        route = buildTestRoute(),
                        completedStepIds = setOf("step-1", "step-2"),
                    ),
                    onBack = {},
                    onRefresh = {},
                    onStepChecked = { _, _ -> },
                    onStepContentClick = { _, _ -> },
                    onLoadNextStep = {},
                    onShowRestartConfirmation = {},
                    onDismissRestartConfirmation = {},
                    onConfirmRestart = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.learning_route_completed_title))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.learning_route_restart))
            .assertIsDisplayed()
    }

    @Test
    fun notFoundErrorShowsRouteMissingMessage() {
        composeRule.setContent {
            HeritageTheme {
                LearningRouteDetailScreen(
                    uiState = LearningRouteDetailUiState(
                        errorKind = ErrorKind.NotFound,
                    ),
                    onBack = {},
                    onRefresh = {},
                    onStepChecked = { _, _ -> },
                    onStepContentClick = { _, _ -> },
                    onLoadNextStep = {},
                    onShowRestartConfirmation = {},
                    onDismissRestartConfirmation = {},
                    onConfirmRestart = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.learning_route_not_found))
            .assertIsDisplayed()
    }

    @Test
    fun restartConfirmationDialogIsDisplayed() {
        composeRule.setContent {
            HeritageTheme {
                LearningRouteDetailScreen(
                    uiState = LearningRouteDetailUiState(
                        route = buildTestRoute(),
                        completedStepIds = setOf("step-1", "step-2"),
                        showRestartConfirmDialog = true,
                    ),
                    onBack = {},
                    onRefresh = {},
                    onStepChecked = { _, _ -> },
                    onStepContentClick = { _, _ -> },
                    onLoadNextStep = {},
                    onShowRestartConfirmation = {},
                    onDismissRestartConfirmation = {},
                    onConfirmRestart = {},
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.learning_route_restart_confirm_title))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.learning_route_restart_confirm_message))
            .assertIsDisplayed()
    }

    private fun buildTestRoute(): LearningRouteDetailUiModel = LearningRouteDetailUiModel(
        routeId = "route-1",
        title = "Test Route",
        description = "A test route",
        difficulty = LearningRouteDifficulty.Beginner,
        estimatedMinutes = 20,
        sections = listOf(
            LearningRouteSectionUiModel(
                sectionId = "section-1",
                title = "Section 1",
                description = null,
                stepIds = listOf("step-1", "step-2"),
            ),
        ),
        steps = listOf(
            LearningRouteStepUiModel(
                stepId = "step-1",
                order = 1,
                title = "Step 1",
                description = "First step",
                targetType = "article",
                targetId = "article-1",
                reason = null,
                estimatedMinutes = 5,
                required = true,
            ),
            LearningRouteStepUiModel(
                stepId = "step-2",
                order = 2,
                title = "Step 2",
                description = "Second step",
                targetType = null,
                targetId = null,
                reason = null,
                estimatedMinutes = 0,
                required = false,
            ),
        ),
        relatedRoutes = emptyList(),
    )
}
