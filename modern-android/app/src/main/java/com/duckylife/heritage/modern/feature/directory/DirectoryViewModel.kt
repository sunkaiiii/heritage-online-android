package com.duckylife.heritage.modern.feature.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DirectoryUiState())
    val uiState: StateFlow<DirectoryUiState> = _uiState.asStateFlow()

    val items: Flow<PagingData<DirectoryItemSummaryDto>> =
        uiState
            .map { state ->
                DirectoryItemQuery(
                    kind = state.selectedKind,
                    page = 1,
                    pageSize = 20,
                    keywords = state.searchKeywords.trim().takeIf { it.isNotEmpty() },
                )
            }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                repository.pagedDirectoryItems(query)
            }
            .cachedIn(viewModelScope)

    fun selectKind(kind: DirectoryItemKind) {
        _uiState.update { it.copy(selectedKind = kind) }
    }

    fun updateSearchKeywords(keywords: String) {
        _uiState.update { it.copy(searchKeywords = keywords) }
    }
}
