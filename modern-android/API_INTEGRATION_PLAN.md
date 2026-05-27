# 安卓客户端新版 API 对接实施文档

> 依据后端合同：`/Users/kaisun/Documents/Github/HeritageOnlineDotNetCore/API-CONTRACT.md`  
> 依据安卓接入 commit：`e26cc75 new api`  
> 目标：先校准数据合同，再按“发现”聚合入口逐步接入 Search、Explore、Learning Paths、Timeline、Region Atlas、Collections 和详情页 Context。

## 1. 当前对接审查结论

### 1.1 已经接得比较正确的部分

`new api` commit 已经完成了新版 API 的第一层数据接入，主要变更集中在：

- `core/network/HeritageApiClient.kt`
- `core/network/HeritageQueries.kt`
- `core/network/dto/HeritageDtos.kt`
- `core/data/HeritageRepository.kt`
- Fake repository / fake API client
- DTO 序列化测试和 Ktor MockEngine 测试

当前已经覆盖的稳定接口包括：

| 功能 | Android 方法 | 后端接口 | 当前状态 |
| --- | --- | --- | --- |
| 首页聚合 | `getHomeFeed()` | `GET /api/home/feed` | 已接入，需复查 DTO 字段 |
| 首页 Banner | `getHomeBanners()` | `GET /api/home-banners` | 已接入 |
| 搜索 v2 | `searchV2(query)` | `GET /api/search/v2` | 路径正确，DTO 需修 |
| 搜索建议 | `getSearchSuggestions(prefix, limit)` | `GET /api/search/suggestions` | 路径和参数正确 |
| 时间线 v2 | `getTimelineV2(query)` | `GET /api/timeline/v2` | 路径正确，DTO 需修 |
| 时间线年份 | `getTimelineYears()` | `GET /api/timeline/years` | 路径正确，DTO 需修 |
| 探索首页 | `getExploreIndex()` | `GET /api/explore` | 路径正确，DTO 需修 |
| 探索主题列表 | `getExploreTopics(type, limit)` | `GET /api/explore/topics` | 路径和参数基本正确 |
| 探索主题详情 | `getExploreTopic(type, key, limit)` | `GET /api/explore/topics/{type}/{key}` | 路径正确，需 path segment 编码 |
| 学习路径列表 | `getLearningPaths()` | `GET /api/explore/learning-paths` | 路径正确，DTO 需修 |
| 学习路径详情 | `getLearningPathDetail(id, limit)` | `GET /api/explore/learning-paths/{id}` | 路径正确，DTO 需修 |
| 地区图谱首页 | `getRegionAtlas()` | `GET /api/regions/atlas` | 路径正确，DTO 需修 |
| 地区图谱详情 | `getRegionAtlasDetail(region, limit)` | `GET /api/regions/{region}/atlas` | 路径正确，需 path segment 编码，DTO 需修 |
| 精选合集 | `getFeaturedCollections()` | `GET /api/collections/featured` | 路径正确，DTO 需修 |
| 合集详情 | `getCollection(id, limit)` | `GET /api/collections/{id}` | 路径正确，DTO 需修 |
| 主题合集 | `getTopicCollection(type, key, limit)` | `GET /api/collections/topic/{type}/{key}` | 路径正确，需 path segment 编码 |
| 详情页上下文 | `getArticleContext()` / `getDirectoryItemContext()` / `getInheritorContext()` | `GET /*/{id}/context` | 路径正确，DTO 需修 |

整体判断：

- **接口方法覆盖率是好的。**
- **Repository 暴露方式是合理的。**
- **第一版不入 Room 是合理的。**
- **真正的问题集中在 DTO shape 与后端合同不一致。**

### 1.2 必须优先修正的问题

这些问题不会一定导致编译失败，但会导致运行时静默丢字段，UI 表现为空白或默认值。

#### Search v2

后端 `SearchV2ResponseDto`：

- `items`
- `page`
- `pageSize`
- `hasMore`
- `total`
- `facets`
- `query`

当前 Android 问题：

- 缺 `query`。
- `facets` 缺 `types`。
- `facets` 多了非合同字段 `facets: List<SearchFacetDto>`。
- `SearchResultItemDto` 缺：
  - `publishedAt`
  - `highlights`
  - `matchedFields`
- `score` 后端是 `Int`，Android 当前是 `Double`。

#### Timeline v2

后端 `TimelineV2ResponseDto`：

