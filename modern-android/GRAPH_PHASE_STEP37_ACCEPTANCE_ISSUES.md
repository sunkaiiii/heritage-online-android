# 图谱阶段步骤 37 测试与人工验收问题记录

日期：2026-06-26  
范围：`MODERN-ANDROID-API-EXPANSION-PLAN.md` 步骤 37  
边界：原始记录只做测试与人工验收；后续已针对记录的问题完成代码修复。

## 1. 验收命令与结果

### 单元测试

```bash
./gradlew --no-daemon :app:testDebugUnitTest \
  --tests '*Graph*' \
  --tests '*KnowledgeGraphRepositoryTest*' \
  --tests '*AdvancedDtoSerializationTest*'
```

结果：通过。

覆盖到的步骤 37 重点：

- graph DTO decode / advanced DTO decode。
- `KnowledgeGraphRepositoryTest`。
- `GraphExploreViewModelTest`。
- `GraphModelMappingTest`。
- `GraphTrailViewModelTest`、`TopicGraphMapViewModelTest` 等图谱阶段相关 ViewModel 测试。

### 模拟器 Compose/UI 测试

设备：

```text
emulator-5554 / Pixel_10_Pro(AVD) / Android 17
```

命令：

```bash
./gradlew --no-daemon :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.duckylife.heritage.modern.feature.discovery.graphexplore.GraphExploreRouteTest
```

结果：通过，`GraphExploreRouteTest` 16/16。

覆盖到的步骤 37 重点：

- 四个 GraphExplore tab 的基本渲染与切换。
- Similar 空态引导 Explore。
- AI inferred toggle。
- path bottom sheet。
- Canvas/Overview 回退相关 UI。

### 真实后端 API 探测

默认 debug baseUrl：

```text
https://tuantuan.myds.me:28887
```

本机模拟器开发地址 `https://localhost:5078` / `https://127.0.0.1:5078` 当前不可达；公开 HTTPS 后端可达。

真实后端 API 检查结果：

| 类型 | 样本 | neighbors | similar | explore | evidence |
| --- | --- | --- | --- | --- | --- |
| article | `"女制陶男莫近" ――访广西黎族原始制陶技艺传承人` / `6a23b224344532f474d3f719` | 200 | 200 | 200 | 200 |
| directoryItem | `一勾勾` / `6a240b5bce67593793a1e9d7` | 200 | 200 | 200 | 200 |
| inheritor | `丁凡` / `6a244cd1ce67593793a1fd6d` | 200 | 200 | 200 | 200 |
| topic map | `category/传统戏剧` | 200 | - | - | - |

说明：名录图谱路径必须使用 `directoryItem`。使用 `directory` 会返回 400，但客户端当前 `SearchResultType.DirectoryItem.wireName` 是 `directoryItem`，所以这不是客户端 bug。

## 2. 人工验收通过项

### 2.1 发现页与图谱 Hub

- App 能在模拟器上启动并加载公开 HTTPS 后端数据。
- Discover 页能显示真实内容。
- “知识图谱”入口可见并可进入。
- Knowledge Graph Hub 能加载 communities。
- Hub 中能看到英文 key 和中文 key，例如：
  - `specialTopic`
  - `nationalProject`
  - `传统戏剧`
  - `传统技艺`

### 2.2 中文 topic key

点击 `传统戏剧` community 后，Topic graph 页面成功打开：

```text
Topic graph
传统戏剧
category
51 nodes · 50 relations
```

结论：中文 topic key 的 path segment 编码在真实后端上可用。

### 2.3 Canvas/Overview 大图回退

在 `传统戏剧` Topic graph 中切换 Overview 后，页面显示：

```text
Many relationships found; using list view for readability
The overview graph is hidden to avoid an unreadable dense graph.
```

结论：真实大图超过阈值时能回退到说明，不会强行绘制过密图。

### 2.4 详情页基础可用性

