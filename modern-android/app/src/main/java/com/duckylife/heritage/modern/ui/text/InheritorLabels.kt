package com.duckylife.heritage.modern.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R

/**
 * 传承人性别可选项。
 *
 * [wireValue] 是发送给后端的值（与现有数据一致）。
 */
enum class InheritorGender(
    val wireValue: String,
    val labelRes: Int,
) {
    Any("", R.string.filter_gender_any),
    Male("男", R.string.filter_gender_male),
    Female("女", R.string.filter_gender_female),
}

/**
 * 将后端返回的性别 wire value 映射为本地化显示文案；无法识别时返回原值。
 */
@Composable
fun localizedInheritorGender(value: String?): String {
    if (value.isNullOrBlank()) return ""
    return InheritorGender.entries
        .firstOrNull { it.wireValue == value }
        ?.let { stringResource(it.labelRes) }
        ?: value
}
