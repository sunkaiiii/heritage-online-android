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
val rawPath = project.findProperty("heritageReleaseStoreFile") as? String
if (rawPath != null) {
    val expandedPath = rawPath.replaceFirst("^~".toRegex(), homeDir)
    val storeFile = file(expandedPath)
    android.signingConfigs.maybeCreate("release").apply {
        this.storeFile = storeFile
        storePassword = project.findProperty("heritageReleaseStorePassword") as? String ?: ""
        keyAlias = project.findProperty("heritageReleaseKeyAlias") as? String ?: ""
        keyPassword = project.findProperty("heritageReleaseKeyPassword") as? String ?: ""
    }
    android.buildTypes.getByName("release").signingConfig = android.signingConfigs.getByName("release")
}

// 仅在执行 assembleRelease 时校验签名配置完整性，debug 不受影响。
val relStoreFilePath = (project.findProperty("heritageReleaseStoreFile") as? String)?.replaceFirst("^~".toRegex(), homeDir)
val relStoreFile = relStoreFilePath?.let { file(it) }
val relStorePwd = project.findProperty("heritageReleaseStorePassword") as? String
val relKeyAlias = project.findProperty("heritageReleaseKeyAlias") as? String
val relKeyPwd = project.findProperty("heritageReleaseKeyPassword") as? String

tasks.whenTaskAdded {
    if (name == "packageRelease") {
        doFirst {
            if (relStoreFile == null || !relStoreFile.exists()) {
                error(
                    "Missing heritageReleaseStoreFile or keystore not found.\n" +
                        "  1. Run: keytool -genkey -v -keystore ~/.gradle/heritage-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias heritage-release\n" +
                        "  2. Add to ~/.gradle/gradle.properties:\n" +
                        "    heritageReleaseStoreFile=~/.gradle/heritage-release.jks\n" +
                        "    heritageReleaseStorePassword=<your-store-password>\n" +
                        "    heritageReleaseKeyAlias=heritage-release\n" +
                        "    heritageReleaseKeyPassword=<your-key-password>",
                )
            }
            if (relStorePwd.isNullOrBlank()) error("Missing heritageReleaseStorePassword. Set it in ~/.gradle/gradle.properties")
            if (relKeyAlias.isNullOrBlank()) error("Missing heritageReleaseKeyAlias. Set it in ~/.gradle/gradle.properties")
            if (relKeyPwd.isNullOrBlank()) error("Missing heritageReleaseKeyPassword. Set it in ~/.gradle/gradle.properties")
        }
    }
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