- `items`
- `page`
- `pageSize`
- `hasMore`
- `total`
- `facets`

当前 Android 问题：

- Android 当前写了 `years`，但后端 timeline v2 响应是 `facets`。
- `TimelineItemDto` 当前字段是 `publishedAt`，后端字段是 `date`。
- `/api/timeline/years` 后端返回：
  - `year`
  - `total`
  - `articleCount`
  - `directoryItemCount`
  - `inheritorCount`
  Android 当前只有 `count`。

#### Explore / Learning Paths

后端 `ExploreIndexDto`：

- `regions`
- `categories`
- `years`

当前 Android 问题：

- Android 当前是 `topics`、`learningPaths`、`featuredCollections`，不匹配。

后端 `ExploreTopicInfoDto`：

- `type`
- `key`
- `title`
- `subtitle`

当前 Android 问题：

- Android 当前是 `name`、`description`、`itemCount`。

后端 `ExploreTopicV2Dto`：

- `topic`
- `stats`
- `sections`
- `relatedTopics`
- `timeline`
- `generatedAt`

当前 Android 问题：

- Android 当前把 `type/key/name/description` 平铺在根对象。
- 缺 `topic`、`relatedTopics`、`timeline`、`generatedAt`。
- section 后端字段是 `id/title/subtitle/items`，Android 当前是 `heading/items`。

后端 `LearningPathDto`：

- `id`
- `title`
- `subtitle`
- `topics`
- `description`
- `coverImage`
- `estimatedItemCount`
- `stepCount`
- `tags`

当前 Android 问题：

- Android 当前使用 `name`。
- Android 当前 `coverImage` 是 `MediaAssetDto?`，后端是 `String?`。
- Android 当前 `itemCount` 不匹配后端 `estimatedItemCount`。
- 缺 `topics`、`subtitle`、`tags`。

后端 `LearningPathDetailDto`：

- `id`
- `title`
- `subtitle`
- `description`
- `tags`
- `steps`
- `featuredItems`
- `relatedTopics`
- `generatedAt`

当前 Android 问题：

- Android 当前使用 `name`。
- 缺 `subtitle`、`tags`、`featuredItems`、`relatedTopics`、`generatedAt`。

#### Region Atlas

后端 `RegionAtlasDto`：

- `regions`
- `totals`
- `generatedAt`

后端 `RegionAtlasTotalsDto`：

- `directoryItemCount`
- `inheritorCount`
- `regionCount`

后端 `RegionAtlasItemDto`：

- `region`
- `displayName`
- `directoryItemCount`
- `inheritorCount`
- `total`
- `topCategories`
- `topKinds`
- `coverImage`

当前 Android 问题：

- Android 当前 item 是 `region/count/highlightImage`。
- Android 当前 totals 是 `regions/items`。
- 缺大量统计字段，地区图谱 UI 无法正确展示。

后端 `RegionAtlasDetailDto`：

- `region`
- `displayName`
- `stats`
- `categoryBreakdown`
- `kindBreakdown`
- `featuredDirectoryItems`
- `featuredInheritors`
- `relatedArticles`
- `timeline`
- `relatedRegions`
- `generatedAt`

当前 Android 问题：

- Android 当前只有 `region/stats/items`。
- `RegionAtlasStatsDto` 也不匹配后端 detail stats。

#### Collections

后端 `FeaturedCollectionDto`：

- `id`
- `title`
- `subtitle`
- `itemCount`

当前 Android 问题：

- Android 当前是 `name/description/coverImage/itemCount`。

后端 `CollectionDto`：

- `id`
- `title`
- `subtitle`
- `type`
- `tags`
- `generatedAt`
- `items`

当前 Android 问题：

- Android 当前是 `name/description/coverImage/items/total/hasMore`。

后端 `CollectionItemDto`：

- `type`
- `id`
- `title`
- `summary`
- `category`
- `region`
- `publishedAt`
- `publishedYear`
- `coverImage`
- `sourceUrl`

当前 Android 问题：

- 缺 `category`、`region`、`publishedAt`、`publishedYear`。

#### Detail Context / Graph / Recommendations

后端 `ContextCollectionDto`：

- `id`
- `title`
- `items`

当前 Android 问题：

- Android 当前是 `name/description/items`。

后端 `GraphNodeDto`：

- `id`
- `type`
- `title`
- `category`
- `region`
- `sourceUrl`
- `subtitle`
- `coverImage`

