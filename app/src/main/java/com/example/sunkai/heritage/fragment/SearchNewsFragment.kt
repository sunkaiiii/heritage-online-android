package com.example.sunkai.heritage.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.SearchNewsViewModel
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.tools.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchNewsFragment : BaseGlideFragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(SearchNewsViewModel::class.java) }
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
            viewModel.startSearchNews("江苏")
        }
    }

    @Composable
    fun SearchNewsView() {
        val searchResult = viewModel.searchNewsResult.asFlow().collectAsLazyPagingItems()
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 30.dp)
        ) {
            SearchNewsTopBar(onBack = { findNavController().popBackStack() })
            Spacer(Modifier.height(27.dp))
            SearchNewsEditTextBar()
            Spacer(Modifier.height(15.dp))
            SearchNewsArgumentView(listOf())
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
                Column(Modifier.fillMaxHeight()) {

                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun SearchNewsArgumentView(argument: List<String>) {
        LazyVerticalGrid(cells = GridCells.Adaptive(minSize = 72.dp)) {
            items(argument.size) {
                Row(
                    Modifier
                        .height(24.dp)
                        .padding(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 4.dp)
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(14.dp))
                    Text(argument[it], fontSize = 12.sp, color = Color(0xff252A3B))
                    Spacer(Modifier.height(5.dp))
                    Image(
                        painter = painterResource(id = R.drawable.icon_material_cancel),
                        contentDescription = null,
                        Modifier.size(8.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                }
            }
        }
    }

    @Composable
    fun SearchNewsResultList(newsList: LazyPagingItems<NewsListResponse>){
        LazyColumn(Modifier.fillMaxSize()){
            items(newsList){news->
                Text(news?.title ?:"")
            }
        }
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
                "长江",
                "黄河",
                "长江",
                "黄河",
                "长江",
                "黄河",
                "长江",
                "黄河",
                "长江",
                "黄河",
                "长江",
                "黄河"
            )
        )
    }
}