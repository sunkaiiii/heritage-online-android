package com.duckylife.heritage.modern.feature.taxonomy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaxonomyViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaxonomyUiState())
    val uiState: StateFlow<TaxonomyUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }

            val categoriesDeferred = async {
                runCatchingCancellable { repository.taxonomyCategories() }
            }
            val regionsDeferred = async {
                runCatchingCancellable { repository.taxonomyRegions() }
            }
            val kindsDeferred = async {
                runCatchingCancellable { repository.taxonomyKinds() }
            }

            val categoriesResult = categoriesDeferred.await()
            val regionsResult = regionsDeferred.await()
            val kindsResult = kindsDeferred.await()

            val failure = categoriesResult.exceptionOrNull()
                ?: regionsResult.exceptionOrNull()
                ?: kindsResult.exceptionOrNull()

            if (failure != null) {
                _uiState.update {
                    it.copy(isLoading = false, errorKind = failure.toUiError().kind)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = categoriesResult.getOrThrow().items,
                        regions = regionsResult.getOrThrow().items,
                        kinds = kindsResult.getOrThrow().items,
                    )
                }
            }
        }
    }
}
