package com.duckylife.heritage.modern.feature.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleReferenceDto
import com.duckylife.heritage.modern.feature.articles.detail.ArticleDetailRoute
import com.duckylife.heritage.modern.feature.my.MyPageDestination

private data object ArticlesList

private data class ArticleDetail(
    val id: String? = null,
    val sourceId: String? = null,
    val sourceUrl: String? = null,
    val category: ArticleCategory = ArticleCategory.News,
)

private fun serializeArticles(stack: List<Any>): String =
    stack.joinToString("\n") { entry ->
        when (entry) {
            ArticlesList -> "L"
            is ArticleDetail -> "D|${entry.id.orEmpty()}|${entry.sourceId.orEmpty()}|${entry.sourceUrl.orEmpty()}|${entry.category.wireName}"
            else -> "L"
        }
    }

private fun deserializeArticles(str: String): List<Any> =
    if (str.isBlank()) listOf(ArticlesList)
    else str.split("\n").mapNotNull { item ->
        val parts = item.split("|")
        when (parts[0]) {
            "D" -> ArticleDetail(
                id = parts.getOrNull(1)?.takeIf { it.isNotEmpty() },
                sourceId = parts.getOrNull(2)?.takeIf { it.isNotEmpty() },
                sourceUrl = parts.getOrNull(3)?.takeIf { it.isNotEmpty() },
                category = ArticleCategory.entries.firstOrNull { it.wireName == parts.getOrNull(4) } ?: ArticleCategory.News,
            )
            else -> ArticlesList
        }
    }

@Composable
fun ArticlesNavHost(
    onSettingsSelected: () -> Unit,
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    pendingNavigation: MyPageDestination.Article? = null,
    onPendingNavigationConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var savedStack by rememberSaveable { mutableStateOf("L") }
    val backStack = remember { mutableStateListOf<Any>().also { it.addAll(deserializeArticles(savedStack)) } }
    LaunchedEffect(backStack.size) {
        savedStack = serializeArticles(backStack.toList())
    }
    val isInDetail = backStack.lastOrNull() is ArticleDetail
    LaunchedEffect(isInDetail) {
        onSecondaryDestinationChanged(isInDetail)
    }
    LaunchedEffect(pendingNavigation) {
        val dest = pendingNavigation ?: return@LaunchedEffect
        backStack.clear()
        backStack.add(ArticlesList)
        backStack.add(
            ArticleDetail(
                id = dest.articleId,
                sourceId = dest.sourceId,
                sourceUrl = dest.sourceUrl,
                category = dest.category,
            ),
        )
        onPendingNavigationConsumed()
    }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { key ->
            when (key) {
                ArticlesList -> NavEntry(key) {
                    ArticlesRoute(
                        onSettingsSelected = onSettingsSelected,
                        onArticleSelected = { articleId ->
                            backStack.add(ArticleDetail(id = articleId))
                        },
                        modifier = modifier,
                    )
                }

                is ArticleDetail -> NavEntry(key) {
                    ArticleDetailRoute(
                        articleId = key.id,
                        sourceId = key.sourceId,
                        sourceUrl = key.sourceUrl,
                        category = key.category,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedArticleSelected = { reference, category ->
                            reference.toArticleDetail(category)?.let(backStack::add)
                        },
                        modifier = modifier,
                    )
                }

                else -> NavEntry(Unit) {
                    ArticlesRoute(
                        onSettingsSelected = onSettingsSelected,
                        onArticleSelected = { articleId ->
                            backStack.add(ArticleDetail(id = articleId))
                        },
                        modifier = modifier,
                    )
                }
            }
        },
    )
}

private fun ArticleReferenceDto.toArticleDetail(category: ArticleCategory): ArticleDetail? =
    when {
        !sourceId.isNullOrBlank() -> ArticleDetail(
            sourceId = sourceId,
            category = category,
        )

        !detailUrl.isNullOrBlank() -> ArticleDetail(
            sourceUrl = detailUrl,
            category = category,
        )

        else -> null
    }
