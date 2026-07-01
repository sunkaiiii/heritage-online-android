# Heritage Online Modern Android

English | [简体中文](README_CN.md)

This is the clean, modern Android shell for the next version of **E迹** (Heritage Online).

The legacy app in the repository root remains untouched as a functional reference. This project is intentionally independent so the new client can move to current Android tooling without dragging the old Gradle setup along.

## Current Baseline

- Gradle 9.5.1
- Android Gradle Plugin 9.2.1
- Kotlin 2.3.21
- Compose BOM 2026.05.01
- Material 3
- Navigation 3 with typed back stack
- Ktor Client 3.5.0
- kotlinx.serialization 1.11.0
- Hilt 2.59.2
- Room 2.8.4
- Paging 3.5.0
- Flow / StateFlow-first UI state

## Run

```bash
cd modern-android
./gradlew :app:assembleDebug
```

## API Contract Layer

The network contract layer lives under `app/src/main/java/com/duckylife/heritage/modern/core/network`:

- DTOs: `core/network/dto`
- API client: `core/network/HeritageApiClient.kt`
- Repository boundary: `core/data/HeritageRepository.kt`
- Base URL configured via `BuildConfig.HERITAGE_API_BASE_URL` (default `https://tuantuan.myds.me:28887`)

Endpoints include (but are not limited to):

- `/api/home-banners`
- `/api/articles`
- `/api/directory-items`
- `/api/inheritors`
- `/api/search/v2`
- `/api/learning-routes`
- `/api/rankings`
- `/api/spacetime`
- `/api/research`

## Local Verification

This project does not use remote CI. Run the appropriate commands locally after significant changes.

### Quick check (recommended for daily development)

```bash
cd modern-android
./gradlew :app:verifyLocal
```

Equivalent to `testDebugUnitTest` + `assembleDebug`; no emulator required.

### Verification by change scope

| Scope | Commands |
|-------|----------|
| UI layout, copy, styles | `./gradlew :app:verifyLocal` |
| Repository, Room, Paging, navigation | `./gradlew :app:verifyLocal`<br>`./gradlew :app:connectedDebugAndroidTest` (emulator/device required) |
| Release, signing, Gradle config | `./gradlew :app:verifyLocal`<br>`./gradlew :app:assembleRelease` should fail without signing<br>`./gradlew :app:assembleRelease` should succeed with signing |
| Backend API / mock data | `./gradlew :app:connectedDebugAndroidTest` |

### Individual commands

```bash
# Unit tests + debug build
./gradlew :app:verifyLocal

# Instrumentation tests (101 tests, emulator/device required)
./gradlew :app:connectedDebugAndroidTest

# Release (requires signing configuration, see below)
./gradlew :app:assembleRelease
```

## Release Signing

Release builds **must** be signed. `assembleRelease` will fail with a clear message if the signing configuration is missing.

Generate a keystore once:

```bash
keytool -genkey -v -keystore ~/.gradle/heritage-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias heritage-release
```

Then add to `~/.gradle/gradle.properties` (or a repository-level `gradle.properties` that is not committed):

```properties
heritageReleaseStoreFile=~/.gradle/heritage-release.jks
heritageReleaseStorePassword=<your-store-password>
heritageReleaseKeyAlias=heritage-release
heritageReleaseKeyPassword=<your-key-password>
```

The `~` path is expanded automatically, and the build validates that the keystore file exists.

## Install

```bash
# Debug (no signing required)
./gradlew :app:installDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Release (requires signing)
./gradlew :app:assembleRelease
adb install -r app/build/outputs/apk/release/app-release.apk
```

## Database Governance

Room schemas are exported to `app/schemas/`. When changing an entity:

1. Bump the version in `HeritageDatabase`.
2. Add the corresponding `Migration` in `HeritageMigrations.kt`.
3. Run `./gradlew :app:assembleDebug` to generate the new schema JSON.
4. Run the migration tests to confirm correctness.

`.fallbackToDestructiveMigration(true)` has been removed. To reset the database, uninstall the app or clear app data in system settings.

## Network Configuration

The base URL and HTTPS trust settings are injected at build time via `BuildConfig`:

| Field | Source | Default |
|-------|--------|---------|
| `HERITAGE_API_BASE_URL` | `-PheritageApiBaseUrl=...` | `https://tuantuan.myds.me:28887` |
| `HERITAGE_TRUST_SELF_SIGNED_CERTS` | `-PheritageTrustSelfSigned=...` | `true` (debug) / `false` (release) |

### Emulator / local development

The Android emulator maps host `localhost` to `10.0.2.2`. When the backend runs on the host machine, override via Gradle property:

```bash
./gradlew :app:assembleDebug -PheritageApiBaseUrl=https://10.0.2.2:5078
```

Do not commit `localhost`, `127.0.0.1`, or LAN IPs to the repository. Use them only in local build commands or `~/.gradle/gradle.properties`.

### LAN device

Use the host's LAN IP so a physical device on the same network can reach the backend:

```bash
./gradlew :app:assembleDebug -PheritageApiBaseUrl=https://192.168.x.x:5078
```

### Public HTTPS (recommended for release)

Release builds must use a publicly trusted certificate:

```properties
heritageApiBaseUrl=https://your-public-domain/
heritageTrustSelfSigned=false
```

```bash
./gradlew :app:assembleRelease
```

Release builds validate that the base URL is `https://` and that `heritageTrustSelfSigned` is `false`. Using `http://` or enabling trust-all will fail explicitly rather than silently sending plaintext requests.

### Self-signed certificates

Debug builds trust self-signed certificates by default to simplify local backend debugging. Release builds do **not** trust self-signed certificates; do not enable trust-all in release.

```bash
# Debug only
./gradlew :app:assembleDebug -PheritageTrustSelfSigned=true
```

When `HERITAGE_TRUST_SELF_SIGNED_CERTS` is `false`, the OkHttp engine rejects certificates not in the system trust store.
