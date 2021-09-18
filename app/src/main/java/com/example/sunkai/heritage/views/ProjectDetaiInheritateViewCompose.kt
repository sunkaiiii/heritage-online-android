package com.example.sunkai.heritage.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.response.nestedData.InheritatePeople
import com.example.sunkai.heritage.tools.getString

@Composable
fun ProjectDetaiInheritateViewCompose(
    projectName: String,
    datas: List<InheritatePeople>,
    modifier: Modifier = Modifier,
    onPeopleClick: ((String) -> Unit)? = null
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = getString(R.string.activity_project_detail_relative_inheritate),
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(datas.size) { i ->
                val data = datas[i]
                Column(Modifier.clickable {
                    onPeopleClick?.invoke(data.link)
                }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        modifier = Modifier.size(62.dp),
                        contentDescription = getString(R.string.user)
                    )
                    Text(
                        text = data.content.first { it.key.contains("姓名") }.value,
                        color = Color(0xFF32313C),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

}