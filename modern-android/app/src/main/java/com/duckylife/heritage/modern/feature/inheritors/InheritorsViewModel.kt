package com.duckylife.heritage.modern.feature.inheritors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class InheritorsViewModel @Inject constructor(
    repository: HeritageRepository,
) : ViewModel() {
    val inheritors: Flow<PagingData<InheritorSummaryDto>> =
        repository.pagedInheritors(
            InheritorQuery(
                page = 1,
                pageSize = 20,
            ),
        ).cachedIn(viewModelScope)
}