至少以下真实内容详情页可打开并阅读：

- 文章：`海韵岱山・非遗潮生——2026浙江非遗嘉年华岱山专场暨岱山文旅推介会在浙江省非物质文化遗产馆启幕`
- 名录：`三多节`
- 传承人：`丁义江`

详情页主体文字、收藏/刷新按钮、相关内容区块仍可显示。

## 3. 发现的问题

### P1：详情页图谱入口缺失或不可见

现象：

- 步骤 37 人工验收要求用文章、名录、传承人检查真实后端图谱阶段。
- 在文章、名录、传承人详情页向下滚动到 `Keep exploring` / 推荐 / 连接线索区域后，没有看到明确的 `Content relationships` / `关系图谱` / GraphExplore 入口按钮。
- 传承人详情 `Keep exploring` 附近仅看到：
  - `Quick Read`
  - `Key Facts`
  - `Recommended`
  - 推荐内容卡片
- 未观察到可直接进入 GraphExplore 四 tab 页的入口。

影响：

- 用户从内容详情页无法自然进入步骤 33-35 的 GraphExplore 页面。
- 即使后端 graph endpoints 对 article / directoryItem / inheritor 都返回 200，真实用户路径仍可能断在入口层。
- 步骤 37 “用至少一条文章、一条名录、一位传承人检查真实后端”的 UI 验收无法完整覆盖 GraphExplore 页面。

建议后续修复方向：

- 检查 Article / Directory / Inheritor 三类详情页的继续探索区块是否应该渲染图谱入口。
- 若入口依赖 V3 intelligence 成功加载，需要确认 intelligence 失败时是否仍应展示基础图谱入口。
- 增加详情页到 GraphExplore 的仪器化测试，覆盖三类内容。

### P1：连接线索中暴露 raw relation key

现象：

在真实文章、名录、传承人详情页的 `Connection clues` 区域，能看到后端/枚举样式 key，例如：

```text
relatedArticle
relatedInheritor
semanticSimilarity
```

同时页面也会显示用户友好的中文关系文案，例如：

```text
相关阅读
语义相似
```

影响：

- 用户会看到实现细节或后端字段名。
- 英文 UI 下夹杂 camelCase key，中文内容下也夹杂英文 key，体验不完整。
- 这和步骤 37 对图谱阶段人工验收的“真实用户可读”目标不一致。

建议后续修复方向：

- 详情页连接线索区块不要直接显示 relation code。
- 使用与 GraphExplore 一致的 relation formatter / string resource。
- 如需要展示来源类型，应显示 `Source: explicit / inferred / embedding` 这类来源标签，而不是 relation code。

### P2：Topic graph 内容列表节点点击无响应

现象：

- 在 `传统戏剧` Topic graph 的 List 视图中，列表项显示内容节点：
  - `一勾勾`
  - `丁凡`
  - `丁守艮`
  - `丁振耀`
- 多次点击可见列表项正文区域，没有进入对应详情页，也没有视觉反馈。

代码观察：

- `TopicGraphMapScreen.kt` 中 `TopicNodeRow` 看起来通过 `HeritageContentCard(onClick = ...)` 支持内容节点点击。
- 实际模拟器验收中点击未触发跳转。

影响：

- Topic graph 能展示中文主题下的内容，但用户无法从主题图谱继续进入内容详情。
- 对“主题图谱地图”作为探索入口的可用性有影响。

建议后续修复方向：

- 检查 `GraphNodeUiModel.isContentNode` / `toDiscoveryItemDto()` 对 `directoryItem`、`inheritor` 的转换是否完整。
- 检查 Topic graph route 的 `onItemClick` 是否正确入栈。
- 为 Topic graph 列表项点击增加 Compose/UI 测试。

### P2：传承人详情真实后端出现“智能解读加载失败”

现象：

打开传承人 `丁义江` 详情后，页面显示：

