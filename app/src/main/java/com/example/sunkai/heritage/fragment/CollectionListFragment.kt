package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.database.entities.Collection
import com.example.sunkai.heritage.entity.CollectionListViewModel
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.tools.Utils.dip2px
import com.example.sunkai.heritage.tools.Utils.px2dip
import com.example.sunkai.heritage.tools.buildUrl
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalCoilApi
@AndroidEntryPoint
class CollectionListFragment : BaseGlideFragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(CollectionListViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Column(Modifier.fillMaxSize()) {
                    CollectionTitle()
                    CollectionPager()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun CollectionTitle() {
        Text(getString(R.string.collection), modifier = Modifier.padding(start = 24.dp, top = 36.dp, bottom = 36.dp, end = 24.dp))
    }


    @Composable
    private fun CollectionPager() {
        Column(Modifier.fillMaxSize()) {
            PagerTab()
            PagerContent()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun PagerContent() {
        val collectionList = viewModel.collectionListData.observeAsState().value
        val width = 2080
        val cornerShape = RoundedCornerShape(12.dp)
        if (!collectionList.isNullOrEmpty()) {
            LazyVerticalGrid(cells = GridCells.Adaptive(180.dp)) {
                items(collectionList.size) {
                    Box(
                        Modifier
                            .padding(12.dp)
                            .fillMaxWidth()) {
                        Column(
                            Modifier
                                .background(shape = cornerShape, color = Color(0XFFE7F0FF))
                                .fillMaxWidth()
                        ) {
                            val item = collectionList[it]
                            if (!item.imageLink.isNullOrBlank()) {
                                Image(painter = rememberImagePainter(buildUrl(item.imageLink)), contentDescription = null, contentScale = ContentScale.FillHeight, modifier = Modifier
                                    .clip(cornerShape)
                                    .fillMaxWidth()
                                    .height(200.dp))
                            }
                            Text(text = item.content, color = Color(0xFF434247), fontSize = 13.sp,modifier = Modifier.padding(start = 16.dp,end = 16.dp))
                        }
                    }

                }
            }
        } else{
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if(collectionList?.size == 0){
                    Text(text = "没有收藏")
                }else{
                    CircularProgressIndicator()
                }

            }
        }

    }

    @Preview
    @Composable
    private fun PagerTab() {
        val types = Collection.CollectionType.values()
        val state = viewModel.selectedIndex.observeAsState().value
        ScrollableTabRow(selectedTabIndex = state?:0) {
            types.forEachIndexed { index, type ->
                Tab(text = { Text(type.getName()) }, selected = state == index, onClick = { viewModel.selectedIndex.value = index })
            }

        }
    }

}