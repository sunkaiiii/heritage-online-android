package com.example.sunkai.heritage.views

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.ProjectStatisticsViewModel
import com.example.sunkai.heritage.entity.response.HeritageProjectStatisticsItem

@ExperimentalFoundationApi
@Composable
fun ProjectStatisticsByRegion(
        statisByRegion: List<HeritageProjectStatisticsItem>,
        viewModel: ProjectStatisticsViewModel
) {
    val isExpand: Boolean by viewModel.isExpandStatisticsByRegion.observeAsState(false)
    Card(elevation = 8.dp, backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White) {
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
                        maxColor,
                        isExpand
                )
                StatisitcsRegionColumn(
                        startIndex = 1,
                        statisByRegion = statisByRegion,
                        modifier = modifier,
                        maxValue,
                        maxColor,
                        isExpand
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                    Modifier
                            .align(Alignment.End)
                            .clickable {
                                viewModel.onExpandAreaClick()
                            },
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (isExpand) "收起" else "显示更多", color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                Image(
                        painter = painterResource(if (isExpand) R.drawable.shrink else R.drawable.expand),
                        contentDescription = "",
                        modifier = Modifier
                                .width(24.dp)
                                .height(24.dp),
                        colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray)
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
        isExpand: Boolean,
        stepping: Int = 2,
        minAlpha: Float = 35 / 255f
) {
    val size = statisByRegion.size
    var index = startIndex
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        while (index < if (isExpand) size else 3 * stepping) {
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
                        Text(it.name, textAlign = TextAlign.Center)
                        Text(it.value.toString())
                    }
                    index += stepping
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

        }
    }
}