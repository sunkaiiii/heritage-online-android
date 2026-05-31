package com.duckylife.heritage.modern.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DigestCard(
    digest: ContentDigestDto,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    HeritageContentCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // 标题行
            Text(
                text = stringResource(R.string.digest_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            // quickRead 正文
            if (!digest.quickRead.isNullOrBlank()) {
                Text(
                    text = digest.quickRead,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // 阅读时间
            if (digest.readingTimeMinutes > 0) {
                Text(
                    text = stringResource(R.string.digest_reading_time, digest.readingTimeMinutes),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            // 要点
            if (digest.highlights.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.digest_highlights),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                val displayHighlights = if (expanded) {
                    digest.highlights
                } else {
                    digest.highlights.take(3)
                }
                displayHighlights.forEach { highlight ->
                    Text(
                        text = "• $highlight",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (digest.highlights.size > 3) {
                    TextButton(onClick = { expanded = !expanded }) {
                        Text(
                            text = stringResource(
                                if (expanded) R.string.digest_show_less else R.string.digest_show_more,
                            ),
                        )
                    }
                }
            }

            // 关键信息
            if (digest.keyFacts.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.digest_key_facts),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    digest.keyFacts.forEach { fact ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = "${fact.label}: ${fact.value}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                        )
                    }
                }
            }

            // 关键词
            if (digest.keywords.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.digest_keywords),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    digest.keywords.forEach { keyword ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = keyword,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
