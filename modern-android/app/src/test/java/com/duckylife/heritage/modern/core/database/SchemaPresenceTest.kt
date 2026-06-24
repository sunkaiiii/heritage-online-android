package com.duckylife.heritage.modern.core.database

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 验证 Room exportSchema 正确导出了当前版本的 schema JSON。
 *
 * 该测试在 JVM 单元测试环境中运行，避免在 instrumentation 测试中检查本地文件路径失效。
 */
class SchemaPresenceTest {

    private val schemaDir: File by lazy {
        val schemaRoot = System.getProperty("room.schema.dir")
            ?: error("Missing system property 'room.schema.dir'. Make sure build.gradle.kts sets it for unit tests.")
        File(schemaRoot, "com.duckylife.heritage.modern.core.database.HeritageDatabase")
    }

    @Test
    fun schemaDirectoryExists() {
        assertTrue(
            "Schema directory should exist at $schemaDir. Run ./gradlew :app:assembleDebug to generate.",
            schemaDir.isDirectory,
        )
    }

    @Test
    fun schemaV11JsonExists() {
        val schemaFile = File(schemaDir, "11.json")
        assertTrue(
            "Schema v11 JSON should exist at $schemaFile. Run ./gradlew :app:assembleDebug to generate.",
            schemaFile.exists(),
        )
    }
}
