package com.duckylife.heritage.modern.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.profile.LocalUserSyncRepository
import com.duckylife.heritage.modern.core.profile.ProfileLearningProgress
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import com.duckylife.heritage.modern.core.saved.SavedContentTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val savedContentRepository: SavedContentRepository,
    private val syncRepository: LocalUserSyncRepository,
) : ViewModel() {

    val favorites: StateFlow<List<SavedContentEntity>> =
        savedContentRepository.favorites().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val recentlyViewed: StateFlow<List<SavedContentEntity>> =
        savedContentRepository.recentlyViewed().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val profileState = syncRepository.profileState().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val pendingOperationCount = syncRepository.pendingOperationCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
    )

    val learningProgress = syncRepository.learningProgress().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun syncNow() {
        viewModelScope.launch {
            runCatching { syncRepository.syncNow() }
        }
    }

    fun unfavorite(entity: SavedContentEntity) {
        viewModelScope.launch {
            savedContentRepository.removeFavorite(entity.toTarget())
            val type = entity.toSyncType()
            val targetId = entity.targetId
            if (type != null && !targetId.isNullOrBlank()) {
                syncRepository.removeFavorite(type, targetId)
            }
        }
    }

    fun removeRecent(entity: SavedContentEntity) {
        viewModelScope.launch {
            savedContentRepository.removeRecent(entity.toTarget())
        }
    }

    fun clearRecent() {
        viewModelScope.launch {
            savedContentRepository.clearRecent()
        }
    }

    private fun SavedContentEntity.toTarget() = SavedContentTarget(
        id = targetId,
        sourceId = targetSourceId,
        sourceUrl = targetSourceUrl,
        category = targetCategory,
        kind = targetKind,
    )

    private fun SavedContentEntity.toSyncType(): String? = contentType.takeIf {
        it in setOf("article", "directoryItem", "inheritor")
    }
}
