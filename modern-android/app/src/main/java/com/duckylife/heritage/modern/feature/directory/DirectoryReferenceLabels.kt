package com.duckylife.heritage.modern.feature.directory

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto

@Composable
internal fun DirectoryReferenceDto.localizedKindLabel(): String? {
    val rawKind = kind?.takeIf { it.isNotBlank() } ?: return null
    val labelRes = when {
        rawKind.equals("inheritor", ignoreCase = true) -> R.string.nav_inheritors
        else -> DirectoryItemKind.entries
            .firstOrNull { it.wireName.equals(rawKind, ignoreCase = true) }
            ?.labelRes
    }
    return labelRes?.let { stringResource(it) } ?: rawKind
}

@get:StringRes
internal val DirectoryItemKind.labelRes: Int
    get() = when (this) {
        DirectoryItemKind.NationalProject -> R.string.directory_kind_national_project
        DirectoryItemKind.CulturalEcoZone -> R.string.directory_kind_cultural_eco_zone
        DirectoryItemKind.ProductiveProtectionBase -> R.string.directory_kind_productive_protection_base
        DirectoryItemKind.UnescoEntry -> R.string.directory_kind_unesco_entry
        DirectoryItemKind.ChinaUnescoEntry -> R.string.directory_kind_china_unesco_entry
        DirectoryItemKind.ContractingState -> R.string.directory_kind_contracting_state
    }
