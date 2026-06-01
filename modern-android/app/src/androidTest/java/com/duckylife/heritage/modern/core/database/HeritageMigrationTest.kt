package com.duckylife.heritage.modern.core.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

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
        assertTrue(db.isOpen)
        db.close()
    }

    @Test
    fun schemaJsonIsPresent() {
        val schemaDir = File("app/schemas/com.duckylife.heritage.modern.core.database.HeritageDatabase")
        assertTrue("Schema directory not found at $schemaDir", schemaDir.isDirectory)
        val schemaFile = File(schemaDir, "10.json")
        assertTrue("Schema v10 JSON not found at $schemaFile. Run assembleDebug to generate.", schemaFile.exists())
    }
}
