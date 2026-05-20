import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.duckylife.heritage.modern"
    compileSdk = 36

    val apiBaseUrl = project.findProperty("heritageApiBaseUrl") as? String
        ?: "https://10.0.2.2:5078"

    defaultConfig {
        applicationId = "com.duckylife.heritage.modern"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "com.duckylife.heritage.modern.core.testing.HiltTestRunner"

        buildConfigField("String", "HERITAGE_API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            val trustSelfSigned = project.findProperty("heritageTrustSelfSigned") as? String ?: "true"
            buildConfigField("boolean", "HERITAGE_TRUST_SELF_SIGNED_CERTS", trustSelfSigned)
        }
        release {
            val trustSelfSigned = project.findProperty("heritageTrustSelfSigned") as? String ?: "false"
            buildConfigField("boolean", "HERITAGE_TRUST_SELF_SIGNED_CERTS", trustSelfSigned)
        }
    }

}

val homeDir = System.getProperty("user.home") ?: "~"

// 将 keystore 路径统一按 rootProject（modern-android/）解析。
// ~ 展开为用户 home；绝对路径原样返回；相对路径拼在 rootProject 目录下。
fun resolveReleaseStoreFile(rawPath: String?): File? {
    if (rawPath.isNullOrBlank()) return null
    val expanded = rawPath.replaceFirst("^~".toRegex(), homeDir)
    val f = File(expanded)
    return if (f.isAbsolute) f else rootProject.file(expanded)
}

val resolvedStoreFile = resolveReleaseStoreFile(
    project.findProperty("heritageReleaseStoreFile") as? String,
)
if (resolvedStoreFile != null) {
    android.signingConfigs.maybeCreate("release").apply {
        storeFile = resolvedStoreFile
        storePassword = project.findProperty("heritageReleaseStorePassword") as? String ?: ""
        keyAlias = project.findProperty("heritageReleaseKeyAlias") as? String ?: ""
        keyPassword = project.findProperty("heritageReleaseKeyPassword") as? String ?: ""
    }
    android.buildTypes.getByName("release").signingConfig = android.signingConfigs.getByName("release")
}

// 自定义任务：仅在 assembleRelease 调用时校验签名配置。
// @Input 属性可被 configuration cache 安全序列化。
abstract class ValidateReleaseSigningTask : DefaultTask() {
    @get:Input
    abstract val storeFilePath: Property<String>

    @get:Input
    abstract val storePassword: Property<String>

    @get:Input
    abstract val keyAlias: Property<String>

    @get:Input
    abstract val keyPassword: Property<String>

    @TaskAction
    fun validate() {
        val path = storeFilePath.get()
        if (path.isBlank() || !File(path).exists()) {
            val helpPath = path.ifBlank { "~/.gradle/heritage-release.jks" }
            throw GradleException(
                "Missing heritageReleaseStoreFile or keystore not found.\n" +
                    "  1. Run: keytool -genkey -v -keystore $helpPath -keyalg RSA -keysize 2048 -validity 10000 -alias heritage-release\n" +
                    "  2. Add to ~/.gradle/gradle.properties:\n" +
                    "    heritageReleaseStoreFile=~/.gradle/heritage-release.jks\n" +
                    "    heritageReleaseStorePassword=<your-store-password>\n" +
                    "    heritageReleaseKeyAlias=heritage-release\n" +
                    "    heritageReleaseKeyPassword=<your-key-password>",
            )
        }
        if (storePassword.get().isBlank()) throw GradleException("Missing heritageReleaseStorePassword. Set it in ~/.gradle/gradle.properties")
        if (keyAlias.get().isBlank()) throw GradleException("Missing heritageReleaseKeyAlias. Set it in ~/.gradle/gradle.properties")
        if (keyPassword.get().isBlank()) throw GradleException("Missing heritageReleaseKeyPassword. Set it in ~/.gradle/gradle.properties")
    }
}

// 校验和签名共用同一个 resolvedStoreFile，保证路径一致。
tasks.register<ValidateReleaseSigningTask>("validateReleaseSigning") {
    storeFilePath.set(resolvedStoreFile?.absolutePath ?: "")
    storePassword.set((project.findProperty("heritageReleaseStorePassword") as? String).orEmpty())
    keyAlias.set((project.findProperty("heritageReleaseKeyAlias") as? String).orEmpty())
    keyPassword.set((project.findProperty("heritageReleaseKeyPassword") as? String).orEmpty())
}

// packageRelease 依赖 validateReleaseSigning——校验仅在 release 构建时运行。
tasks.matching { it.name == "packageRelease" }.configureEach {
    dependsOn("validateReleaseSigning")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.room.runtime)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization.kotlinx.json)

    debugImplementation(libs.androidx.compose.ui.tooling)

    ksp(libs.hilt.compiler)
    ksp(libs.androidx.room.compiler)
    kspAndroidTest(libs.hilt.compiler)
    kspAndroidTest(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.mock)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.paging.testing)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.espresso.core)
}

// 本地快速验证：单元测试 + debug 构建，不依赖模拟器。
tasks.register("verifyLocal") {
    group = "verification"
    description = "Runs unit tests and assembles debug APK"
    dependsOn(":app:testDebugUnitTest", ":app:assembleDebug")
}
