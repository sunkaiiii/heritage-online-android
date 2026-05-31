package com.duckylife.heritage.modern.feature.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = StoryViewModel.Factory::class)
class StoryViewModel @AssistedInject constructor(
    @Assisted("region") private val region: String?,
    @Assisted("category") private val category: String?,
    @Assisted("year") private val year: Int?,
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoryUiState())
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatchingCancellable {
                when {
                    region != null -> repository.regionStory(region)
                    category != null -> repository.categoryStory(category)
                    year != null -> repository.yearStory(year)
                    else -> error("At least one of region, category, or year must be provided")
                }
            }.onSuccess { story ->
                _uiState.update { it.copy(isLoading = false, story = story) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorKind = e.toUiError().kind) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("region") region: String?,
            @Assisted("category") category: String?,
            @Assisted("year") year: Int?,
        ): StoryViewModel
    }
}
