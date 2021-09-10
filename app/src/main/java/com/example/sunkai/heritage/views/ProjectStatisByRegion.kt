package com.example.sunkai.heritage.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
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
                val maxValue = statisByRegion.maxOf { it.value }.toInt()
                val maxColor = Color(0XFF99FFBE)
                StatisitcsRegionColumn(
                    startIndex = 0,
                    statisByRegion = statisByRegion,
                    modifier = modifier,
                    maxValue,
                    maxColor
                )
                StatisitcsRegionColumn(
                    startIndex = 1,
                    statisByRegion = statisByRegion,
                    modifier = modifier,
                    maxValue,
                    maxColor
                )
            }
        }
    }
}

@Composable
fun StatisitcsRegionColumn(
    startIndex: Int,
    statisByRegion: List<HeritageProjectStatisticsItem>,
    modifier: Modifier,
    maxValue: Int,
    maxColor: Color,
    stepping: Int = 2,
    minAlpha: Float = 35 / 255f
) {
    val size = statisByRegion.size
    var index = startIndex
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        while (index < size) {
            val it = statisByRegion[index]
            val valuePercentage = it.value.toFloat() / maxValue;
            val valueAlpha = 1 - ((1 - minAlpha) - (1 - minAlpha) * valuePercentage)
            Column {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    Modifier
                        .background(
                            color = Color(
                                maxColor.red,
                                maxColor.green,
                                maxColor.blue,
                                valueAlpha
                            ), shape = RoundedCornerShape(32.dp)
                        )
                        .fillMaxWidth(0.95f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(it.name,textAlign = TextAlign.Center)
                        Text(it.value.toString())
                    }
                    index += stepping
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

        }
    }
}