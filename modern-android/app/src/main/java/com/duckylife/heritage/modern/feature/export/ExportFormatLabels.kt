package com.duckylife.heritage.modern.feature.export

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat

@Composable
fun ExportFormat.localizedLabel(): String = stringResource(
    when (this) {
        ExportFormat.Markdown -> R.string.export_format_markdown
        ExportFormat.Json -> R.string.export_format_json
        ExportFormat.Csv -> R.string.export_format_csv
        ExportFormat.Unknown -> R.string.export_format_unknown
    },
)
