package com.duckylife.heritage.modern.feature.research

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.ResearchRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.feature.research.model.ResearchArtifactUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageDetailUiModel
import com.duckylife.heritage.modern.ui.error.toUiError
import com.duckylife.heritage.modern.ui.state.AsyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MAX_INLINE_ARTIFACT_BYTES = 1_000_000

sealed interface SharePayload {
    data class Text(val content: String) : SharePayload
    data class File(val file: java.io.File, val mimeType: String) : SharePayload
}

private const val KEY_PACKAGE_ID = "research_package_id"

/**
 * 资料包详情 UI 状态。
 */
data class ResearchPackageUiState(
    val packageId: String = "",
    val detail: AsyncState<ResearchPackageDetailUiModel> = AsyncState(),
    val artifactContent: AsyncState<String> = AsyncState(),
)

@HiltViewModel
class ResearchPackageViewModel @Inject constructor(
    private val repository: ResearchRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreUiState())
    val uiState: StateFlow<ResearchPackageUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var reportJob: Job? = null
    private var artifactJob: Job? = null
    private var shareJob: Job? = null

    private val _shareEvent = Channel<SharePayload>(Channel.BUFFERED)
    val shareEvent: Flow<SharePayload> = _shareEvent.receiveAsFlow()

    init {
        if (_uiState.value.packageId.isNotBlank()) {
            loadDetail()
        }
    }

    fun setPackageId(packageId: String) {
        if (packageId.isBlank()) return
        if (_uiState.value.packageId == packageId) return
        _uiState.update { ResearchPackageUiState(packageId = packageId) }
        savedStateHandle[KEY_PACKAGE_ID] = packageId
        loadDetail()
    }

    fun loadDetail() {
        val currentId = _uiState.value.packageId
        if (currentId.isBlank()) return
        loadJob?.cancel()
        reportJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(detail = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getPackageDetail(currentId) }
                .onSuccess { detail ->
                    if (_uiState.value.packageId == currentId) {
                        _uiState.update { it.copy(detail = AsyncState(data = detail)) }
                        if (detail.status == ResearchTaskStatus.Succeeded) {
                            checkReport(currentId)
                        }
                    }
                }
                .onFailure { throwable ->
                    if (_uiState.value.packageId == currentId) {
                        _uiState.update { it.copy(detail = AsyncState(errorKind = throwable.toUiError().kind)) }
                    }
                }
        }
    }

    fun loadArtifact(artifactName: String) {
        val currentId = _uiState.value.packageId
        if (currentId.isBlank()) return
        artifactJob?.cancel()
        artifactJob = viewModelScope.launch {
            _uiState.update { it.copy(artifactContent = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getArtifactContent(currentId, artifactName) }
                .onSuccess { content ->
                    if (_uiState.value.packageId == currentId) {
                        _uiState.update { it.copy(artifactContent = AsyncState(data = content)) }
                    }
                }
                .onFailure { throwable ->
                    if (_uiState.value.packageId == currentId) {
                        _uiState.update { it.copy(artifactContent = AsyncState(errorKind = throwable.toUiError().kind)) }
                    }
                }
        }
    }

    fun clearArtifactContent() {
        _uiState.update { it.copy(artifactContent = AsyncState()) }
    }

    fun shareArtifact(artifact: ResearchArtifactUiModel) {
        val currentId = _uiState.value.packageId
        if (currentId.isBlank()) return
        shareJob?.cancel()
        shareJob = viewModelScope.launch {
            val useTextPath = artifact.isTextLike && artifact.sizeBytes < MAX_INLINE_ARTIFACT_BYTES
            runCatchingCancellable {
                if (useTextPath) {
                    val content = repository.getArtifactContent(currentId, artifact.name)
                    SharePayload.Text(content)
                } else {
                    val bytes = repository.getArtifactBytes(currentId, artifact.name)
                    val file = repository.saveArtifactToCache(currentId, artifact.name, bytes)
                    SharePayload.File(
                        file = file,
                        mimeType = artifact.mimeType.takeIf { it.isNotBlank() } ?: "application/octet-stream",
                    )
                }
            }
                .onSuccess { payload ->
                    if (_uiState.value.packageId == currentId) {
                        _shareEvent.trySend(payload)
                    }
                }
                .onFailure { throwable ->
                    if (_uiState.value.packageId == currentId) {
                        _uiState.update { it.copy(artifactContent = AsyncState(errorKind = throwable.toUiError().kind)) }
                    }
                }
        }
    }

    fun retry() {
        loadDetail()
    }

    private fun checkReport(packageId: String) {
        reportJob?.cancel()
        reportJob = viewModelScope.launch {
            runCatchingCancellable { repository.getReportByPackage(packageId) }
                .onSuccess { report ->
                    if (_uiState.value.packageId == packageId && report.reportId.isNotBlank()) {
                        _uiState.update { state ->
                            state.copy(
                                detail = state.detail.copy(
                                    data = state.detail.data?.copy(
                                        hasReport = true,
                                        reportId = report.reportId,
                                    ),
                                ),
                            )
                        }
                    }
                }
                .onFailure {
                    if (_uiState.value.packageId == packageId) {
                        _uiState.update { state ->
                            state.copy(
                                detail = state.detail.copy(
                                    data = state.detail.data?.copy(
                                        hasReport = false,
                                        reportId = null,
                                    ),
                                ),
                            )
                        }
                    }
                }
        }
    }

    private fun restoreUiState(): ResearchPackageUiState {
        val packageId = savedStateHandle.get<String>(KEY_PACKAGE_ID) ?: ""
        return ResearchPackageUiState(packageId = packageId)
    }
}