当前 Android 问题：

- Android 当前有 `label/properties`，不匹配。

后端 `GraphEdgeDto`：

- `from`
- `to`
- `label`
- `relationType`
- `reason`
- `source`
- `weight`

当前 Android 问题：

- Android 当前是 `sourceId/targetId/properties`。

后端 `RecommendationDto`：

- `type`
- `id`
- `title`
- `subtitle`
- `relationType`
- `reason`
- `weight`
- `source`
- `category`
- `region`
- `publishedAt`
- `publishedYear`
- `coverImage`
- `sourceUrl`

当前 Android 问题：

- 缺 `type/subtitle/category/region/publishedAt/publishedYear`。

后端 `RelatedSummaryDto`：

- `id`
- `type`
- `title`
- `category`
- `kind`
- `region`
- `sourceUrl`

当前 Android 问题：

- 缺 `category/kind/region`。
- 多出的 `summary/coverImage` 不影响解析，但不应作为 UI 依赖字段。

## 2. 第一阶段：修正 API 数据层合同

### 2.1 目标

先让 Android 数据层严格贴合后端合同，再进入 UI 开发。

验收标准：

- Android DTO 字段名与后端 C# DTO 一致。
- Mock JSON 使用后端真实 shape。
- 序列化测试能证明关键字段不会静默丢失。
- `SearchV2Query` 和 `TimelineV2Query` 不再使用裸字符串表达类型集合。
- 中文 path segment 能被安全编码。

### 2.2 建议文件结构

当前 `HeritageDtos.kt` 已经过大，建议拆分：

```text
core/network/dto/
  CommonDtos.kt
  ContentDtos.kt
  SearchDtos.kt
  TimelineDtos.kt
  ExploreDtos.kt
  RegionDtos.kt
  CollectionDtos.kt
  ContextDtos.kt
```

拆分规则：

- `CommonDtos.kt`
  - `PagedResult`
  - `MediaAssetDto`
  - `ProblemDetailsDto`
- `ContentDtos.kt`
  - Article DTO
  - Directory DTO
  - Inheritor DTO
  - Article/Directory enum
- `SearchDtos.kt`
  - `SearchResultType`
  - `SearchV2ResponseDto`
  - `SearchV2ResultItemDto`
  - `SearchV2FacetsDto`
  - `FacetBucketDto`
  - `SearchSuggestionDto`
- `TimelineDtos.kt`
  - `TimelineV2ResponseDto`
  - `TimelineV2ItemDto`
  - `TimelineV2FacetsDto`
  - `TimelineYearBucketDto`
- `ExploreDtos.kt`
  - `ExploreIndexDto`
  - `ExploreTopicInfoDto`
  - `ExploreTopicV2Dto`
  - `ExploreTopicSectionDto`
  - `ExploreTopicItemDto`
  - `LearningPathDto`
  - `LearningPathDetailDto`
- `RegionDtos.kt`
  - `RegionStatisticDto`
  - `RegionOverviewDto`
  - `RegionAtlasDto`
  - `RegionAtlasDetailDto`
- `CollectionDtos.kt`
  - `FeaturedCollectionDto`
  - `CollectionDto`
  - `CollectionItemDto`
- `ContextDtos.kt`
  - `DetailContextDto`
  - `RelatedSummaryDto`
  - `RecommendationDto`
  - `GraphDto`
  - `GraphNodeDto`
  - `GraphEdgeDto`
  - `ContextCollectionDto`

### 2.3 Query 类型调整

修改 `core/network/HeritageQueries.kt`：

```kotlin
enum class SearchResultType(val wireName: String) {
    Article("article"),
    DirectoryItem("directoryItem"),
    Inheritor("inheritor"),
}

enum class ExploreTopicType(val wireName: String) {
    Region("region"),
    Category("category"),
    Year("year"),
    Kind("kind"),
}

enum class ExploreTopicListType(val wireName: String) {
    Region("region"),
    Category("category"),
    Year("year"),
    Mixed("mixed"),
    All("all"),
}

enum class CollectionTopicType(val wireName: String) {
    Region("region"),
    Category("category"),
    Year("year"),
}
```

调整 query：

