package com.example.sunkai.heritage.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.response.SearchProjectTypeViewData

@Composable
fun ProjectListSearchProjectDetailView(projectCategory: SearchProjectTypeViewData, modifier: Modifier = Modifier) {
    Column(modifier) {
        Card(modifier = Modifier
                .fillMaxWidth()
                .height(32.dp), shape = RoundedCornerShape(21.dp), elevation = 6.dp) {
            TextField(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent, shape = RoundedCornerShape(21.dp)), leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "搜索", Modifier.size(16.dp)) }, value = "", onValueChange = {})

        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(Modifier
                .fillMaxWidth()
                .height(124.dp)) {
            val category = projectCategory.projectTypes
            LazyColumn(Modifier
                    .weight(64f)
                    .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                items(category.size) {
                    Text(text = category[it], fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
            val years = projectCategory.projectYear
            LazyColumn(Modifier
                    .weight(56f)
                    .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                items(years.size) {
                    Text(text = years[it], fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
            Box(Modifier.weight(29f), contentAlignment = Alignment.Center) {
                Image(modifier = Modifier.size(24.dp), painter = painterResource(id = R.drawable.project_list_fragment_search_project_submit), contentDescription = "提交")
            }
        }
    }
}

@Preview
@Composable
fun ProjectListSearchProjectDetailViewPreview() {
    ProjectListSearchProjectDetailView(SearchProjectTypeViewData(listOf("民间文学", "民间音乐", "民间体育")), Modifier
            .height(242.dp)
            .width(198.dp))
}