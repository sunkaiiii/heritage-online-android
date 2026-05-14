package com.duckylife.heritage.modern.feature.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.duckylife.heritage.modern.feature.articles.detail.ArticleDetailRoute

private data object ArticlesList

private data class ArticleDetail(val id: String)

@Composable
fun ArticlesNavHost(
    modifier: Modifier = Modifier,
) {
    val backStack = remember { mutableStateListOf<Any>(ArticlesList) }
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
                        onArticleSelected = { articleId ->
                            backStack.add(ArticleDetail(articleId))
                        },
                        modifier = modifier,
                    )
                }

                is ArticleDetail -> NavEntry(key) {
                    ArticleDetailRoute(
                        articleId = key.id,
                        onBack = { backStack.removeLastOrNull() },
                        modifier = modifier,
                    )
                }

                else -> NavEntry(Unit) {
                    ArticlesRoute(
                        onArticleSelected = { articleId ->
                            backStack.add(ArticleDetail(articleId))
                        },
                        modifier = modifier,
                    )
                }
            }
        },
    )
}
