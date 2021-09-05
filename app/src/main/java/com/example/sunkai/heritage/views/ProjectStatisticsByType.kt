package com.example.sunkai.heritage.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sunkai.heritage.entity.response.HeritageProjectStatisticsItem

val descViewColors = arrayOf(
        ProjectStatisticsDescribtionColorSet(0xFFA4E0D4, 0xFFF4FDFB, 0xFF00CB92, 0xFFA9AEB9),
        ProjectStatisticsDescribtionColorSet(0xFFD4E0A4, 0xFFFCFDF4, 0xFFCBAC00, 0xFFA9AEB9),
        ProjectStatisticsDescribtionColorSet(0xFFE0A4A4, 0xFFFDF4F4, 0xFFCB0000, 0xFFA9AEB9),
        ProjectStatisticsDescribtionColorSet(0xFFD892ED, 0xFFEFD4FA, 0xFFBC3AE3, 0xFFA9AEB9))

class ProjectStatisticsDescribtionColorSet(borderColor: Long, backgroundColor: Long, firstTextColor: Long, secondTextColor: Long) {
    val borderColor: Color
    val backgroundColor: Color
    val firstTextColor: Color
    val secondTextColor: Color

    init {
        this.borderColor = Color(borderColor)
        this.backgroundColor = Color(backgroundColor)
        this.firstTextColor = Color(firstTextColor)
        this.secondTextColor = Color(secondTextColor)
    }
}

@Composable
fun ProjectStatisticsByTypeView(statisByType: List<HeritageProjectStatisticsItem>) {
    val allValue = statisByType.sumOf { it.value }.toFloat()
    Card(elevation = 8.dp) {
        Column(modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)) {
            StatisticsProjectTitle(title = "项目类别")
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow {
                items(statisByType.size) { index ->
                    val it = statisByType[index]
                    ProjectStatiticsDescView(firstText = it.name, secondText = "${((it.value / allValue) * 100).toInt()}%", descViewColors[index % descViewColors.size])
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(26.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier
                        .width(210.dp)
                        .height(210.dp)) {
                    var startArc = 0f
                    statisByType.forEach {
                        for (i in descViewColors.indices) {
                            val currentArc = (it.value / allValue) * 360
                            drawArc(color = descViewColors[i].borderColor, startAngle = startArc, sweepAngle = currentArc, useCenter = true)
                            startArc += currentArc
                        }
                    }
                    drawCircle(Color.White, 130.dp.value)
                }
            }

        }
    }
}

@Composable
fun ProjectStatiticsDescView(firstText: String, secondText: String, colorSet: ProjectStatisticsDescribtionColorSet) {
    Box(modifier = Modifier
            .border(BorderStroke(1.dp, colorSet.borderColor), RoundedCornerShape(8.dp))
            .background(colorSet.backgroundColor, RoundedCornerShape(8.dp))) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .padding(20.dp))
        {
            Text(text = firstText, color = colorSet.firstTextColor, fontSize = 16.sp)
            Text(text = secondText, color = colorSet.secondTextColor, fontSize = 10.sp)
        }
    }

}

@Composable
@Preview
private fun ProjectStatiticsDescPreview() {
    ProjectStatiticsDescView("民间文学", "123", descViewColors[0])
}