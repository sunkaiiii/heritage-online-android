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

    val ALL: Array<Migration> = arrayOf(MIGRATION_7_8)
}
