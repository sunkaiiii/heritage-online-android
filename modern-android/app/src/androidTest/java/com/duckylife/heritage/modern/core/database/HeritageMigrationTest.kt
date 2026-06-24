package com.duckylife.heritage.modern.core.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.duckylife.heritage.modern.core.database.entity.PendingProfileOperationEntity
import com.duckylife.heritage.modern.core.profile.PendingOperationKind
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeritageMigrationTest {

    private val testDbName = "heritage-migration-test.db"

    @get:Rule
    val migrationHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        HeritageDatabase::class.java,
    )

    @Test
    fun migrate7To8CreatesBannerTable() {
        // 从 v7 创建库，然后应用 7→8 迁移。
        val dbV7 = migrationHelper.createDatabase(testDbName, 7)
        dbV7.close()

        val dbV8 = migrationHelper.runMigrationsAndValidate(
            testDbName, 8, true, HeritageMigrations.MIGRATION_7_8,
        )
        assertTrue(dbV8.isOpen)
        dbV8.close()
    }

    @Test
    fun migrate8To9CreatesReadingPathTable() {
        // 从 v8 创建库，然后应用 8→9 迁移。
        val dbV8 = migrationHelper.createDatabase(testDbName, 8)
        dbV8.close()

        val dbV9 = migrationHelper.runMigrationsAndValidate(
            testDbName, 9, true, HeritageMigrations.MIGRATION_8_9,
        )
        assertTrue(dbV9.isOpen)
        // 验证 reading_path_events 表存在
        val cursor = dbV9.query("SELECT name FROM sqlite_master WHERE type='table' AND name='reading_path_events'")
        assertTrue("reading_path_events table should exist", cursor.moveToFirst())
        cursor.close()
        dbV9.close()
    }

    @Test
    fun migrate9To10AddsReadingPathMetadata() {
        // 从 v9 创建库，然后应用 9→10 迁移。
        val dbV9 = migrationHelper.createDatabase(testDbName, 9)
        dbV9.close()

        val dbV10 = migrationHelper.runMigrationsAndValidate(
            testDbName, 10, true, HeritageMigrations.MIGRATION_9_10,
        )
        assertTrue(dbV10.isOpen)
        // 验证新增字段存在
        val cursor = dbV10.query("PRAGMA table_info(reading_path_events)")
        val columnNames = mutableListOf<String>()
        while (cursor.moveToNext()) {
            columnNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
        }
        cursor.close()
        assertTrue("toCategory column should exist", columnNames.contains("toCategory"))
        assertTrue("toKind column should exist", columnNames.contains("toKind"))
        assertTrue("toSourceId column should exist", columnNames.contains("toSourceId"))
        assertTrue("toSourceUrl column should exist", columnNames.contains("toSourceUrl"))
        assertTrue("toSubtitle column should exist", columnNames.contains("toSubtitle"))
        assertTrue("toImageUrl column should exist", columnNames.contains("toImageUrl"))
        dbV10.close()
    }

    @Test
    fun openAtV10WorksDirectly() {
        val db = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HeritageDatabase::class.java,
            testDbName,
        )
            .addMigrations(*HeritageMigrations.ALL)
            .build()
        // 强制打开底层连接，isOpen 才真正为 true。
        val openedDb = db.openHelper.writableDatabase
        assertTrue(openedDb.isOpen)
        db.close()
    }

    @Test
    fun migrate10To11CreatesProfileSyncTables() {
        val dbV10 = migrationHelper.createDatabase(testDbName, 10)
        dbV10.close()

        val dbV11 = migrationHelper.runMigrationsAndValidate(
            testDbName, 11, true, HeritageMigrations.MIGRATION_10_11,
        )
        assertTrue(dbV11.isOpen)

        val tables = listOf(
            "local_profile_state",
            "profile_favorites",
            "profile_history",
            "profile_learning_progress",
            "pending_profile_operations",
        )
        for (table in tables) {
            val cursor = dbV11.query(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='$table'",
            )
            assertTrue("$table table should exist", cursor.moveToFirst())
            cursor.close()
        }

        val indexCursor = dbV11.query(
            "SELECT name FROM sqlite_master WHERE type='index' AND name='index_profile_favorites_profileId_targetType_targetId'",
        )
        assertTrue("unique index on favorites should exist", indexCursor.moveToFirst())
        indexCursor.close()

        dbV11.close()
    }

    @Test
    fun migrate10To11PreservesSavedContent() {
        val dbV10 = migrationHelper.createDatabase(testDbName, 10)
        dbV10.execSQL(
            """
            INSERT INTO saved_content (
                contentKey, contentType, title, summary, coverImageJson,
                category, region, year, sourceUrl, targetId, targetSourceId,
                targetSourceUrl, targetCategory, targetKind, isFavorite,
                favoritedAt, lastViewedAt
            ) VALUES (
                'article:a1', 'article', 'Legacy Article', 'summary', NULL,
                'news', 'Zhejiang', 2024, 'https://example.test', 'a1', NULL,
                NULL, NULL, NULL, 1, 1719200000000, 1719200000000
            )
            """.trimIndent(),
        )
        dbV10.close()

        val dbV11 = migrationHelper.runMigrationsAndValidate(
            testDbName, 11, true, HeritageMigrations.MIGRATION_10_11,
        )

        val cursor = dbV11.query("SELECT * FROM saved_content WHERE contentKey = 'article:a1'")
        assertTrue("saved_content row should survive migration", cursor.moveToFirst())
        val titleIndex = cursor.getColumnIndexOrThrow("title")
        assertEquals("Legacy Article", cursor.getString(titleIndex))
        cursor.close()
        dbV11.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pendingProfileOperationReplaceKeepsOnlyLastIntentForDeduplicationKey() = runTest {
        val db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HeritageDatabase::class.java,
        ).build()
        try {
            val dao = db.pendingProfileOperationDao()
            val deduplicationKey = "favorite:article:a1"
            dao.replace(
                PendingProfileOperationEntity(
                    operationId = UUID.randomUUID().toString(),
                    kind = PendingOperationKind.AddFavorite,
                    deduplicationKey = deduplicationKey,
                    payloadJson = "{\"targetType\":\"article\",\"targetId\":\"a1\"}",
                    createdAt = 1L,
                ),
            )
            dao.replace(
                PendingProfileOperationEntity(
                    operationId = UUID.randomUUID().toString(),
                    kind = PendingOperationKind.RemoveFavorite,
                    deduplicationKey = deduplicationKey,
                    payloadJson = "{\"targetType\":\"article\",\"targetId\":\"a1\"}",
                    createdAt = 2L,
                ),
            )

            val operations = dao.getAll()
            assertEquals(1, operations.size)
            assertEquals(PendingOperationKind.RemoveFavorite, operations.single().kind)
        } finally {
            db.close()
        }
    }

}
