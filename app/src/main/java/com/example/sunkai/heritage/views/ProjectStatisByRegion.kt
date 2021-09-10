package com.example.sunkai.heritage.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.example.sunkai.heritage.entity.response.HeritageProjectStatisticsItem

@ExperimentalFoundationApi
@Composable
fun ProjectStatisticsByRegion(statisByRegion: List<HeritageProjectStatisticsItem>) {
    Card(elevation = 8.dp) {
        Column(modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
                .onGloballyPositioned {

                }) {
            StatisticsProjectTitle(title = "申报地区")
            Spacer(modifier = Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                val modifier = Modifier.weight(1f)
                StatisitcsRegionColumn(startIndex = 0, statisByRegion = statisByRegion, modifier = modifier)
                StatisitcsRegionColumn(startIndex = 1, statisByRegion = statisByRegion, modifier = modifier)
            }
        }
    }
}

@Composable
fun StatisitcsRegionColumn(startIndex: Int, statisByRegion: List<HeritageProjectStatisticsItem>, modifier: Modifier, stepping: Int = 2) {
    val size = statisByRegion.size
    var index = startIndex
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        while (index < size) {
            val it = statisByRegion[index]
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                Text(it.name)
                Text(it.value.toString())
            }
            index += stepping
        }
    }
}