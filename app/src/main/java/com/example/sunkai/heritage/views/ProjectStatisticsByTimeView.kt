package com.example.sunkai.heritage.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sunkai.heritage.entity.response.HeritageProjectStatisticsItem


@Composable
fun ProjectStatisticsByTime(statisByTime: List<HeritageProjectStatisticsItem>) {
    Card(elevation = 8.dp) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            StatisticsProjectTitle(title = "新增项目")
            Spacer(modifier = Modifier.height(28.dp))
            ProjectStatisticsByTimeBarChart(statisByTime)
        }
    }
}

@Composable
fun StatisticsProjectTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        DoubleCircleIndicator()
        Text(
            text = title,
            color = Color.Black,
            modifier = Modifier.padding(start = 4.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun DoubleCircleIndicator() {
    Box(
        modifier = Modifier
            .height(12.dp)
            .width(12.dp)
    ) {
        Canvas(
            modifier = Modifier
                .height(12.dp)
                .width(12.dp)
        ) {
            drawCircle(
                color = Color(0xFFD6E5FF)
            )
            drawCircle(
                color = Color(0XFF0073FF),
                radius = (12.dp).value / 3
            )
        }
    }
}

@Composable
private fun ProjectStatisticsByTimeBarChart(statisByTime: List<HeritageProjectStatisticsItem>) {
    Column() {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            val height = size.height
            val width = size.width
            val sizeOfBar = width / statisByTime.size
            val maxValueOfData = statisByTime.maxOf {
                it.value
            }
            val normalBarBrush =
                Brush.verticalGradient(listOf(Color(0XFFFFB4B1), Color(0XFFFFEBEB)))
            val maxValueBarBrush = Brush.verticalGradient(
                listOf(
                    Color(0XFFE65650),
                    Color(0XFFD4757F),
                    Color(0XFFD06F7A),
                    Color(0XFFFFB5B2)
                )
            )
            for (i in 0..5) {
                drawLine(
                    start = Offset(x = 0f, y = height / 5 * i),
                    end = Offset(x = width, height / 5 * i),
                    color = Color(0XFFA9AEB9),
                    strokeWidth = 1.dp.value
                )
            }
            for (i in statisByTime.indices) {
                val currentValue = statisByTime[i].value
                val heightOfBar = height * (currentValue.toFloat() / maxValueOfData)
                drawRect(
                    brush = if (currentValue == maxValueOfData) maxValueBarBrush else normalBarBrush,
                    topLeft = Offset(
                        x = sizeOfBar * i + sizeOfBar / 4 * 1,
                        y = height - heightOfBar
                    ),
                    size = Size(width = sizeOfBar / 2, height = heightOfBar)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            statisByTime.forEach {
                Text(
                    modifier = Modifier.weight(1f),
                    text = it.name,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    color = Color(0XFF525A6A)
                )
            }
        }
    }


}

@Preview
@Composable
fun StatisticsProjectTitlePreview() {
    StatisticsProjectTitle(title = "新增项目")
}