```kotlin
data class SearchV2Query(
    val keywords: String,
    val types: Set<SearchResultType> = emptySet(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val kind: DirectoryItemKind? = null,
    val hasImage: Boolean? = null,
)

data class TimelineV2Query(
    val year: Int? = null,
    val types: Set<SearchResultType> = emptySet(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val category: String? = null,
    val region: String? = null,
    val kind: DirectoryItemKind? = null,
    val hasImage: Boolean? = null,
)
```

API client 内部处理：

```kotlin
optionalParameter(
    "types",
    query.types.takeIf { it.isNotEmpty() }?.joinToString(",") { it.wireName },
)
```

### 2.4 Path segment 编码

需要重点处理：

- `getExploreTopic(type, key, limit)`
- `getRegionAtlasDetail(region, limit)`
- `getTopicCollection(type, key, limit)`

建议新增私有工具方法：

```kotlin
private fun pathSegment(value: String): String =
    value.encodeURLPathPart()
```

然后：

```kotlin
endpoint("api/explore/topics/${pathSegment(type)}/${pathSegment(key)}")
endpoint("api/regions/${pathSegment(region)}/atlas")
endpoint("api/collections/topic/${pathSegment(type)}/${pathSegment(key)}")
```

注意：如果使用 Ktor URL builder 更稳，也可以直接改为 `httpClient.get { url { ... } }`，但第一轮可先用 path segment helper。

### 2.5 数据层测试

必须重写 `HeritageDtoSerializationTest` 中这些用例：

- `searchV2ResponseDto_parsesBackendShape`
- `timelineV2ResponseDto_parsesBackendShape`
- `timelineYearBucketDto_parsesBackendShape`
- `exploreIndexDto_parsesBackendShape`
- `exploreTopicV2Dto_parsesBackendShape`
- `learningPathDto_parsesBackendShape`
- `learningPathDetailDto_parsesBackendShape`
- `regionAtlasDto_parsesBackendShape`
- `regionAtlasDetailDto_parsesBackendShape`
- `collectionDto_parsesBackendShape`
- `detailContextDto_parsesBackendShape`

必须重写 `KtorHeritageApiClientTest` 中这些用例：

- Search v2 query 参数：
  - `types=article,directoryItem`
  - `kind=nationalProject`
  - `hasImage=true`
- Timeline v2 query 参数：
  - `types=article,inheritor`
  - `year`
  - `region`
  - `category`
- Explore topic path：
  - 中文 key 能正确进入 path。
- Region atlas detail path：
  - 中文 region 能正确进入 path。
- Topic collection path：
  - 中文 key 能正确进入 path。

## 3. 第二阶段：新增“发现”主入口

### 3.1 UI 放置位置

底部导航新增第 4 个入口：

```text
文章 | 名录 | 传承人 | 发现
```

不要为 Search、Timeline、Explore、Region Atlas、Collections 分别增加底部导航。

`发现`是新版 API 的统一入口，承载：

- 全局搜索
- 探索主题
- 学习路径
- 地区图谱
- 合集
- 时间线

### 3.2 需要新增的文件

```text
feature/discovery/
  DiscoveryNavigation.kt
  DiscoveryScreen.kt
  DiscoveryUiState.kt
  DiscoveryViewModel.kt
```

`MainActivity.kt` 修改：

- `HomeDestination` 增加：

```kotlin
Discovery(R.string.nav_discovery, Icons.Outlined.Explore)
```

- `when (selectedDestination)` 中增加 `DiscoveryNavHost(...)`。

字符串：

- `values/strings.xml`
  - `nav_discovery`: `发现`
- `values-en/strings.xml`
  - `nav_discovery`: `Discover`

### 3.3 发现页首屏结构

发现页第一屏不做营销 hero，直接做可用的信息工作台：

```text
顶部搜索框
今日探索 topic chips
学习路径横向卡片
精选合集横向卡片
地区图谱入口卡片
时间线入口卡片
```

数据来源：

| UI 区块 | API |
| --- | --- |
| 今日探索 | `GET /api/explore/topics?type=all&limit=12` |
| 探索首页补充 | `GET /api/explore` |
| 学习路径 | `GET /api/explore/learning-paths` |
| 精选合集 | `GET /api/collections/featured` |
| 地区图谱摘要 | `GET /api/regions/atlas` |

ViewModel 建议：

```kotlin
data class DiscoveryUiState(
    val isLoading: Boolean = false,
    val errorKind: ErrorKind? = null,
    val exploreIndex: ExploreIndexDto? = null,
    val topics: List<ExploreTopicInfoDto> = emptyList(),
    val learningPaths: List<LearningPathDto> = emptyList(),
    val featuredCollections: List<FeaturedCollectionDto> = emptyList(),
    val regionAtlas: RegionAtlasDto? = null,
)
```