```text
智能解读加载失败，请稍后重试
重试
```

同时页面正文、相关项目、推荐、连接线索仍可显示。

影响：

- 不阻塞详情页基础阅读。
- 但会影响详情页上半部分的 V3 intelligence 展示质量。
- 如果图谱入口依赖 intelligence section 成功加载，可能间接导致图谱入口不可见。

补充确认（2026-06-26）：

用户截图中的失败卡片位于 `Related inheritors` 与 `Keep exploring` 之间，对应传承人详情页的 `DetailIntelligenceSection`，不是相关传承人推荐列表本身。

已确认用户提供的 blended recommendations API 可以返回 HTTP 200 JSON：

```text
GET https://tuantuan.myds.me:28887/api/recommendations/blended/inheritor/6a242cb9ce67593793a1f64e?limit=10&ruleWeight=1.0&semanticWeight=1.0&sameCategoryWeight=1.0&sameRegionWeight=1.0&diversify=true
```

因此截图中的“智能解读加载失败”不应归因于 `blendedRecommendations` 接口不可用。页面上方已经展示了多条 `Related inheritors` 项，也侧面说明推荐数据链路本身不是这张失败卡片的直接来源。

进一步检查真实后端 V3 页面接口后，问题更像是 Android 客户端 DTO 与后端当前 JSON 契约不一致导致的反序列化失败：

- 客户端 `ContentIntelligenceRepository.loadContentPage()` 请求的是 `/api/v3/pages/inheritor/{id}`。
- 该接口在 `includeLocalState=true` 时需要 `profileId`，否则会返回 400：`Valid profileId is required when includeLocalState=true`。
- 带 `profileId` 后接口可以返回 200，并且 `sectionStatus` 中 `digest`、`graph`、`recommendations`、`relatedContent`、`localState` 为 ready，`aiCard` 为 missing。
- 后端返回的 `digest.keyFacts` 是对象数组，例如 `{ "label": "...", "value": "..." }`，但客户端 `ContentDigestSectionDto.keyFacts` 当前按 `List<String>` 解析。
- 后端返回的 `warnings` 也是对象数组，例如 `{ "code": "...", "message": "...", "severity": "..." }`，但客户端 `V3ContentPageDto.warnings` 当前按 `List<String>` 解析。

判断：

- 这是程序问题，不是“本应如此”的空状态。
- AI card missing 本身可以是后端正常状态，但客户端不应把整块智能解读渲染成加载失败。
- 如果 V3 页面部分 section 可用、仅 `aiCard` 缺失，预期 UI 应降级为“暂无智能解读”/隐藏 AI 卡片，或继续展示 digest、graph、recommendations 等可用部分，而不是显示 fatal error。

建议后续修复方向：

- 对齐 `ContentDigestSectionDto.keyFacts`、`V3ContentPageDto.warnings` 与后端真实响应契约，或增加兼容旧/新 shape 的容错 serializer。
- 确认 `/api/v3/pages/{type}/{id}` 在 `includeLocalState=true` 时是否必须传 `profileId`；若必须，客户端应在 `V3ContentPageQuery` 中稳定传入当前 profileId，或在无 profileId 时关闭 local state 请求。
- 将 `aiCard.status=missing` 视为 section-level missing，而不是整页 intelligence load failure。
- 增加使用真实 V3 payload shape 的回归测试，覆盖 `digest.keyFacts` 对象数组、`warnings` 对象数组、`aiCard missing`、其他 section ready 的组合。
- 确认 intelligence 失败时图谱入口是否仍应独立显示。

### P3：无法完成“关闭 Neo4j 后重试”的严格验收

现象：

- 本机 `https://localhost:5078` / `https://127.0.0.1:5078` 当前没有后端服务。
- 本轮验收使用公开 HTTPS 后端 `https://tuantuan.myds.me:28887`。
- 无法安全关闭公开后端的 Neo4j，也没有本地 Neo4j/后端进程可控。

