package com.duckylife.heritage.modern.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// 数据库迁移注册。当前版本 7→8 为 Banner 缓存。
// 以后 Entity 字段变化时必须：
// 1. 升版本号
// 2. 在这里补对应的 Migration
// 3. 跑 MigrationTest 确认迁移正确
object HeritageMigrations {
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS home_banners (
                    id TEXT NOT NULL PRIMARY KEY,
                    sortOrder INTEGER NOT NULL,
                    targetUrl TEXT,
                    displayImageJson TEXT,
                    mobileImageJson TEXT,
                    desktopImageJson TEXT,
                    updatedAtEpochMillis INTEGER NOT NULL
                )
                """.trimIndent(),
            )
        }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS reading_path_events (
                    id TEXT NOT NULL PRIMARY KEY,
                    fromType TEXT,
                    fromId TEXT,
                    fromTitle TEXT,
                    toType TEXT NOT NULL,
                    toId TEXT NOT NULL,
                    toTitle TEXT,
                    source TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent(),
            )
        }
    }

    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE reading_path_events ADD COLUMN toCategory TEXT")
            db.execSQL("ALTER TABLE reading_path_events ADD COLUMN toKind TEXT")
            db.execSQL("ALTER TABLE reading_path_events ADD COLUMN toSourceId TEXT")
            db.execSQL("ALTER TABLE reading_path_events ADD COLUMN toSourceUrl TEXT")
            db.execSQL("ALTER TABLE reading_path_events ADD COLUMN toSubtitle TEXT")
            db.execSQL("ALTER TABLE reading_path_events ADD COLUMN toImageUrl TEXT")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_reading_path_events_createdAt ON reading_path_events(createdAt)")
        }
    }

    val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS local_profile_state (
                    profileId TEXT NOT NULL PRIMARY KEY,
                    displayName TEXT NOT NULL,
                    favoriteCount INTEGER NOT NULL,
                    historyCount INTEGER NOT NULL,
                    learningRouteCount INTEGER NOT NULL,
                    generatedAt TEXT,
                    lastSyncAt INTEGER,
                    lastSyncError TEXT
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS profile_favorites (
                    id TEXT NOT NULL PRIMARY KEY,
                    profileId TEXT NOT NULL,
                    targetType TEXT NOT NULL,
                    targetId TEXT NOT NULL,
                    titleSnapshot TEXT,
                    coverImageUrlSnapshot TEXT,
                    tags TEXT NOT NULL,
                    note TEXT,
                    createdAt TEXT,
                    updatedAt TEXT,
                    syncStatus TEXT NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_profile_favorites_profileId_targetType_targetId
                ON profile_favorites(profileId, targetType, targetId)
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_profile_favorites_updatedAt
                ON profile_favorites(updatedAt)
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS profile_history (
                    id TEXT NOT NULL PRIMARY KEY,
                    profileId TEXT NOT NULL,
                    targetType TEXT NOT NULL,
                    targetId TEXT NOT NULL,
                    titleSnapshot TEXT,
                    viewedAt TEXT,
                    viewCount INTEGER NOT NULL,
                    lastPosition TEXT,
                    syncStatus TEXT NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_profile_history_profileId_targetType_targetId
                ON profile_history(profileId, targetType, targetId)
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_profile_history_viewedAt
                ON profile_history(viewedAt)
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS profile_learning_progress (
                    id TEXT NOT NULL PRIMARY KEY,
                    profileId TEXT NOT NULL,
                    routeId TEXT NOT NULL,
                    routeTitle TEXT,
                    completedStepIds TEXT NOT NULL,
                    currentStepId TEXT,
                    percent INTEGER NOT NULL,
                    startedAt TEXT,
                    updatedAt TEXT,
                    completedAt TEXT,
                    syncStatus TEXT NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_profile_learning_progress_profileId_routeId
                ON profile_learning_progress(profileId, routeId)
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_profile_learning_progress_updatedAt
                ON profile_learning_progress(updatedAt)
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS pending_profile_operations (
                    operationId TEXT NOT NULL PRIMARY KEY,
                    kind TEXT NOT NULL,
                    deduplicationKey TEXT NOT NULL,
                    payloadJson TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    attemptCount INTEGER NOT NULL,
                    lastError TEXT
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_pending_profile_operations_deduplicationKey
                ON pending_profile_operations(deduplicationKey)
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_pending_profile_operations_createdAt
                ON pending_profile_operations(createdAt)
                """.trimIndent(),
            )
        }
    }

    val ALL: Array<Migration> = arrayOf(
        MIGRATION_7_8,
        MIGRATION_8_9,
        MIGRATION_9_10,
        MIGRATION_10_11,
    )
}
