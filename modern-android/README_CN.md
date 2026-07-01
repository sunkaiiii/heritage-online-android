# Heritage Online Modern Android

[English](README.md) | 简体中文

这是下一代 **E迹** 非遗移动平台的现代 Android 壳工程。

仓库根目录的旧版 Android App 保持原样，作为功能参考。本工程刻意独立，以便新客户端使用当前 Android 工具链，而不拖入旧版 Gradle 配置。

## 当前基线

- Gradle 9.5.1
- Android Gradle Plugin 9.2.1
- Kotlin 2.3.21
- Compose BOM 2026.05.01
- Material 3
- Navigation 3（类型化返回栈）
- Ktor Client 3.5.0
- kotlinx.serialization 1.11.0
- Hilt 2.59.2
- Room 2.8.4
- Paging 3.5.0
- Flow / StateFlow 优先的 UI 状态管理

## 运行

```bash
cd modern-android
./gradlew :app:assembleDebug
```

## API 契约层

网络契约层位于 `app/src/main/java/com/duckylife/heritage/modern/core/network`：

- DTO：`core/network/dto`
- API 客户端：`core/network/HeritageApiClient.kt`
- Repository 边界：`core/data/HeritageRepository.kt`
- 基地址通过 `BuildConfig.HERITAGE_API_BASE_URL` 注入（默认 `https://tuantuan.myds.me:28887`）

接口包括（但不限于）：

- `/api/home-banners`
- `/api/articles`
- `/api/directory-items`
- `/api/inheritors`
- `/api/search/v2`
- `/api/learning-routes`
- `/api/rankings`
- `/api/spacetime`
- `/api/research`

## 本地验证

本项目不做远端 CI。每次大改后，按改动范围跑对应命令确认项目没坏。

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
| Repository、Room、Paging、导航 | `./gradlew :app:verifyLocal`<br>`./gradlew :app:connectedDebugAndroidTest`（需模拟器/真机在线） |
| Release、签名、Gradle 配置 | `./gradlew :app:verifyLocal`<br>无签名参数跑 `:app:assembleRelease` 应失败<br>有签名参数跑 `:app:assembleRelease` 应成功 |
| 后端 API、Mock 数据 | `./gradlew :app:connectedDebugAndroidTest` |

### 单步命令

```bash
# 单元测试 + debug 构建
./gradlew :app:verifyLocal

# Instrumentation 测试（101 个，需模拟器/真机在线）
./gradlew :app:connectedDebugAndroidTest

# Release（需先配置签名，见下方）
./gradlew :app:assembleRelease
```

## Release 签名

Release 构建**必须**签名。缺少签名配置时，`assembleRelease` 会失败并提示具体缺少哪几项。

生成 keystore（只需一次）：

```bash
keytool -genkey -v -keystore ~/.gradle/heritage-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias heritage-release
```

然后在 `~/.gradle/gradle.properties`（或不提交到仓库的仓库级 `gradle.properties`）中加入：

```properties
heritageReleaseStoreFile=~/.gradle/heritage-release.jks
heritageReleaseStorePassword=<your-store-password>
heritageReleaseKeyAlias=heritage-release
heritageReleaseKeyPassword=<your-key-password>
```

`~` 路径会自动展开；构建时会校验 keystore 文件确实存在。

## 安装

```bash
# Debug（不需要签名）
./gradlew :app:installDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Release（需先配置签名）
./gradlew :app:assembleRelease
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 数据库治理

Room schema 已导出到 `app/schemas/`。改 Entity 时必须遵循：

1. 升版本号（`HeritageDatabase.version`）
2. 在 `HeritageMigrations.kt` 补对应的 `Migration`
3. 跑 `./gradlew :app:assembleDebug` 生成新 schema JSON
4. 跑 migration test 确认迁移正确

已移除 `.fallbackToDestructiveMigration(true)`。想重建数据库：卸载 App 或在系统设置里清除应用数据。

## 网络配置

基地址与 HTTPS 信任设置通过 `BuildConfig` 在构建时注入：

| 字段 | 来源 | 默认值 |
|-------|--------|---------|
| `HERITAGE_API_BASE_URL` | `-PheritageApiBaseUrl=...` | `https://tuantuan.myds.me:28887` |
| `HERITAGE_TRUST_SELF_SIGNED_CERTS` | `-PheritageTrustSelfSigned=...` | debug `true` / release `false` |

### 模拟器 / 本地开发

Android 模拟器将宿主机的 `localhost` 映射为 `10.0.2.2`。当后端运行在本机时，可通过 Gradle property 显式指定：

```bash
./gradlew :app:assembleDebug -PheritageApiBaseUrl=https://10.0.2.2:5078
```

不要把 `localhost`、`127.0.0.1` 或内网 IP 写入仓库；这些地址仅在构建命令或本机 `~/.gradle/gradle.properties` 中作为覆盖值使用。

### 局域网真机

使用本机局域网 IP，让真机访问同一网络下的后端：

```bash
./gradlew :app:assembleDebug -PheritageApiBaseUrl=https://192.168.x.x:5078
```

### 公开 HTTPS 服务（推荐用于发布）

发布构建必须通过公开可信证书访问后端：

```properties
heritageApiBaseUrl=https://your-public-domain/
heritageTrustSelfSigned=false
```

```bash
./gradlew :app:assembleRelease
```

Release 构建会校验：基地址必须是 `https://`，且 `heritageTrustSelfSigned` 必须为 `false`。若使用 `http://` 或启用 trust-all，构建/启动将明确失败，不会静默发送明文请求。

### 自签名证书

Debug 构建默认信任自签名证书，方便本地后端调试。Release 构建默认不信任自签名证书；**不要**为了绕过证书问题在 release 中启用 trust-all。

```bash
# 仅 debug 可用
./gradlew :app:assembleDebug -PheritageTrustSelfSigned=true
```

当 `HERITAGE_TRUST_SELF_SIGNED_CERTS` 为 `false` 时，OkHttp 引擎会拒绝系统信任库之外的证书。
