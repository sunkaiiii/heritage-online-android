package com.duckylife.heritage.modern.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto

@Composable
fun DiscoveryItemCard(
    item: DiscoveryItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier.width(200.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!item.summary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                if (item.type.isNotBlank()) {
                    HeritageMetaChip(text = item.type)
                }
                if (!item.category.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    HeritageMetaChip(text = item.category)
                }
                if (!item.region.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    HeritageMetaChip(text = item.region)
                }
            }
        }
    }
}

@Composable
fun DiscoveryItemRow(
    item: DiscoveryItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!item.summary.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    if (item.type.isNotBlank()) {
                        HeritageMetaChip(text = item.type)
                    }
                    if (!item.category.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        HeritageMetaChip(text = item.category)
                    }
                    if (!item.region.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        HeritageMetaChip(text = item.region)
                    }
                }
            }
        }
    }
}
