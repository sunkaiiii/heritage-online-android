package com.duckylife.heritage.modern.core.profile

import com.duckylife.heritage.modern.core.database.dao.PendingProfileOperationDao
import com.duckylife.heritage.modern.core.database.dao.ProfileFavoriteDao
import com.duckylife.heritage.modern.core.database.dao.ProfileHistoryDao
import com.duckylife.heritage.modern.core.database.dao.ProfileLearningProgressDao
import com.duckylife.heritage.modern.core.database.dao.ProfileStateDao
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalUserSyncRepositoryTest {

    private lateinit var favoriteDao: ProfileFavoriteDao
    private lateinit var historyDao: ProfileHistoryDao
    private lateinit var progressDao: ProfileLearningProgressDao
    private lateinit var stateDao: ProfileStateDao
    private lateinit var pendingDao: PendingProfileOperationDao
    private lateinit var api: FakeLocalUserApi
    private lateinit var scheduler: FakeProfileSyncScheduler
    private lateinit var profileRepository: FakeLocalProfileRepository

    @Before
    fun setup() {
        favoriteDao = FakeProfileFavoriteDao()
        historyDao = FakeProfileHistoryDao()
        progressDao = FakeProfileLearningProgressDao()
        stateDao = FakeProfileStateDao()
        pendingDao = FakePendingProfileOperationDao()
        api = FakeLocalUserApi()
        scheduler = FakeProfileSyncScheduler()
        profileRepository = FakeLocalProfileRepository("android_test_profile")
    }

    private fun createRepository(): LocalUserSyncRepository = DefaultLocalUserSyncRepository(
        api = api,
        profileRepository = profileRepository,
        stateDao = stateDao,
        favoriteDao = favoriteDao,
        historyDao = historyDao,
        progressDao = progressDao,
        pendingDao = pendingDao,
        scheduler = scheduler,
    )

    @Test
    fun toggleFavoriteThenRemove_endsWithNoPendingOp() = runTest {
        val repository = createRepository()

        repository.toggleFavorite("article", "a1", titleSnapshot = "Article")
        repository.removeFavorite("article", "a1")

        val pending = pendingDao.getAll()
        assertEquals(1, pending.size)
        assertEquals(PendingOperationKind.RemoveFavorite, pending[0].kind)
        assertEquals(0, favoriteDao.getByProfileId(profileRepository.currentProfileId()).size)
    }

    @Test
    fun syncNow_success_deletesPendingOps() = runTest {
        val repository = createRepository()
        repository.toggleFavorite("article", "a1", titleSnapshot = "Article")

        api.addFavoriteResult = com.duckylife.heritage.modern.core.network.dto.advanced.LocalFavoriteDto(
            id = "remote:a1",
            targetType = "article",
            targetId = "a1",
            titleSnapshot = "Article",
        )
        api.favorites = listOf(api.addFavoriteResult!!)

        repository.syncNow()

        assertTrue(pendingDao.getAll().isEmpty())
        val favorites = favoriteDao.getByProfileId(profileRepository.currentProfileId())
        assertEquals(1, favorites.size)
        assertEquals(ProfileSyncStatus.Synced, favorites[0].syncStatus)
    }

    @Test
    fun syncNow_503_isRetryable_andKeepsPendingOp() = runTest {
        val repository = createRepository()
        repository.toggleFavorite("article", "a1", titleSnapshot = "Article")
        api.addFavoriteFailure = FakeLocalUserApi.createResponseException(HttpStatusCode.ServiceUnavailable)

        val result = runCatching { repository.syncNow() }

        assertTrue("expected retryable exception", result.exceptionOrNull() is ProfileSyncException)
        val error = result.exceptionOrNull() as ProfileSyncException
        assertTrue(error.isRetryable)

        val pending = pendingDao.getAll()
        assertEquals(1, pending.size)
        assertEquals(PendingOperationKind.AddFavorite, pending[0].kind)
        assertEquals(1, pending[0].attemptCount)
    }

    @Test
    fun syncNow_400_terminal_dropsPendingOp() = runTest {
        val repository = createRepository()
        repository.toggleFavorite("article", "a1", titleSnapshot = "Article")
        api.addFavoriteFailure = FakeLocalUserApi.createResponseException(HttpStatusCode.BadRequest)

        repository.syncNow()

        assertTrue(pendingDao.getAll().isEmpty())
        assertTrue(favoriteDao.getByProfileId(profileRepository.currentProfileId()).isEmpty())
        val state = stateDao.getByProfileId(profileRepository.currentProfileId())
        assertTrue(!state?.lastSyncError.isNullOrBlank())
    }
}