影响：

- 步骤 37 中“关闭 Neo4j 后重试，确认详情页和发现页仍可使用”没有完成严格人工验证。
- 当前只能确认：公开后端正常时详情页和发现页可用；不能确认 Neo4j outage 下的真实端到端行为。

建议后续验收方式：

1. 启动本地后端并配置模拟器使用：

   ```bash
   ./gradlew :app:assembleDebug -PheritageApiBaseUrl=https://10.0.2.2:5078
   ```

2. 在本地关闭/断开 Neo4j。
3. 重新打开 Discover、文章详情、名录详情、传承人详情。
4. 记录图谱页是否显示可重试错误，非图谱详情是否继续可读。

## 4. 非问题/已澄清项

### 名录图谱 API 400 初始误报

最初手动 probe 使用了：

```text
/api/knowledge-graph/directory/{id}/...
```

该路径返回 400。

复核客户端和 DTO 后确认正确 content type 是：

```text
directoryItem
```

使用：

```text
/api/knowledge-graph/directoryItem/{id}/...
```

后，neighbors / similar / explore / evidence 均返回 200。

因此这不是客户端问题。

## 5. 建议补充的测试

1. 详情页图谱入口测试：
   - Article detail 显示 GraphExplore 入口。
   - Directory detail 显示 GraphExplore 入口。
   - Inheritor detail 显示 GraphExplore 入口。
   - intelligence 加载失败时入口仍可用。

2. Topic graph 点击测试：
   - 点击 `directoryItem` 节点进入名录详情。
   - 点击 `inheritor` 节点进入传承人详情。
   - 点击 `article` 节点进入文章详情。

3. 连接线索文案测试：
   - 不显示 `relatedArticle` / `relatedInheritor` / `semanticSimilarity` 等 raw key。
   - 中文 locale 显示中文关系文案。
   - 英文 locale 显示英文关系文案。

4. Neo4j outage 端到端测试：
   - 图谱端点 503 时 GraphExplore 显示可重试错误。
   - 详情页正文、推荐兜底、Discover 非图谱区块仍可用。

## 6. 问题修复记录

针对第 3 节记录的问题，已完成以下代码修复：

### P1：详情页图谱入口缺失或不可见

- 修复 `V3ContentPageDto` 与后端真实契约不一致导致的反序列化失败（见 P2 修复），使 `DetailContinueExploreSection` 能正常显示“关系图谱 / 相似内容 / 学习路线”入口。
- 在 `ArticlesNavHost`、`DirectoryRoute`、`InheritorsRoute` 中补齐 `onGraphExploreClick`、`onSimilarClick`、`onLearningRoutesClick` 的跨 Tab 导航：
  - 点击“关系图谱”→ 跳转至 Discovery 的 `GraphExplorePage(initialTab = Neighbors)`。
  - 点击“相似内容”→ 跳转至 Discovery 的 `GraphExplorePage(initialTab = Similar)`。
  - 点击“学习路线”→ 跳转至 Discovery 的 `LearningRoutesPage(seedType = "content", seedId = "{type}:{id}")`。
- 扩展 `MyPageDestination.GraphExplore` 支持 `initialTabName`，并新增 `MyPageDestination.LearningRoutes`，由 `MainActivity` 统一分发到 Discovery tab。

### P1：连接线索中暴露 raw relation key

- 在 `ui/text/ContentLabels.kt` 新增 `localizedRelationLabel(relationType, label)`，同时覆盖 V3 图谱枚举 wire name（`RELATED_TO` 等）与旧版 Context API 业务关系码（`relatedArticle`、`semanticSimilarity`、`sameCategory` 等）。
- 在 `ui/component/DetailContextSection.kt` 的 `GraphEdgeRow` 中使用该映射，优先显示本地化关系文案，未知时回退到后端 label。
- 新增中/英文 string resource：`graph_relation_related_article`、`_related_directory_item`、`_related_inheritor`、`_semantic_similarity`、`_same_category`、`_same_region`、`_same_topic`。
- 在 `GraphRelationFormatter` 中新增字符串 overload `labelResId(rawRelationType)`，并补充 `GraphRelationFormatterTest` 断言。

