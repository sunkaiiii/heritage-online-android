package com.duckylife.heritage.modern.feature.export

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportSampleItemDto
import com.duckylife.heritage.modern.ui.state.AsyncState
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentExportBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun showsFormatOptionsAndTriggersPreview() {
        var previewClicked = false
        composeTestRule.setContent {
            HeritageTheme {
                ContentExportSheetContent(
                    uiState = ContentExportUiState(
                        supportedFormats = setOf(ExportFormat.Markdown, ExportFormat.Json),
                        selectedFormat = ExportFormat.Markdown,
                    ),
                    onFormatSelected = {},
                    onIncludeSourcesChanged = {},
                    onIncludeImagesChanged = {},
                    onIncludeAiSummaryChanged = {},
                    onPreview = { previewClicked = true },
                    onExport = {},
                    onRetryTemplates = {},
                    onRetryPreview = {},
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.export_sheet_title))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.export_format_json))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.export_preview_button))
            .performClick()

        assertTrue(previewClicked)
    }

    @Test
    fun displaysPreviewResultAndShowsConfirmDialogOnExportClick() {
        var exportClicked = false
        composeTestRule.setContent {
            HeritageTheme {
                ContentExportSheetContent(
                    uiState = ContentExportUiState(
                        supportedFormats = setOf(ExportFormat.Markdown),
                        selectedFormat = ExportFormat.Markdown,
                        preview = AsyncState(
                            data = ExportPreviewUiModel(
                                estimatedItemCount = 1,
                                estimatedSize = "2 KB",
                                warnings = emptyList(),
                                samples = listOf(
                                    ExportSampleItemDto(title = "Sample A"),
                                ),
                            ),
                        ),
                    ),
                    onFormatSelected = {},
                    onIncludeSourcesChanged = {},
                    onIncludeImagesChanged = {},
                    onIncludeAiSummaryChanged = {},
                    onPreview = {},
                    onExport = { exportClicked = true },
                    onRetryTemplates = {},
                    onRetryPreview = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Sample A").assertIsDisplayed()

        // First click opens the secondary confirmation dialog, does not export yet.
        composeTestRule.onNodeWithText(context.getString(R.string.export_generate_and_share))
            .performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.export_confirm_title))
            .assertIsDisplayed()
        assertFalse(exportClicked)

        // Confirming the dialog triggers the actual export.
        composeTestRule.onNodeWithText(context.getString(R.string.confirm))
            .performClick()
        assertTrue(exportClicked)
    }

    @Test
    fun cancelsConfirmDialogWithoutExport() {
        var exportClicked = false
        composeTestRule.setContent {
            HeritageTheme {
                ContentExportSheetContent(
                    uiState = ContentExportUiState(
                        supportedFormats = setOf(ExportFormat.Markdown),
                        selectedFormat = ExportFormat.Markdown,
                        preview = AsyncState(
                            data = ExportPreviewUiModel(
                                estimatedItemCount = 1,
                                estimatedSize = "2 KB",
                                warnings = emptyList(),
                                samples = emptyList(),
                            ),
                        ),
                    ),
                    onFormatSelected = {},
                    onIncludeSourcesChanged = {},
                    onIncludeImagesChanged = {},
                    onIncludeAiSummaryChanged = {},
                    onPreview = {},
                    onExport = { exportClicked = true },
                    onRetryTemplates = {},
                    onRetryPreview = {},
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.export_generate_and_share))
            .performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.export_confirm_title))
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(context.getString(R.string.cancel))
            .performClick()

        // Dialog should be dismissed and export must not have been triggered.
        composeTestRule.onNodeWithText(context.getString(R.string.export_confirm_title))
            .assertDoesNotExist()
        assertFalse(exportClicked)
    }

    @Test
    fun oversizedWarningIsVisible() {
        composeTestRule.setContent {
            HeritageTheme {
                ContentExportSheetContent(
                    uiState = ContentExportUiState(
                        supportedFormats = setOf(ExportFormat.Markdown),
                        selectedFormat = ExportFormat.Markdown,
                        oversizedWarning = true,
                    ),
                    onFormatSelected = {},
                    onIncludeSourcesChanged = {},
                    onIncludeImagesChanged = {},
                    onIncludeAiSummaryChanged = {},
                    onPreview = {},
                    onExport = {},
                    onRetryTemplates = {},
                    onRetryPreview = {},
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.export_oversized_warning))
            .assertIsDisplayed()
    }
}
