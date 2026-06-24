package com.duckylife.heritage.modern.feature.detail.intelligence

import com.duckylife.heritage.modern.core.data.ContentIntelligencePage
import com.duckylife.heritage.modern.core.data.ContentIntelligenceRef
import com.duckylife.heritage.modern.core.data.ContentIntelligenceRepository
import com.duckylife.heritage.modern.core.data.IntelligenceSection
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentDigestSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import java.net.UnknownHostException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContentIntelligenceViewModelDelegateTest {

    @Test
    fun loadPublishesSuccessState() = runTest {
        val repository = FakeContentIntelligenceRepository()
        val ref = ContentIntelligenceRef(SearchResultType.Article, "article-1")
        repository.given(ref, page(ref))
        val delegate = DefaultContentIntelligenceViewModelDelegate(this, repository)

        delegate.load(ref)
        advanceUntilIdle()

        val state = delegate.uiState.value
        assertEquals(false, state.isLoading)
        assertNull(state.loadError)
        assertEquals(ref, state.page?.ref)
        assertEquals(SectionStatus.Ready, state.aiSection.status)
        assertEquals(true, state.learningRoutesAvailable)
    }

    @Test
    fun loadSkipsDuplicateRef() = runTest {
        val repository = FakeContentIntelligenceRepository()
        val ref = ContentIntelligenceRef(SearchResultType.Article, "article-1")
        repository.given(ref, page(ref))
        val delegate = DefaultContentIntelligenceViewModelDelegate(this, repository)

        delegate.load(ref)
        advanceUntilIdle()
        delegate.load(ref)
        advanceUntilIdle()

        assertEquals(listOf(ref), repository.loads)
    }

    @Test
    fun retryRepeatsLastRef() = runTest {
        val repository = FakeContentIntelligenceRepository()
        val ref = ContentIntelligenceRef(SearchResultType.DirectoryItem, "item-1")
        repository.given(ref, page(ref))
        val delegate = DefaultContentIntelligenceViewModelDelegate(this, repository)

        delegate.load(ref)
        advanceUntilIdle()
        delegate.retry()
        advanceUntilIdle()

        assertEquals(listOf(ref, ref), repository.loads)
    }

    @Test
    fun switchesRefAndCancelsPreviousJob() = runTest {
        val repository = FakeContentIntelligenceRepository()
        val first = ContentIntelligenceRef(SearchResultType.Article, "first")
        val second = ContentIntelligenceRef(SearchResultType.Article, "second")
        repository.given(first, page(first))
        repository.given(second, page(second))
        repository.hangOn(first)
        val delegate = DefaultContentIntelligenceViewModelDelegate(this, repository)

        delegate.load(first)
        runCurrent()
        delegate.load(second)
        advanceUntilIdle()

        assertEquals(second, delegate.uiState.value.page?.ref)
        assertTrue(repository.cancelledRefs.contains(first))
    }

    @Test
    fun switchingRefImmediatelyClearsPreviousContentActions() = runTest {
        val repository = FakeContentIntelligenceRepository()
        val first = ContentIntelligenceRef(SearchResultType.Article, "first")
        val second = ContentIntelligenceRef(SearchResultType.Article, "second")
        repository.given(first, page(first))
        repository.given(second, page(second))
        val delegate = DefaultContentIntelligenceViewModelDelegate(this, repository)

        delegate.load(first)
        advanceUntilIdle()
        assertEquals(first, delegate.uiState.value.page?.ref)

        delegate.load(second)

        val loadingState = delegate.uiState.value
        assertTrue(loadingState.isLoading)
        assertNull(loadingState.page)
        assertFalse(loadingState.learningRoutesAvailable)
        assertEquals(SectionStatus.Disabled, loadingState.graphSection.status)
    }

    @Test
    fun badRequestSkipsIntelligenceLayer() = runTest {
        val repository = FakeContentIntelligenceRepository()
        val ref = ContentIntelligenceRef(SearchResultType.Article, "article-1")
        repository.given(ref, badRequestException())
        val delegate = DefaultContentIntelligenceViewModelDelegate(this, repository)

        delegate.load(ref)
        advanceUntilIdle()

        val state = delegate.uiState.value
        assertEquals(false, state.isLoading)
        assertNull(state.loadError)
        assertNull(state.page)
    }

    @Test
    fun serverErrorDegradesSectionsWithoutFatalError() = runTest {
        val repository = FakeContentIntelligenceRepository()
        val ref = ContentIntelligenceRef(SearchResultType.Inheritor, "inheritor-1")
        repository.given(ref, serviceUnavailableException())
        val delegate = DefaultContentIntelligenceViewModelDelegate(this, repository)

        delegate.load(ref)
        advanceUntilIdle()

        val state = delegate.uiState.value
        assertEquals(false, state.isLoading)
        assertNull(state.loadError)
        assertEquals(SectionStatus.Unavailable, state.aiSection.status)
        assertEquals(SectionStatus.Unavailable, state.graphSection.status)
        assertEquals(SectionStatus.Unavailable, state.recommendationSection.status)
        assertEquals(SectionStatus.Unavailable, state.digestSection.status)
    }

    @Test
    fun networkErrorDegradesSectionsWithoutFatalError() = runTest {
        val repository = FakeContentIntelligenceRepository()
        val ref = ContentIntelligenceRef(SearchResultType.DirectoryItem, "item-1")
        repository.given(ref, UnknownHostException("no network"))
        val delegate = DefaultContentIntelligenceViewModelDelegate(this, repository)

        delegate.load(ref)
        advanceUntilIdle()

        val state = delegate.uiState.value
        assertEquals(false, state.isLoading)
        assertNull(state.loadError)
        assertEquals(SectionStatus.Unavailable, state.aiSection.status)
    }
}

