package com.duckylife.heritage.modern.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.ReadingPathEvent
import com.duckylife.heritage.modern.core.data.ReadingPathRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingPathViewModel @Inject constructor(
    private val readingPathRepository: ReadingPathRepository,
) : ViewModel() {

    val events: StateFlow<List<ReadingPathEvent>> =
        readingPathRepository.observeRecentPath(limit = 50).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun clearAll() {
        viewModelScope.launch {
            readingPathRepository.clear()
        }
    }
}
