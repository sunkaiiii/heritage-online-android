package com.duckylife.heritage.modern.feature.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StoriesIndexViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoriesIndexUiState())
    val uiState: StateFlow<StoriesIndexUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            try {
                coroutineScope {
                    val regionsDeferred = async {
                        runCatchingCancellable { repository.taxonomyRegions(limit = 12) }
                    }
                    val categoriesDeferred = async {
                        runCatchingCancellable { repository.taxonomyCategories(limit = 12) }
                    }
                    val yearsDeferred = async {
                        runCatchingCancellable { repository.timelineYears() }
                    }

                    val regions = regionsDeferred.await().getOrNull()?.items ?: emptyList()
                    val categories = categoriesDeferred.await().getOrNull()?.items ?: emptyList()
                    val years = yearsDeferred.await().getOrNull()?.take(10) ?: emptyList()

                    val hasAnyData = regions.isNotEmpty() || categories.isNotEmpty() || years.isNotEmpty()

                    if (hasAnyData) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorKind = null,
                                regions = regions,
                                categories = categories,
                                years = years,
                            )
                        }
                    } else {
                        val firstError = regionsDeferred.await().exceptionOrNull()
                            ?: categoriesDeferred.await().exceptionOrNull()
                            ?: yearsDeferred.await().exceptionOrNull()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorKind = (firstError ?: Exception("Unknown error")).toUiError().kind,
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorKind = e.toUiError().kind)
                }
            }
        }
    }
}