private fun page(ref: ContentIntelligenceRef): ContentIntelligencePage = ContentIntelligencePage(
    ref = ref,
    pageType = ref.type,
    aiSection = IntelligenceSection(SectionStatus.Ready, AiCardDto(hasAi = true, summary = "summary")),
    graphSection = IntelligenceSection(SectionStatus.Ready, GraphNeighborsDto()),
    recommendationSection = IntelligenceSection(SectionStatus.Ready, emptyList()),
    relatedContentSection = IntelligenceSection(SectionStatus.Ready, emptyList()),
    digestSection = IntelligenceSection(SectionStatus.Ready, ContentDigestSectionDto()),
    localState = null,
    sectionStatus = emptyMap(),
    warnings = emptyList(),
    learningRoutesAvailable = true,
)

private class FakeContentIntelligenceRepository : ContentIntelligenceRepository {
    val loads = mutableListOf<ContentIntelligenceRef>()
    val cancelledRefs = mutableListOf<ContentIntelligenceRef>()
    private val pages = mutableMapOf<ContentIntelligenceRef, ContentIntelligencePage>()
    private val failures = mutableMapOf<ContentIntelligenceRef, Throwable>()
    private val hangingRefs = mutableSetOf<ContentIntelligenceRef>()

    fun given(ref: ContentIntelligenceRef, page: ContentIntelligencePage) {
        pages[ref] = page
    }

    fun given(ref: ContentIntelligenceRef, throwable: Throwable) {
        failures[ref] = throwable
    }

    fun hangOn(ref: ContentIntelligenceRef) {
        hangingRefs += ref
    }

    override suspend fun loadContentPage(ref: ContentIntelligenceRef): ContentIntelligencePage {
        loads += ref
        if (ref in hangingRefs) {
            try {
                CompletableDeferred<Unit>().await()
            } catch (e: CancellationException) {
                cancelledRefs += ref
                throw e
            }
        }
        failures[ref]?.let { throw it }
        return pages.getValue(ref)
    }
}

private suspend fun badRequestException(): Throwable {
    val client = HttpClient(MockEngine { respondBadRequest() }) {
        expectSuccess = true
    }
    return runCatching { client.get("http://test.example/") }.exceptionOrNull()
        ?: error("expected exception")
}

private suspend fun serviceUnavailableException(): Throwable {
    val client = HttpClient(MockEngine { respondError(HttpStatusCode.ServiceUnavailable) }) {
        expectSuccess = true
    }
    return runCatching { client.get("http://test.example/") }.exceptionOrNull()
        ?: error("expected exception")
}
