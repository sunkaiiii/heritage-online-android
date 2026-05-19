package com.duckylife.heritage.modern.feature.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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

@Composable
fun ArticlesNavHost(
    onSettingsSelected: () -> Unit,
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    pendingNavigation: MyPageDestination.Article? = null,
    onPendingNavigationConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val backStack = remember { mutableStateListOf<Any>(ArticlesList) }
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
