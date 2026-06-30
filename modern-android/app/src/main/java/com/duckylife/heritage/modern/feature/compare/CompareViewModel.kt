package com.duckylife.heritage.modern.feature.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.CompareType
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private fun directoryItemKindFromInput(input: String): DirectoryItemKind? =
    DirectoryItemKind.entries.firstOrNull {
        it.wireName.equals(input, ignoreCase = true) || it.name.equals(input, ignoreCase = true)
    }

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    fun updateType(type: CompareType) {
        _uiState.update { it.copy(selectedType = type, result = null, errorKind = null, errorMessage = null) }
    }

    fun updateLeft(value: String) {
        _uiState.update { it.copy(leftInput = value) }
    }

    fun updateRight(value: String) {
        _uiState.update { it.copy(rightInput = value) }
    }

    fun compare() {
        val state = _uiState.value
        val left = state.leftInput.trim()
        val right = state.rightInput.trim()

        if (left.isBlank() || right.isBlank()) {
            _uiState.update { it.copy(errorMessage = "empty") }
            return
        }
        if (left.equals(right, ignoreCase = true)) {
            _uiState.update { it.copy(errorMessage = "same") }
            return
        }

        // Validate kind inputs
        if (state.selectedType == CompareType.Kind) {
            val leftKind = directoryItemKindFromInput(left)
            val rightKind = directoryItemKindFromInput(right)
            if (leftKind == null || rightKind == null) {
                _uiState.update { it.copy(errorMessage = "invalid_kind") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null, errorMessage = null, result = null) }
            runCatchingCancellable {
                when (state.selectedType) {
                    CompareType.Region -> repository.compareRegions(left, right)
                    CompareType.Category -> repository.compareCategories(left, right)
                    CompareType.Kind -> {
                        val leftKind = directoryItemKindFromInput(left)!!
                        val rightKind = directoryItemKindFromInput(right)!!
                        repository.compareKinds(leftKind, rightKind)
                    }
                }
            }.onSuccess { result ->
                _uiState.update { it.copy(isLoading = false, result = result, errorKind = null) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorKind = e.toUiError().kind) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, errorKind = null) }
    }
}
