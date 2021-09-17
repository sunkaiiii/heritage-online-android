package com.example.sunkai.heritage.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.database.entities.SearchNewsHistory
import com.example.sunkai.heritage.entity.NewsPages
import com.example.sunkai.heritage.entity.SearchNewsViewModel
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.tools.Utils
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class SearchNewsFragment : BaseGlideFragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(SearchNewsViewModel::class.java) }
    private val autoSearchHandler by lazy {
        object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val text = msg.data.get("text") as String? ?: return
                viewModel.startSearchNews(text)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchNewsView()
            }
            background = ColorDrawable(Utils.getColorResource(R.color.search_news_background))
        }
    }

    @Composable
    fun SearchNewsView() {
        val searchResult = viewModel.searchNewsResult.asFlow().collectAsLazyPagingItems()
        val searchHistoryState = viewModel.searchNewsHistory.observeAsState()
        val searchHistoryNews = searchHistoryState.value?.sortedByDescending { it.searchHappenedTime }
            ?: listOf()
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 30.dp)
        ) {
            SearchNewsTopBar(onBack = { findNavController().popBackStack() })
            Spacer(Modifier.height(27.dp))
            SearchNewsEditTextBar()
            Spacer(Modifier.height(15.dp))
            SearchNewsArgumentView(searchHistoryNews)
            Spacer(Modifier.height(30.dp))
            SearchNewsResultList(searchResult)

        }
    }

    @Composable
    fun SearchNewsTopBar(onBack: () -> Unit) {
        Image(
            painter = painterResource(id = R.drawable.general_arrow_back),
            contentDescription = "back",
            Modifier.clickable {
                onBack()
            })
    }

    @Composable
    fun SearchNewsEditTextBar() {
        var text = viewModel.searchEditFieldText.observeAsState("")
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val (editTextCard, filterButton) = createRefs()
            Card(
                Modifier
                    .size(42.dp)
                    .constrainAs(filterButton) {
                        end.linkTo(parent.end)
                    }) {
            }
            Card(
                Modifier
                    .height(42.dp)
                    .constrainAs(editTextCard) {
                        end.linkTo(filterButton.start, margin = 18.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        width = Dimension.fillToConstraints
                    }, shape = RoundedCornerShape(21.dp)
            ) {
                Row(Modifier.fillMaxHeight()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search_grey_400_24dp),
                        contentDescription = "搜索",
                        Modifier.padding(start = 18.dp, top = 12.dp, bottom = 12.dp)
                    )
                    TextField(
                        value = text.value, onValueChange = {
                            viewModel.searchEditFieldText.value = it
                            val msg = Message()
                            msg.data = bundleOf("text" to it)
                            msg.what = 0
                            autoSearchHandler.removeMessages(0)
                            autoSearchHandler.sendMessageDelayed(msg, 1000)
                        }, modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun SearchNewsArgumentView(argument: List<SearchNewsHistory>) {
        LazyVerticalGrid(cells = GridCells.Adaptive(minSize = 72.dp)) {
            items(argument.size) {
                Row(
                    Modifier
                        .height(24.dp)
                        .padding(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 4.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clickable { viewModel.startSearchNews(argument[it].searchValue) },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(6.dp))
                    Text(argument[it].searchValue, fontSize = 12.sp, color = Color(0xff252A3B))
                    Spacer(Modifier.width(4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.icon_material_cancel),
                        contentDescription = null,
                        Modifier
                            .size(8.dp)
                            .clickable {
                                viewModel.deleteSearchHistory(argument[it])
                            },
                    )
                    Spacer(Modifier.width(6.dp))
                }
            }
        }
    }


    @Composable
    fun SearchNewsResultList(newsList: LazyPagingItems<NewsListResponse>) {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(newsList) { news ->
                SearchNewsResultListItem(news)
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SearchNewsResultListItem(news: NewsListResponse?) {
        news ?: return
        Card(elevation = 0.dp, shape = RoundedCornerShape(16.dp), onClick = {
            jumpToNewsDetail(news)
        }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            ) {
                Text(news.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(26.dp))
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.icon_material_author),
                        contentDescription = "作者",
                        Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("", color = Color(0xFFBCBCBC))
                    Spacer(Modifier.width(30.dp))
                    Image(
                        painter = painterResource(id = R.drawable.icon_material_timelapse),
                        contentDescription = "发布时间",
                        Modifier.size(18.dp)
                    )
                    Text(news.date, color = Color(0xFFBCBCBC))
                }
            }
        }

    }

    private fun jumpToNewsDetail(news: NewsListResponse) {
        findNavController().navigate(
            R.id.search_news_to_news_detail,
            bundleOf(
                Pair<String, Serializable>(DATA, news),
                Pair<String, Serializable>(API, NewsPages.NewsPage)
            )
        )
    }

    @Preview
    @Composable
    fun SearchNewsTopBarPreview() {
        SearchNewsTopBar {

        }
    }

    @Preview
    @Composable
    fun SearchNewsEditTextBarPreview() {
        SearchNewsEditTextBar()
    }

    @Preview
    @Composable
    fun SearchNewsArgumentViewPreview() {
        SearchNewsArgumentView(
            argument = listOf(
               SearchNewsHistory("长江")
            )
        )
    }
}