### P2：Topic graph 内容列表节点点击无响应

- 后端 `TopicGraphMap` 与图谱接口的节点常以 `{type}:{id}` 形式放在 `nodeKey` 中，而单独 `id` 字段可能缺失。
- 在 `feature/graph/model/GraphModelMapping.kt` 的 `toGraphNodeUiModel()` 中，当 `id` 为空时从 `nodeKey` 提取有效 ID，使内容节点（article / directoryItem / inheritor）和主题节点（category 等）都能被正确识别与导航。
- 同步更新 `ContentIntelligenceRepository.toDetailGraphNode()`，保证 V3 图谱映射到旧版 `DetailContextDto` 时同样能拿到可导航 ID。
- 补充 `GraphModelMappingTest`：验证从 `nodeKey` 推导 content id、topic key，以及显式 `id` 优先。

### P2：传承人详情真实后端出现“智能解读加载失败”

- 修正 `AiProductDtos.kt`：
  - `ContentDigestSectionDto.keyFacts` 由 `List<String>` 改为 `List<ContentDigestKeyFactDto>`（`label` / `value`）。
  - `V3ContentPageDto.warnings` 由 `List<String>` 改为 `List<V3PageWarningDto>`（`code` / `message` / `severity`）。
- 更新 `ContentIntelligenceRepository.toDetailDigest()`，将对象 keyFacts 正确映射为 `DigestFactDto(label, value)`；`warnings` 映射为 message 列表保持 UI 不变。
- 新增 `AdvancedDtoSerializationTest` 回归测试：覆盖真实后端返回的 object keyFacts 与 object warnings。
- 新增 `ContentIntelligenceRepositoryTest`：验证 keyFacts label/value 映射和 warning object → message 映射。
- `aiCard.status=missing` 本身已是 section-level 状态；修复 DTO 后，整页 intelligence 不再因反序列化失败而降级为 Unavailable，其他 ready section 可正常展示。

### P3：无法完成“关闭 Neo4j 后重试”的严格验收

- 仍需在可控本地后端环境下完成 Neo4j outage 端到端验证；当前代码已按 `ContentIntelligenceRepository` 的 503 降级逻辑和图谱 tab 级错误处理实现，等待本地环境就绪后复验。

## 7. 修复后验证

- `./gradlew :app:testDebugUnitTest --tests '*AdvancedDtoSerializationTest*' --tests '*ContentIntelligenceRepositoryTest*' --tests '*GraphModelMappingTest*' --tests '*GraphRelationFormatterTest*'` 通过。
- `./gradlew :app:verifyLocal` 通过（含单元测试与 Debug APK 构建）。
- 人工真实后端验收仍待复验：文章/名录/传承人详情页图谱入口可见且可跳转、Topic graph 内容节点可点击、连接线索不显示 raw key。

## 8. 当前结论

步骤 37 的自动化测试部分通过；真实后端基础链路可用，中文 topic key 和大图回退表现正常。

原始人工验收发现的 4 个代码问题（P1–P2）已完成修复并通过本地单元测试与 `verifyLocal`：

1. 详情页 GraphExplore 入口已跨 Tab 导航到 Discovery。
2. 详情页连接线索的 raw relation key 已通过本地化映射消除。
3. Topic graph 内容节点点击已从 `nodeKey` 推导 ID 并恢复导航。
4. 传承人详情 intelligence 因 `keyFacts` / `warnings` DTO 形状不匹配导致的失败已修复。

剩余 Neo4j outage 端到端验证仍依赖可控本地后端环境，待后续复验。