加载策略：

- 首次进入发现页并发请求。
- 任一接口失败时允许局部降级。
- 如果所有接口都失败，显示整页错误态。
- 下拉刷新或 header refresh 按钮重新加载。

## 4. 第三阶段：功能页面对接

### 4.1 全局搜索

文件结构：

```text
feature/search/
  SearchScreen.kt
  SearchUiState.kt
  SearchViewModel.kt
```

入口：

- 发现页顶部搜索框。
- 后续可在文章、名录、传承人 header 增加搜索按钮。

API：

- `GET /api/search/suggestions`
- `GET /api/search/v2`

UI 要求：

- 顶部固定搜索框。
- 输入前显示搜索建议。
- 输入后显示混合搜索结果。
- 结果类型用 chip 标识：
  - 文章
  - 名录
  - 传承人
- 筛选 chips：
  - 类型
  - 地区
  - 类别
  - 年份
  - kind
  - 仅看有图

交互：

- article 跳文章详情。
- directoryItem 跳名录详情。
- inheritor 跳传承人详情。

数据策略：

- 第一版不入 Room。
- 不接 Paging3。
- pageSize 20。
- 滚动到底部手动加载下一页。

### 4.2 探索主题

文件结构：

```text
feature/explore/
  ExploreTopicDetailScreen.kt
  ExploreTopicDetailUiState.kt
  ExploreTopicDetailViewModel.kt
```

API：

- `GET /api/explore`
- `GET /api/explore/topics`
- `GET /api/explore/topics/{type}/{key}`

UI 要求：

- Topic Detail 顶部展示：
  - `topic.title`
  - `topic.subtitle`
  - stats chips
- 中部展示 sections：
  - 每个 section 使用 `id/title/subtitle/items`
  - section 内使用横向列表或紧凑网格
- 底部展示：
  - timeline
  - relatedTopics

跳转：

- `ExploreTopicItemDto.type == "article"` 跳文章详情。
- `directoryItem` 跳名录详情。
- `inheritor` 跳传承人详情。
- 没有 id 的 item 只展示，不跳转。

### 4.3 学习路径

文件结构：

```text
feature/learning/
  LearningPathDetailScreen.kt
  LearningPathDetailUiState.kt
  LearningPathDetailViewModel.kt
```

API：

- `GET /api/explore/learning-paths`
- `GET /api/explore/learning-paths/{id}`

UI 要求：

- 发现页横向展示学习路径卡片。
- 详情页顶部显示：
  - title
  - subtitle
  - description
  - tags
- steps 使用纵向 stepper 风格：
  - 左侧 step 序号
  - 右侧 step title/subtitle/items
- featuredItems 独立成精选内容区。
- relatedTopics 放底部。

设计方向：

- 更像学习路线，不做营销页面。
- 信息层级清晰，可连续阅读。

### 4.4 时间线

文件结构：

```text
feature/timeline/
  TimelineScreen.kt
  TimelineUiState.kt
  TimelineViewModel.kt
```

API：

- `GET /api/timeline/years`
- `GET /api/timeline/v2`

UI 要求：

- 顶部年份选择器：
  - 横向 chip 优先。
  - 年份过多时可后续升级为 bottom sheet。
- 时间线主体：
  - 左侧时间点。
  - 右侧内容卡片。
  - 不做普通列表堆叠。
- 筛选：
  - 类型
  - 地区
  - 类别
  - kind
  - 仅看有图

数据策略：

- 第一版手动分页。
- pageSize 20。
- 年份或筛选变化时清空旧 items 并重新请求第一页。

### 4.5 地区图谱

文件结构：

```text
feature/regions/
  RegionAtlasScreen.kt
  RegionAtlasDetailScreen.kt
  RegionAtlasUiState.kt
  RegionAtlasViewModel.kt
  RegionAtlasDetailViewModel.kt
```

API：

- `GET /api/regions/atlas`
- `GET /api/regions/{region}/atlas`

UI 要求：

- Atlas 首页使用地区卡片网格，不做复杂地图。
- 地区卡片展示：
  - displayName
  - directoryItemCount
  - inheritorCount
  - total
  - topCategories chips
  - topKinds chips
  - coverImage
