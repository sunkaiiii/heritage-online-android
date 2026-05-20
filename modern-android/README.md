# Heritage Online Modern Android

This is the clean, modern Android shell for the next version of E迹.

The legacy app in the repository root remains untouched as a functional reference. This project is intentionally independent so the new client can move to current Android tooling without dragging the old Gradle setup along.

## Current Baseline

- Gradle 9.5.1
- Android Gradle Plugin 9.2.1
- Kotlin 2.3.21
- Compose BOM 2026.05.00
- Material 3
- Navigation 3 dependencies prepared for the next slice
- Ktor Client 3.4.3
- kotlinx.serialization 1.11.0
- Flow/StateFlow-first architecture planned

## Run

```bash
cd modern-android
./gradlew :app:assembleDebug
```

## API Contract Layer

The first network contract layer is in place:

- DTOs: `app/src/main/java/com/duckylife/heritage/modern/core/network/dto`
- API client: `app/src/main/java/com/duckylife/heritage/modern/core/network/HeritageApiClient.kt`
- Repository boundary: `app/src/main/java/com/duckylife/heritage/modern/core/data/HeritageRepository.kt`
- Base URL configured via `BuildConfig.HERITAGE_API_BASE_URL` (default `https://10.0.2.2:5078`)

The contract maps the local Swagger document:

- `/api/home-banners`
- `/api/articles`
- `/api/directory-items`
- `/api/inheritors`

## Local Verification

本项目不做远端 CI。每次大改后，按改动范围跑对应命令即可确认项目没坏。

### 快速检查（推荐日常开发）

```bash
cd modern-android
./gradlew :app:verifyLocal
```

等价于 `testDebugUnitTest` + `assembleDebug`，不依赖模拟器。

### 改动分级检查表

| 改动范围 | 需要跑的命令 |
|---------|------------|
| UI 布局、文案、样式 | `./gradlew :app:verifyLocal` |
| Repository、Room、Paging、导航 | `./gradlew :app:verifyLocal`<br>`./gradlew :app:connectedDebugAndroidTest`（需模拟器在线） |
| Release、签名、Gradle 配置 | `./gradlew :app:verifyLocal`<br>无签名参数跑 `:app:assembleRelease` 应失败<br>有签名参数跑 `:app:assembleRelease` 应成功 |
| 后端 API、Mock 数据 | `./gradlew :app:connectedDebugAndroidTest`（模拟器 UI 验收） |

### 单步命令

```bash
# 单元测试 + debug 构建
./gradlew :app:verifyLocal

# Instrumentation 测试（39 个，需模拟器在线）
./gradlew :app:connectedDebugAndroidTest

# Release（需先配置签名，见下方）
./gradlew :app:assembleRelease
```

## Release Signing

Release 构建**必须**签名。缺少签名配置时，`assembleRelease` 会失败并提示具体缺少哪几项。

首先生成 keystore（仅一次）：

```bash
keytool -genkey -v -keystore ~/.gradle/heritage-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias heritage-release
```

然后在 `~/.gradle/gradle.properties`（或仓库级 `gradle.properties`）中加入：

```properties
heritageReleaseStoreFile=~/.gradle/heritage-release.jks
heritageReleaseStorePassword=<your-store-password>
heritageReleaseKeyAlias=heritage-release
heritageReleaseKeyPassword=<your-key-password>
```

`~` 路径会自动展开；构建时会校验 keystore 文件确实存在。

## Install

```bash
# Debug（不需要签名）
./gradlew :app:installDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Release（需先配置签名）
./gradlew :app:assembleRelease
adb install -r app/build/outputs/apk/release/app-release.apk
```

## Network Configuration

The base URL and HTTPS trust settings are injected at build time via `BuildConfig`:

| Field | Source | Default |
|-------|--------|---------|
| `HERITAGE_API_BASE_URL` | `-PheritageApiBaseUrl=...` | `https://10.0.2.2:5078` |
| `HERITAGE_TRUST_SELF_SIGNED_CERTS` | `-PheritageTrustSelfSigned=...` | `true` (debug) / `false` (release) |

### Emulator (default)

The Android emulator maps the host machine's `localhost` to `10.0.2.2`.
No extra configuration is needed when the backend is running on the same machine.

### Physical device

Use your machine's LAN IP so the device can reach the backend:

```bash
./gradlew :app:assembleDebug -PheritageApiBaseUrl=https://192.168.x.x:5078
```

### Self-signed certificates

Debug builds trust self-signed certificates by default.
Release builds do not. To force trust in a release build:

```bash
./gradlew :app:assembleRelease -PheritageTrustSelfSigned=true
```

When `HERITAGE_TRUST_SELF_SIGNED_CERTS` is false, the OkHttp engine
will reject any certificate that is not trusted by the system trust store.
