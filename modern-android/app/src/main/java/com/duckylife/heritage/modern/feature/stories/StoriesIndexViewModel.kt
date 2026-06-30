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

                    val regionsResult = regionsDeferred.await()
                    val categoriesResult = categoriesDeferred.await()
                    val yearsResult = yearsDeferred.await()

                    val regions = regionsResult.getOrNull()?.items ?: emptyList()
                    val categories = categoriesResult.getOrNull()?.items ?: emptyList()
                    val years = yearsResult.getOrNull()?.take(10) ?: emptyList()

                    // Check if any API threw an exception
                    val firstError = regionsResult.exceptionOrNull()
                        ?: categoriesResult.exceptionOrNull()
                        ?: yearsResult.exceptionOrNull()

                    if (firstError != null && regions.isEmpty() && categories.isEmpty() && years.isEmpty()) {
                        // All failed
                        _uiState.update {
                            it.copy(isLoading = false, errorKind = firstError.toUiError().kind)
                        }
                    } else {
                        // Some or all succeeded (empty data is valid — not an error)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorKind = null,
                                regions = regions,
                                categories = categories,
                                years = years,
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
