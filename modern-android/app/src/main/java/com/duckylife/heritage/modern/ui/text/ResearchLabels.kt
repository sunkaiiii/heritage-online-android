package com.duckylife.heritage.modern.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.feature.research.model.ResearchDataScope
import com.duckylife.heritage.modern.feature.research.model.ResearchSourceType

/**
 * 将研究资料包来源类型映射为本地化显示文案。
 */
@Composable
fun localizedResearchSourceType(type: ResearchSourceType): String =
    when (type) {
        ResearchSourceType.GraphRagPack -> stringResource(R.string.research_source_graph_rag_pack)
        ResearchSourceType.Snapshot -> stringResource(R.string.research_source_snapshot)
        ResearchSourceType.Unknown -> stringResource(R.string.research_source_unknown)
    }

/**
 * 将研究资料包数据范围标记映射为本地化显示文案。
 */
@Composable
fun localizedResearchDataScope(scope: ResearchDataScope): String =
    when (scope) {
        ResearchDataScope.Content -> stringResource(R.string.research_scope_content)
        ResearchDataScope.Evidence -> stringResource(R.string.research_scope_evidence)
        ResearchDataScope.AiResults -> stringResource(R.string.research_scope_ai_results)
        ResearchDataScope.AiInferred -> stringResource(R.string.research_scope_ai_inferred)
    }

/**
 * 将多个数据范围标记组合为本地化显示文案。
 */
@Composable
fun localizedResearchDataScopeList(scopes: List<ResearchDataScope>): String {
    if (scopes.isEmpty()) return ""
    val labels = mutableListOf<String>()
    for (scope in scopes) {
        labels.add(localizedResearchDataScope(scope))
    }
    return labels.joinToString(" · ")
}

/**
 * 组合来源类型标签与原始 ID 详情。
 *
 * 当 [detail] 为空时仅返回类型标签；否则返回 "类型 · 详情"。
 */
@Composable
fun localizedResearchSource(
    type: ResearchSourceType,
    detail: String?,
): String {
    val typeLabel = localizedResearchSourceType(type)
    return detail?.takeIf { it.isNotBlank() }?.let { "$typeLabel · $it" } ?: typeLabel
}
