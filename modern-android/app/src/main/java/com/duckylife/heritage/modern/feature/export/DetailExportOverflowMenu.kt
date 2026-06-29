package com.duckylife.heritage.modern.feature.export

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R

/**
 * 详情页顶栏「更多」菜单中的「导出此内容」入口。
 *
 * @param enabled 是否可以导出（例如内容已加载）。
 * @param onExportClick 用户点击导出菜单项后的回调。
 */
@Composable
fun DetailExportOverflowMenu(
    enabled: Boolean,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = { expanded = true },
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = stringResource(R.string.action_more_options),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.export_menu_export_this_content)) },
                onClick = {
                    expanded = false
                    if (enabled) onExportClick()
                },
                enabled = enabled,
            )
        }
    }
}
