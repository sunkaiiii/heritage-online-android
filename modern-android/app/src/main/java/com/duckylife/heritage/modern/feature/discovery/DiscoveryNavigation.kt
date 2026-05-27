package com.duckylife.heritage.modern.feature.discovery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun DiscoveryRoute(
    modifier: Modifier = Modifier,
) {
    DiscoveryRoute(
        modifier = modifier,
        viewModel = hiltViewModel(),
    )
}
