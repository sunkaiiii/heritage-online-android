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
    fun openAtV8WorksDirectly() {
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
        val schemaFile = File(schemaDir, "8.json")
        assertTrue("Schema v8 JSON not found at $schemaFile. Run assembleDebug to generate.", schemaFile.exists())
    }
}