- 地区详情展示：
  - 总览统计
  - categoryBreakdown
  - kindBreakdown
  - featuredDirectoryItems
  - featuredInheritors
  - relatedArticles
  - timeline
  - relatedRegions

设计要求：

- 可视化克制。
- 优先使用 progress bar、排行榜、chips。
- 不做地图 SVG，除非以后有省份边界资产。

### 4.6 合集

文件结构：

```text
feature/collections/
  CollectionDetailScreen.kt
  CollectionDetailUiState.kt
  CollectionDetailViewModel.kt
```

API：

- `GET /api/collections/featured`
- `GET /api/collections/{id}`
- `GET /api/collections/topic/{type}/{key}`

UI 要求：

- Featured collections 放发现页。
- Collection detail 独立页面。
- 标题区展示：
  - title
  - subtitle
  - type
  - tags
  - generatedAt
- items 使用混合内容列表。

固定 collection id：

- `latest-news`
- `latest-special-topics`
- `recent-forum`
- `national-projects`
- `featured-inheritors`
- `with-images`
- `latest-all`
- `recommended-articles`
- `recommended-directory-items`
- `recommended-inheritors`
- `image-gallery`

### 4.7 详情页 Context 增强

放置位置：

- 文章详情页底部。
- 名录详情页底部。
- 传承人详情页底部。

API：

- `GET /api/articles/{id}/context`
- `GET /api/directory-items/{id}/context`
- `GET /api/inheritors/{id}/context`

UI 分区顺序：

1. Related
2. Recommendations
3. Semantic Recommendations
4. Collections
5. Explore Topics
6. Graph

Graph 第一版要求：

- 不做复杂力导向图。
- 先做关系列表：
  - from node title
  - to node title
  - label
  - reason
  - source
  - weight

失败策略：

- Context 加载失败不能影响详情正文。
- 详情正文正常显示，Context 区域显示轻量错误态和重试按钮。

## 5. 工程实施顺序

### Step 1：修正 DTO 合同

负责人建议：数据层负责人

任务：

- 拆分或整理 `core/network/dto`。
- 修正 Search、Timeline、Explore、LearningPath、Region、Collection、Context DTO。
- 修正 `HeritageQueries.kt` 强类型枚举。
- 修正 API client path segment 编码。
- 重写 DTO serialization tests。
- 重写 Ktor API client tests。

验收：

```bash
cd /Users/kaisun/Documents/Github/heritage-online-android/modern-android
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

### Step 2：发现页壳

负责人建议：主 UI 负责人

任务：

- 新增 `Discovery` bottom nav。
- 新增 `feature/discovery`。
- 接入发现页首屏数据。
- 做 loading/error/empty/success。
- 完成中英文文案。

验收：

- 发现页能从底部导航打开。
- 首屏至少展示 topic、learning path、collection、region atlas 中的两个成功区块。
- 后端关闭时能显示错误态。
- 暗色模式无明显对比问题。

### Step 3：搜索页

负责人建议：搜索功能负责人

任务：

- 新增 `feature/search`。
- 接 suggestions。
- 接 searchV2。
- 展示 facets。
- 支持混合结果跳转。

验收：

- 空关键词不请求 searchV2。
- 输入关键词后展示结果。
- facets 可点击并刷新。
- 三类结果跳转正确。

### Step 4：Explore + Learning

负责人建议：内容探索负责人

任务：

- 新增 topic detail。
- 新增 learning path detail。
- 完成 sections、steps、featuredItems、relatedTopics 展示。

验收：

- 四类 learning path 都能打开。
- topic detail 的 sections 不丢字段。
- items 点击跳转正确。

### Step 5：Timeline

负责人建议：列表/分页负责人

任务：

- 新增 timeline 页面。
- 接 timelineYears。
- 接 timelineV2。
- 实现手动分页。
- 实现筛选。

验收：

- 年份总数和分类数量显示正确。
- 年份切换不串数据。
- 筛选变化后重新请求第一页。

### Step 6：Region Atlas + Collections

负责人建议：可视化/聚合内容负责人

任务：

- 新增 region atlas 首页和详情。
- 新增 collection detail。
- 混合内容跳转复用统一 router。

验收：

- Region atlas totals、topCategories、topKinds 都展示。
- Region detail 七个区块都能展示或优雅为空。
- Collection title/subtitle/type/tags/generatedAt 展示正确。

### Step 7：详情页 Context

负责人建议：详情页负责人

任务：

- 三类详情页底部接 Context。
- 实现 related/recommendations/collections/exploreTopics/graph 列表。
- Context 局部错误不影响正文。

验收：

- 三类详情页正文不受 Context 失败影响。
- Context 成功时所有非空区块展示。
- Graph 用列表表达关系。

## 6. 统一 UI 与设计要求

### 6.1 Material 3 规范

- 继续使用现有 Material 3 体系。
- 页面背景继续使用 `HeritagePageBackground`。
- 内容容器继续优先使用 `HeritageContentCard`。
- 不做卡片套卡片。
- 不做营销式大 hero。
- 不使用大面积单色背景。
- 所有颜色优先来自 `MaterialTheme.colorScheme`。

### 6.2 信息密度

这是学习/资料型 App，不是品牌官网。

要求：

- 首页和发现页应信息密度适中。
- 卡片要能快速扫描。
- 长标题最多 2 行。
- 元信息用 chips 或紧凑行展示。
- 列表项点击区域要足够大。

### 6.3 图片

- 图片统一使用现有 Coil / image loader。
- 图片失败时显示当前体系里的占位色。
- 有图内容不强制撑大卡片。
- `image-gallery` collection 可以后续单独做瀑布流，本轮先普通网格。

### 6.4 暗色与多语言

每个新页面必须检查：

- 中文浅色
- 中文暗色
- 英文浅色
- 英文暗色

所有新增文案必须进入：

- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-en/strings.xml`

