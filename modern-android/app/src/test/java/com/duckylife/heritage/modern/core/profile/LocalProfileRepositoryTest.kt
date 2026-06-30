package com.duckylife.heritage.modern.core.profile

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class LocalProfileRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun TestScope.createRepository(): LocalProfileRepository {
        val dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { File(tempFolder.root, "profile.preferences_pb") },
        )
        return DataStoreLocalProfileRepository(dataStore)
    }

    @Test
    fun `first call generates and persists profile id`() = runTest {
        val repository = createRepository()

        val id = repository.currentProfileId()

        assertTrue(id.startsWith("android_"))
        assertTrue(id.isValidProfileId())
        assertEquals(id, repository.currentProfileId())
    }

    @Test
    fun `profile id flow emits same persisted id`() = runTest {
        val repository = createRepository()

        val id = repository.currentProfileId()

        assertEquals(id, repository.profileId.first())
    }

    @Test
    fun `recreated repository reads existing profile id`() = runTest {
        val dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { File(tempFolder.root, "profile.preferences_pb") },
        )
        val firstRepository = DataStoreLocalProfileRepository(dataStore)
        val id = firstRepository.currentProfileId()

        val secondRepository = DataStoreLocalProfileRepository(dataStore)
        assertEquals(id, secondRepository.currentProfileId())
    }
}