不能在 Compose 里硬编码 UI 文案，除非是后端返回的内容。

## 7. 不做事项

本轮不要做：

- Search v3。
- Maintenance endpoints。
- 后端 admin/crawl/data-quality 页面。
- 新 API Room 缓存。
- 地图 SVG。
- 复杂图谱力导向动画。
- CI 接入。

## 8. 测试与验收清单

### 8.1 数据层

必须通过：

```bash
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

必须覆盖：

- DTO 真实后端 JSON。
- API client endpoint。
- API client query 参数。
- 中文 path segment。
- ViewModel 成功态。
- ViewModel 失败态。
- ViewModel 空数据态。
- 快速切换状态不串数据。

### 8.2 仪器测试

建议逐步增加：

- 发现页 smoke test。
- 搜索页 smoke test。
- Topic detail smoke test。
- Region atlas smoke test。
- Collection detail smoke test。

### 8.3 手动模拟器验收

每个阶段至少检查：

- 后端运行时正常加载。
- 后端停止时错误态正常。
- Retry 可恢复。
- 浅色中文。
- 暗色中文。
- 浅色英文。
- 暗色英文。
- 长标题不溢出。
- 图片失败不破坏布局。

## 9. 推荐分工

### 数据层负责人

负责：

- DTO 合同修正。
- Query 强类型。
- API client path 编码。
- Repository fake/test fake 同步。
- 数据层测试。

### 主导航负责人

负责：

- Discovery bottom nav。
- Discovery navigation host。
- 新页面返回栈行为。
- 与文章/名录/传承人详情跳转打通。

### 搜索负责人

负责：

- Search screen。
- Suggestions。
- Search v2。
- Facets。
- 混合结果跳转。

### 探索负责人

负责：

- Explore topic detail。
- Learning path detail。
- Topic/learning 相关 UI 组件。

### 聚合内容负责人

负责：

- Timeline。
- Region atlas。
- Collections。

### 详情页负责人

负责：

- Article detail context。
- Directory detail context。
- Inheritor detail context。
- Graph 关系列表。

## 10. 建议里程碑

### Milestone 1：数据合同可信

完成：

- DTO 全部对齐。
- 测试 JSON 全部换成真实后端 shape。
- 构建通过。

这是所有 UI 开发的前置条件。

### Milestone 2：发现页可打开

完成：

- 底部导航有发现。
- 发现页首屏展示真实数据。
- 基础状态完整。

### Milestone 3：搜索可用

完成：

- 搜索建议。
- 搜索结果。
- facets 筛选。
- 结果跳详情。

### Milestone 4：探索内容可用

完成：

- Topic detail。
- Learning path detail。

### Milestone 5：聚合视图可用

完成：

- Timeline。
- Region atlas。
- Collections。

### Milestone 6：详情页上下文增强

完成：

- 三类详情页 context 区域。
- 推荐、关联、合集、探索主题、图谱关系列表。
