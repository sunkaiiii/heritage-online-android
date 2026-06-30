# E迹 Android 主导航重构规格文档

## 0. 文档目的

本文档用于指导 AI 或开发者分步骤完成一次信息架构与首页底部导航重构。

本次重构的核心目标：

1. 将“我的学习空间”从 `文章 -> 设置 -> 收藏与最近浏览` 的深层入口提升为底部主导航 Tab。
2. 将现有底部 Tab `名录` 与 `传承人` 合并为一个新的底部 Tab：`非遗库`。
3. `非遗库` 内部用子页面承载 `名录项目` 与 `代表性传承人` 两类资料。
4. 设置页不再承担“用户学习资产入口”的职责，设置入口改为从 `我的` 页面进入。
5. 通过单元测试、Compose UI 测试、真机视觉验收保证导航路径、页面状态、返回行为、无障碍语义和布局都正确。

本文档只描述 Android `modern-android` 工程的改造。

## 1. 当前问题

### 1.1 当前用户路径过深

当前“我的学习空间”的入口路径是：

```text
底部导航：文章
  -> 文章页右上角设置图标
    -> 设置页底部“收藏与最近浏览”
      -> 我的学习空间
```

这个路径有三个问题：

- “我的学习空间”是用户资产页，不是设置项。
- 入口只出现在文章 Tab，用户在名录、传承人、发现页时很难意识到还有这个页面。
- 用户要先理解“设置里有收藏与最近浏览”，才能找到收藏、浏览历史、学习进度、旅程和资料。

### 1.2 当前底部导航实体拆分过细

当前底部导航是：

```text
文章 / 名录 / 传承人 / 发现
```

`名录` 与 `传承人` 都属于非遗资料库实体：

- 名录项目：非遗项目、生态区、保护基地、UNESCO 等。
- 代表性传承人：人物实体，与名录项目强相关。

从用户心智看，这两者更适合作为同一个 `非遗库` 入口下的两个子页面，而不是占用两个底部主导航位置。

### 1.3 当前代码结构定位

当前主导航集中在：

```text
modern-android/app/src/main/java/com/duckylife/heritage/modern/MainActivity.kt
```

当前 `HomeDestination`：

```kotlin
private enum class HomeDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Articles(R.string.nav_articles, Icons.AutoMirrored.Outlined.Article),
    Directory(R.string.nav_directory, Icons.Outlined.CollectionsBookmark),
    Inheritors(R.string.nav_inheritors, Icons.Outlined.Groups),
    Discovery(R.string.nav_discovery, Icons.Outlined.Explore),
}
```

当前“我的学习空间”不是主导航目的地，而是由这两个状态控制：

```kotlin
var showSettings by rememberSaveable { mutableStateOf(false) }
var showMyPage by rememberSaveable { mutableStateOf(false) }
```

当前入口在：

```text
modern-android/app/src/main/java/com/duckylife/heritage/modern/feature/settings/SettingsScreen.kt
```

对应 UI 文案：

```xml
<string name="my_page_entry">收藏与最近浏览</string>
```

当前“我的学习空间”页面在：

```text
modern-android/app/src/main/java/com/duckylife/heritage/modern/feature/my/MyPage.kt
```

当前页面内部 Tab：

```text
收藏 / 浏览 / 学习 / 旅程 / 资料
```

## 2. 目标信息架构

### 2.1 底部主导航

改造后的底部主导航必须是：

```text
文章 / 非遗库 / 发现 / 我的
```

四个 Tab 的职责：

| Tab | 页面职责 | 首屏重点 |
| --- | --- | --- |
| 文章 | 新闻、论坛、专题、文章详情、文章搜索 | 最新文章、横幅、分类 |
| 非遗库 | 名录项目与代表性传承人 | 子页切换、资料检索、筛选 |
| 发现 | 数据探索、图谱、学习路线、时间线、排行榜 | 探索入口、学习入口 |
| 我的 | 收藏、最近浏览、学习进度、旅程、研究资料、设置入口 | 用户学习档案 |

### 2.2 非遗库内部结构

`非遗库` 是底部主导航的第二个 Tab。

进入 `非遗库` 后，页面顶部必须有一个清晰的子页面切换控件：

```text
[ 名录项目 ] [ 代表性传承人 ]
```

默认选中：

```text
名录项目
```

子页面职责：

| 子页面 | 对应现有页面 | 保留能力 |
| --- | --- | --- |
| 名录项目 | 当前 `DirectoryRoute` | 名录列表、种类筛选、搜索、统计、筛选 sheet、名录详情 |
| 代表性传承人 | 当前 `InheritorsRoute` | 传承人列表、搜索、筛选 sheet、传承人详情 |

### 2.3 我的学习空间结构

`我的` 是底部主导航的第四个 Tab。

进入 `我的` 后直接展示当前 `MyPage` 内容，不再需要从设置页进入。

页面顶部推荐结构：

```text
我的学习空间                         [同步] [设置]
本设备学习档案
收藏 0 · 浏览 2 · 学习路线 0
已同步

[ 收藏 ] [ 浏览 ] [ 学习 ] [ 旅程 ] [ 资料 ]
```

设计要求：

- 作为底部主导航根页面时，不显示返回箭头。
- 右上角提供设置入口，图标使用 `Icons.Outlined.Settings`。
- 同步按钮继续保留，图标使用当前刷新/同步图标。
- 五个内部 Tab 必须在中文环境下完整显示，不允许只露出“资料”的一部分。
- 设置页返回后必须回到 `我的` Tab。

## 3. UI 设计规范

### 3.1 底部导航设计

底部导航顺序：

```text
文章 -> 非遗库 -> 发现 -> 我的
```

建议图标：

| Tab | 图标 | 说明 |
| --- | --- | --- |
| 文章 | `Icons.AutoMirrored.Outlined.Article` | 继续使用当前文章图标 |
| 非遗库 | `Icons.Outlined.CollectionsBookmark` 或 `Icons.Outlined.Inventory2` | 表示资料库、收藏册、目录 |
| 发现 | `Icons.Outlined.Explore` | 继续使用当前发现图标 |
| 我的 | `Icons.Outlined.AccountCircle` | 表示个人空间 |

底部导航文案：

```xml
<string name="nav_articles">文章</string>
<string name="nav_library">非遗库</string>
<string name="nav_discovery">发现</string>
<string name="nav_my">我的</string>
```

英文文案：

```xml
<string name="nav_articles">Articles</string>
<string name="nav_library">Library</string>
<string name="nav_discovery">Discover</string>
<string name="nav_my">My</string>
```

底部导航视觉要求：

- 四个 Tab 等宽。
- 图标与文字必须都可见。
- 选中态沿用当前 `NavigationBarItemDefaults.colors`。
- 底部导航只在主页面根层级显示。
- 进入文章详情、名录详情、传承人详情、发现深层页、设置页时隐藏底部导航。
- 从 `我的` 的收藏/浏览记录跳转到内容详情时，也要隐藏底部导航。

### 3.2 非遗库页面布局

`非遗库` 根页面不需要再额外显示一个大标题“非遗库”，避免与子页面标题重复。

推荐首屏布局：

```text
[ 名录项目 ] [ 代表性传承人 ]

非遗名录                         [筛选] [刷新]
项目、生态区、保护基地与 UNESCO 名录

[ 名录 ] [ 统计 ]

搜索名录
[ 国家级项目 ] [ 文化生态区 ] [ 保护基地 ] [ UNESCO ] ...

列表内容...
```

切换到传承人子页后：

```text
[ 名录项目 ] [ 代表性传承人 ]

代表性传承人                     [筛选] [刷新]
按项目、类别与地区浏览

搜索传承人

列表内容...
```

具体规范：

- 子页切换控件放在每个子页面 `LazyColumn` 的第一项。
- 子页切换控件应该随内容滚动，不要固定吸顶。
- 子页切换控件使用 `PrimaryTabRow` 或同等视觉的固定双 Tab。
- 双 Tab 文案必须完整显示。
- 选中态使用主题色下划线或 `primaryContainer` 背景，保持与当前 `DirectoryTabToggle` 风格一致。
- 子页切换控件下方间距为 `12.dp` 到 `16.dp`。
- 保留原有 `DirectoryHeader` 与 `InheritorsHeader`，因为它们包含当前页面的筛选和刷新操作。
- 不要在子页切换上方再加一个大标题，避免首屏过高。

### 3.3 非遗库子页状态保存

必须分别保存两个子页的状态：

- 用户在 `名录项目` 中选了 `统计` Tab，切到 `代表性传承人` 再回来后，应仍停在 `统计`。
- 用户在 `名录项目` 中输入搜索词，切走再回来，搜索词不应丢失。
- 用户在 `代表性传承人` 中输入搜索词或筛选条件，切走再回来，不应丢失。
- 用户在任一子页进入详情时，底部导航隐藏；点返回后回到该子页原列表。

实现时不要把两个子页共用一个 back stack。

### 3.4 我的页面布局

`我的` 根页面推荐首屏布局：

```text
我的学习空间                         [同步] [设置]

本设备学习档案
收藏 0 · 浏览 2 · 学习路线 0
已同步

[ 收藏 ] [ 浏览 ] [ 学习 ] [ 旅程 ] [ 资料 ]

当前 Tab 内容...
```

具体规范：

- 根页面不显示返回箭头。
- 如果 `MyPage` 仍需要支持从其他地方以覆盖页方式打开，可以添加参数 `showBackButton: Boolean = false`，但底部主导航场景必须传 `false`。
- 设置图标必须有 content description：`设置` / `Settings`。
- 同步按钮必须保留 content description：`同步` / `Sync`。
- 个人档案区保持当前结构，但要控制高度，不要压缩内部 Tab。
- 内部五个 Tab 建议改成固定等宽 Tab，而不是当前容易露出半个 Tab 的 `SecondaryScrollableTabRow`。

推荐内部 Tab 方案：

```kotlin
PrimaryTabRow(
    selectedTabIndex = selectedTabIndex,
    modifier = Modifier.fillMaxWidth(),
) {
    MyPageTab.entries.forEachIndexed { index, tab ->
        Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = {
                Text(
                    text = stringResource(tab.titleRes),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
    }
}
```

中文文案保持：

```text
收藏 / 浏览 / 学习 / 旅程 / 资料
```

英文建议改短，避免固定五等分时溢出：

```text
Saved / Recent / Learn / Journey / Research
```

对应英文资源建议：

```xml
<string name="favorites_tab">Saved</string>
<string name="browsing_tab">Recent</string>
<string name="learning_tab">Learn</string>
<string name="journeys_tab">Journey</string>
<string name="research_tab">Research</string>
```

### 3.5 设置页布局

设置页只保留设置相关内容：

```text
设置

外观
主题模式

语言
应用语言
```

移除设置页底部的：

```text
收藏与最近浏览
```

原因：

- `收藏与最近浏览` 已成为 `我的` 主导航。
- 设置页不应再承载用户内容资产入口。
- 保留重复入口会让用户困惑：到底应该从设置进，还是从底部 `我的` 进。

设置页入口改为：

```text
底部导航：我的 -> 右上角设置图标
```

## 4. 代码改造步骤

### Step 1：新增与调整字符串资源

修改：

```text
modern-android/app/src/main/res/values/strings.xml
modern-android/app/src/main/res/values-en/strings.xml
```

新增中文：

```xml
<string name="nav_library">非遗库</string>
<string name="nav_my">我的</string>
<string name="library_tab_directory">名录项目</string>
<string name="library_tab_inheritors">代表性传承人</string>
<string name="my_page_settings">设置</string>
```

新增英文：

```xml
<string name="nav_library">Library</string>
<string name="nav_my">My</string>
<string name="library_tab_directory">Directory</string>
<string name="library_tab_inheritors">Inheritors</string>
<string name="my_page_settings">Settings</string>
```

调整英文 My 内部 Tab：

```xml
<string name="favorites_tab">Saved</string>
<string name="browsing_tab">Recent</string>
<string name="learning_tab">Learn</string>
<string name="journeys_tab">Journey</string>
<string name="research_tab">Research</string>
```

保留但不再用于主入口：

```xml
<string name="nav_directory">名录</string>
<string name="nav_inheritors">传承人</string>
<string name="my_page_entry">收藏与最近浏览</string>
```

不要立刻删除这些旧字符串，因为测试或旧页面可能仍引用它们。等编译确认无引用后再清理。

### Step 2：调整主导航枚举

修改：

```text
modern-android/app/src/main/java/com/duckylife/heritage/modern/MainActivity.kt
```

目标 `HomeDestination`：

```kotlin
private enum class HomeDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Articles(R.string.nav_articles, Icons.AutoMirrored.Outlined.Article),
    Library(R.string.nav_library, Icons.Outlined.CollectionsBookmark),
    Discovery(R.string.nav_discovery, Icons.Outlined.Explore),
    My(R.string.nav_my, Icons.Outlined.AccountCircle),
}
```

需要新增 import：

```kotlin
import androidx.compose.material.icons.outlined.AccountCircle
```

需要删除或停止使用：

```kotlin
HomeDestination.Directory
HomeDestination.Inheritors
```

### Step 3：移除 `showMyPage` 覆盖式入口

当前：

```kotlin
var showMyPage by rememberSaveable { mutableStateOf(false) }
```

目标：

- 删除 `showMyPage` 状态。
- `MyPage` 改为 `HomeDestination.My` 的正常主导航页面。
- `showSettings` 保留，因为设置页仍是临时全屏页面。

注意：

- 删除 `showMyPage` 后，`shouldShowBottomBar` 不再依赖它。
- `selectedDestinationInDetail` 需要覆盖新目的地 `Library` 与 `My`。

推荐结构：

```kotlin
var selectedDestination by rememberSaveable { mutableStateOf(HomeDestination.Articles) }
var showSettings by rememberSaveable { mutableStateOf(false) }
var articlesInDetail by remember { mutableStateOf(false) }
var libraryInDetail by remember { mutableStateOf(false) }
var discoveryInDetail by remember { mutableStateOf(false) }
var myInDetail by remember { mutableStateOf(false) }
var pendingNavigation by remember { mutableStateOf<MyPageDestination?>(null) }
```

`myInDetail` 第一阶段可以始终为 `false`。如果以后 `我的` 页内部的研究资料详情需要隐藏底部导航，再把它接入。

### Step 4：新增 `LibraryRoute`

新增文件：

```text
modern-android/app/src/main/java/com/duckylife/heritage/modern/feature/library/LibraryRoute.kt
```

职责：

- 管理 `名录项目 / 代表性传承人` 子页面选中态。
- 将 pending navigation 分发给 `DirectoryRoute` 或 `InheritorsRoute`。
- 合并两个子页面的 detail 状态，向主导航报告是否隐藏底部导航。
- 为两个子页面注入同一个顶部子页切换控件。

建议数据结构：

```kotlin
private enum class LibrarySection(@StringRes val labelRes: Int) {
    Directory(R.string.library_tab_directory),
    Inheritors(R.string.library_tab_inheritors),
}
```

建议签名：

```kotlin
@Composable
fun LibraryRoute(
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    onKeywordSearch: (String) -> Unit = {},
    onGraphExploreSelected: (contentType: String, contentId: String, initialTabName: String) -> Unit = { _, _, _ -> },
    onLearningRoutesSelected: (seedType: String?, seedId: String?) -> Unit = { _, _ -> },
    pendingNavigation: MyPageDestination? = null,
    onPendingNavigationConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
)
```

核心逻辑：

```kotlin
var selectedSection by rememberSaveable { mutableStateOf(LibrarySection.Directory) }
var directoryInDetail by remember { mutableStateOf(false) }
var inheritorsInDetail by remember { mutableStateOf(false) }

LaunchedEffect(pendingNavigation) {
    when (pendingNavigation) {
        is MyPageDestination.Directory -> selectedSection = LibrarySection.Directory
        is MyPageDestination.Inheritor -> selectedSection = LibrarySection.Inheritors
        else -> Unit
    }
}

LaunchedEffect(selectedSection, directoryInDetail, inheritorsInDetail) {
    onSecondaryDestinationChanged(
        when (selectedSection) {
            LibrarySection.Directory -> directoryInDetail
            LibrarySection.Inheritors -> inheritorsInDetail
        },
    )
}
```

子页切换 UI：

```kotlin
@Composable
private fun LibrarySectionTabs(
    selectedSection: LibrarySection,
    onSectionSelected: (LibrarySection) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrimaryTabRow(
        selectedTabIndex = LibrarySection.entries.indexOf(selectedSection),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        LibrarySection.entries.forEach { section ->
            Tab(
                selected = selectedSection == section,
                onClick = { onSectionSelected(section) },
                text = {
                    Text(
                        text = stringResource(section.labelRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}
```

### Step 5：给名录和传承人列表页增加顶部插槽

为了避免粗暴包裹造成页面重复标题，推荐把 `非遗库` 的子页切换控件注入到现有列表页 `LazyColumn` 的第一项。

修改：

```text
modern-android/app/src/main/java/com/duckylife/heritage/modern/feature/directory/DirectoryScreen.kt
modern-android/app/src/main/java/com/duckylife/heritage/modern/feature/inheritors/InheritorsScreen.kt
```

给 `DirectoryRoute` 增加参数：

```kotlin
listTopContent: (@Composable () -> Unit)? = null,
```

逐层传给：

```kotlin
DirectoryListRoute(...)
DirectoryScreen(...)
```

给 `DirectoryScreen` 增加参数：

```kotlin
listTopContent: (@Composable () -> Unit)? = null,
```

并在 `LazyColumn` 最前方插入：

```kotlin
listTopContent?.let { content ->
    item {
        content()
    }
}
```

传承人同理，给 `InheritorsRoute`、`InheritorsListRoute`、`InheritorsScreen` 增加同名参数。

注意：

- 不要隐藏 `DirectoryHeader` 和 `InheritorsHeader`，因为它们承载筛选和刷新按钮。
- `listTopContent` 只出现在列表根页，不出现在详情页。
- 从详情页返回列表后，`listTopContent` 必须仍然可见。

### Step 6：在 `LibraryRoute` 内组合两个现有路由

伪代码：

```kotlin
when (selectedSection) {
    LibrarySection.Directory -> DirectoryRoute(
        onSecondaryDestinationChanged = { directoryInDetail = it },
        onKeywordSearch = onKeywordSearch,
        onGraphExploreSelected = onGraphExploreSelected,
        onLearningRoutesSelected = onLearningRoutesSelected,
        pendingNavigation = pendingNavigation as? MyPageDestination.Directory,
        onPendingNavigationConsumed = onPendingNavigationConsumed,
        listTopContent = {
            LibrarySectionTabs(
                selectedSection = selectedSection,
                onSectionSelected = { selectedSection = it },
            )
        },
        modifier = modifier,
    )

    LibrarySection.Inheritors -> InheritorsRoute(
        onSecondaryDestinationChanged = { inheritorsInDetail = it },
        onKeywordSearch = onKeywordSearch,
        onGraphExploreSelected = onGraphExploreSelected,
        onLearningRoutesSelected = onLearningRoutesSelected,
        pendingNavigation = pendingNavigation as? MyPageDestination.Inheritor,
        onPendingNavigationConsumed = onPendingNavigationConsumed,
        listTopContent = {
            LibrarySectionTabs(
                selectedSection = selectedSection,
                onSectionSelected = { selectedSection = it },
            )
        },
        modifier = modifier,
    )
}
```

状态保存要求：

- `DirectoryRoute` 与 `InheritorsRoute` 切换时不能丢失内部 back stack。
- 如果直接 `when` 切换导致未显示子树被销毁，应使用 `rememberSaveableStateHolder()` 为两个子树分别保存状态。
- 推荐结构：

```kotlin
val saveableStateHolder = rememberSaveableStateHolder()

saveableStateHolder.SaveableStateProvider(selectedSection.name) {
    // compose selected child route
}
```

如果测试发现切换子页会丢失搜索词或统计 Tab，则必须补这个状态保存。

### Step 7：重写 `MainActivity` 主页面分发

`selectedDestinationInDetail` 目标：

```kotlin
val selectedDestinationInDetail = when (selectedDestination) {
    HomeDestination.Articles -> articlesInDetail
    HomeDestination.Library -> libraryInDetail
    HomeDestination.Discovery -> discoveryInDetail
    HomeDestination.My -> myInDetail
}
```

`shouldShowBottomBar` 目标：

```kotlin
val shouldShowBottomBar = !showSettings && !selectedDestinationInDetail
```

主内容分发：

```kotlin
when (selectedDestination) {
    HomeDestination.Articles -> ArticlesNavHost(...)

    HomeDestination.Library -> LibraryRoute(
        onSecondaryDestinationChanged = { libraryInDetail = it },
        onKeywordSearch = openDiscoverySearch,
        onGraphExploreSelected = navigateToGraphExplore,
        onLearningRoutesSelected = navigateToLearningRoutes,
        pendingNavigation = pendingNavigation,
        onPendingNavigationConsumed = { pendingNavigation = null },
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    )

    HomeDestination.Discovery -> DiscoveryNavHost(...)

    HomeDestination.My -> MyPage(
        onBack = {},
        showBackButton = false,
        onSettingsClick = { showSettings = true },
        onNavigate = { destination ->
            pendingNavigation = destination
            showSettings = false
            selectedDestination = when (destination) {
                is MyPageDestination.Article -> HomeDestination.Articles
                is MyPageDestination.Directory -> HomeDestination.Library
                is MyPageDestination.Inheritor -> HomeDestination.Library
                is MyPageDestination.GraphExplore -> HomeDestination.Discovery
                is MyPageDestination.LearningRoutes -> HomeDestination.Discovery
                is MyPageDestination.LearningRouteDetail -> HomeDestination.Discovery
            }
        },
        onNavigateToDiscovery = {
            selectedDestination = HomeDestination.Discovery
        },
        onNavigateToLearningRoutes = {
            pendingNavigation = MyPageDestination.LearningRoutes()
            selectedDestination = HomeDestination.Discovery
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    )
}
```

### Step 8：调整设置页

修改：

```text
modern-android/app/src/main/java/com/duckylife/heritage/modern/feature/settings/SettingsScreen.kt
```

删除参数：

```kotlin
onMyPageClick: () -> Unit = {},
```

删除设置页中的 `HeritageContentCard`：

```kotlin
HeritageContentCard(
    modifier = Modifier.clickable(onClick = onMyPageClick),
) {
    ...
    Text(text = stringResource(R.string.my_page_entry))
    ...
}
```

设置页只保留外观与语言。

### Step 9：调整文章页设置入口

当前文章页右上角有设置入口，且测试依赖：

```text
文章 -> 设置 -> 收藏与最近浏览
```

目标：

- 设置主入口迁移到 `我的` 页面右上角。
- 文章页不再作为设置入口的唯一位置。

推荐做法：

1. 第一阶段可以暂时保留文章页设置图标，避免一次性移除造成用户找不到设置。
2. 但测试不能再依赖文章页进入“我的学习空间”。
3. 如果设计上追求清爽，第二阶段再移除文章页设置图标。

如果保留文章页设置图标：

- 它只进入设置页。
- 设置页不再有“收藏与最近浏览”。
- 从设置页返回后回到文章页。

如果移除文章页设置图标：

- `ArticlesNavHost` 与文章 header 中相关 `onSettingsSelected` 参数要一并清理。
- 所有设置入口测试改为从 `我的` 进入。

本次推荐采用：

```text
第一阶段保留文章页设置入口，但新增我的页设置入口；设置页不再进入我的页。
```

原因：

- 改动风险更低。
- 用户仍能从旧习惯找到设置。
- 核心问题“我的学习空间太深”已经通过底部 `我的` 解决。

### Step 10：调整 `MyPage`

修改：

```text
modern-android/app/src/main/java/com/duckylife/heritage/modern/feature/my/MyPage.kt
```

新增参数：

```kotlin
showBackButton: Boolean = true,
onSettingsClick: () -> Unit = {},
```

传给 `MyPageTopBar`：

```kotlin
MyPageTopBar(
    showBackButton = showBackButton,
    onBack = onBack,
    pendingCount = pendingCount,
    lastSyncError = profileState?.lastSyncError,
    onSyncStatusClick = { showSyncSheet = true },
    onSettingsClick = onSettingsClick,
)
```

调整 `MyPageTopBar`：

```kotlin
@Composable
private fun MyPageTopBar(
    showBackButton: Boolean,
    onBack: () -> Unit,
    pendingCount: Int,
    lastSyncError: String?,
    onSyncStatusClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(...) {
        if (showBackButton) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                )
            }
        }

        Text(
            text = stringResource(R.string.my_page_title),
            ...
            modifier = Modifier.weight(1f),
        )

        IconButton(onClick = onSyncStatusClick) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = stringResource(R.string.action_sync_now),
            )
        }

        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.nav_settings),
            )
        }
    }
}
```

替换内部 Tab：

- 当前 `SecondaryScrollableTabRow` 容易让最右侧“资料”只露出一部分。
- 改成固定宽度的 `PrimaryTabRow` 或自定义五等分 Row。

如果使用 `PrimaryTabRow`：

```kotlin
PrimaryTabRow(
    selectedTabIndex = selectedTabIndex,
    modifier = Modifier.fillMaxWidth(),
) {
    MyPageTab.entries.forEachIndexed { index, tab ->
        Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = {
                Text(
                    text = stringResource(tab.titleRes),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
    }
}
```

如果英文仍溢出，改为自定义五等分按钮：

```kotlin
Row(Modifier.fillMaxWidth()) {
    MyPageTab.entries.forEachIndexed { index, tab ->
        TextButton(
            onClick = { selectedTabIndex = index },
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(tab.titleRes),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
```

但首选 `PrimaryTabRow`，因为它保留 Material Tab 语义。

## 5. 测试编写要求

### 5.1 单元测试

建议新增：

```text
modern-android/app/src/test/java/com/duckylife/heritage/modern/feature/library/LibrarySectionTest.kt
```

测试内容：

1. `LibrarySection.Directory` 对应 `library_tab_directory`。
2. `LibrarySection.Inheritors` 对应 `library_tab_inheritors`。
3. 默认 section 是 `Directory`。

如果 `LibrarySection` 是 private，可不写这个测试，改由 Compose UI 测试覆盖。

### 5.2 Compose UI 测试：主导航

修改：

```text
modern-android/app/src/androidTest/java/com/duckylife/heritage/modern/feature/smoke/AppSmokeTest.kt
```

替换旧测试：

```kotlin
bottomNavShowsThreeTabsAndCanSwitch()
```

改为：

```kotlin
bottomNavShowsFourTabsAndCanSwitch()
```

测试步骤：

1. 启动 App。
2. 断言文章首页可见。
3. 断言底部导航存在 `文章`、`非遗库`、`发现`、`我的`。
4. 点击 `非遗库`。
5. 断言 `名录项目` 与 `代表性传承人` 子 Tab 可见。
6. 断言默认展示 `非遗名录`。
7. 点击 `代表性传承人`。
8. 断言展示 `代表性传承人` 页面。
9. 点击 `发现`。
10. 断言发现页标题可见。
11. 点击 `我的`。
12. 断言 `我的学习空间` 可见。

伪代码：

```kotlin
@Test
fun bottomNavShowsFourTabsAndCanSwitch() {
    composeRule.onNodeWithText(string(R.string.articles_latest_title))
        .assertIsDisplayed()

    clickContentDescription(R.string.nav_library)
    composeRule.onNodeWithText(string(R.string.library_tab_directory))
        .assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.library_tab_inheritors))
        .assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.directory_title))
        .assertIsDisplayed()

    composeRule.onNodeWithText(string(R.string.library_tab_inheritors))
        .performClick()
    composeRule.onNodeWithText(string(R.string.inheritors_title))
        .assertIsDisplayed()

    clickContentDescription(R.string.nav_discovery)
    composeRule.onNodeWithText(string(R.string.discovery_title))
        .assertIsDisplayed()

    clickContentDescription(R.string.nav_my)
    composeRule.onNodeWithText(string(R.string.my_page_title))
        .assertIsDisplayed()
}
```

### 5.3 Compose UI 测试：我的页从底部导航直接进入

新增测试：

```kotlin
@Test
fun myPageIsTopLevelDestination() {
    clickContentDescription(R.string.nav_my)

    composeRule.onNodeWithText(string(R.string.my_page_title))
        .assertIsDisplayed()

    composeRule.onNodeWithText(string(R.string.favorites_tab))
        .assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.browsing_tab))
        .assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.learning_tab))
        .assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.journeys_tab))
        .assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.research_tab))
        .assertIsDisplayed()

    composeRule.onNodeWithContentDescription(string(R.string.nav_settings))
        .assertIsDisplayed()
}
```

验收点：

- 不需要进入设置页。
- 不需要点击“收藏与最近浏览”。
- `资料` Tab 完整可见，不需要横向滚动。
- 根页面不显示返回按钮。可以用 `onNodeWithContentDescription(action_back).assertDoesNotExist()` 检查。

### 5.4 Compose UI 测试：设置入口从我的页进入

新增测试：

```kotlin
@Test
fun settingsCanOpenFromMyTabAndBackReturnsToMy() {
    clickContentDescription(R.string.nav_my)

    composeRule.onNodeWithContentDescription(string(R.string.nav_settings))
        .performClick()

    composeRule.onNodeWithText(string(R.string.settings_title))
        .assertIsDisplayed()

    clickContentDescription(R.string.action_back)

    composeRule.onNodeWithText(string(R.string.my_page_title))
        .assertIsDisplayed()
}
```

如果第一阶段保留文章页设置入口，还要保证：

- 文章页设置仍能打开设置页。
- 设置页不再出现“收藏与最近浏览”。

### 5.5 Compose UI 测试：收藏链路

修改当前 `favoriteFlowFromDetailToMyPage()`。

旧路径：

```text
文章详情收藏
-> 返回文章列表
-> 点击文章页设置
-> 点击收藏与最近浏览
-> 我的学习空间
```

新路径：

```text
文章详情收藏
-> 返回文章列表
-> 点击底部“我的”
-> 收藏 Tab 直接看到收藏内容
```

目标伪代码：

```kotlin
@Test
fun favoriteFlowFromDetailToMyTab() {
    clickTestArticle()
    clickContentDescription(R.string.action_favorite)
    clickContentDescription(R.string.action_back)

    clickContentDescription(R.string.nav_my)

    waitUntilTextExists(TestFakeRepository.TestArticleDetailTitle)
    composeRule.onNodeWithText(TestFakeRepository.TestArticleDetailTitle)
        .assertIsDisplayed()
}
```

### 5.6 Compose UI 测试：名录收藏链路

修改当前 `directoryDetailFavoriteShowsInMyPageAndJumpsBack()`。

旧路径依赖：

```text
名录 -> 详情收藏 -> 返回 -> 文章 -> 设置 -> 收藏与最近浏览
```

新路径：

```text
非遗库 -> 名录项目 -> 详情收藏 -> 返回 -> 我的
```

目标伪代码：

```kotlin
@Test
fun directoryDetailFavoriteShowsInMyTabAndJumpsBack() {
    clickContentDescription(R.string.nav_library)
    clickItemByTitle(TestFakeRepository.TestDirectoryTitle)

    composeRule.onNodeWithText(string(R.string.directory_detail_title))
        .assertIsDisplayed()

    clickContentDescription(R.string.action_favorite)
    clickContentDescription(R.string.action_back)

    clickContentDescription(R.string.nav_my)

    waitUntilTextExists(TestFakeRepository.TestDirectoryDetailTitle)
    composeRule.onNodeWithText(TestFakeRepository.TestDirectoryDetailTitle)
        .assertIsDisplayed()
}
```

### 5.7 Compose UI 测试：传承人链路

修改当前依赖 `nav_inheritors` 的测试。

旧路径：

```text
底部导航：传承人
```

新路径：

```text
底部导航：非遗库 -> 子 Tab：代表性传承人
```

目标伪代码：

```kotlin
@Test
fun inheritorDetailRelatedProjectNavigatesToDirectoryDetail() {
    clickContentDescription(R.string.nav_library)
    composeRule.onNodeWithText(string(R.string.library_tab_inheritors))
        .performClick()

    clickItemByTitle(TestFakeRepository.TestInheritorName)

    composeRule.onNodeWithText(string(R.string.inheritor_detail_title))
        .assertIsDisplayed()

    scrollToAndClick("中医诊疗法")

    composeRule.onNodeWithText(string(R.string.directory_detail_title))
        .assertIsDisplayed()
}
```

### 5.8 Compose UI 测试：从我的页跳回非遗库详情

新增测试，覆盖 pending navigation。

目标：

- 我的页收藏/浏览中点击名录项目，应跳转到 `非遗库` 并打开名录详情。
- 我的页收藏/浏览中点击传承人，应跳转到 `非遗库` 并打开传承人详情。

如果测试数据构造较复杂，可以拆到 `MyPageTest` 或 `AppSmokeTest` 中，使用 fake repository 预置收藏/历史。

验收点：

```text
点击我的页名录收藏 -> selectedDestination = Library -> DirectoryRoute 打开详情
点击我的页传承人历史 -> selectedDestination = Library -> InheritorsRoute 打开详情
```

### 5.9 MyPage 组件测试

修改：

```text
modern-android/app/src/androidTest/java/com/duckylife/heritage/modern/feature/my/MyPageTest.kt
```

已有测试：

```kotlin
allTabsAreVisibleAndClickable()
```

调整为：

- 不再需要 `performScrollTo()` 才能看到 `资料`。
- 直接断言五个 Tab 都可见。
- 增加设置图标断言。

目标：

```kotlin
@Test
fun allTabsAreVisibleWithoutHorizontalScroll() {
    renderMyPage(showBackButton = false)

    composeRule.onNodeWithText(string(R.string.favorites_tab)).assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.browsing_tab)).assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.learning_tab)).assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.journeys_tab)).assertIsDisplayed()
    composeRule.onNodeWithText(string(R.string.research_tab)).assertIsDisplayed()
}
```

如果 `renderMyPage()` 需要传入新参数：

```kotlin
MyPage(
    onBack = {},
    showBackButton = showBackButton,
    onSettingsClick = {},
    onNavigate = {},
    viewModel = viewModel,
    readingPathViewModel = readingPathViewModel,
)
```

### 5.10 设置页测试

修改设置页相关测试：

- 保留 `settingsPageShowsThemeAndLanguageGroups()`。
- 新增设置页不再显示 `收藏与最近浏览` 的断言。

伪代码：

```kotlin
@Test
fun settingsPageDoesNotContainMyPageEntry() {
    clickContentDescription(R.string.nav_my)
    clickContentDescription(R.string.nav_settings)

    composeRule.onNodeWithText(string(R.string.settings_title))
        .assertIsDisplayed()

    composeRule.onNodeWithText(string(R.string.my_page_entry))
        .assertDoesNotExist()
}
```

## 6. 手工视觉验收清单

真机命令：

```bash
cd modern-android
ANDROID_SERIAL=R5CR11FF7EE ./gradlew :app:installDebug
adb -s R5CR11FF7EE shell am start -n com.duckylife.heritage.modern/.MainActivity
```

### 6.1 底部导航验收

必须确认：

- 底部导航只有四项：`文章 / 非遗库 / 发现 / 我的`。
- 四项文字完整显示。
- 四项图标语义清楚。
- 选中态明显。
- 点击每一项都能切换到对应页面。

### 6.2 非遗库验收

进入 `非遗库` 后必须确认：

- 默认显示 `名录项目` 子页。
- 顶部有 `名录项目 / 代表性传承人` 子页切换。
- `名录项目` 文案完整显示。
- `代表性传承人` 文案完整显示。
- 名录搜索框、种类筛选、列表、统计 Tab 可用。
- 切到 `代表性传承人` 后，传承人搜索、筛选、列表可用。
- 进入名录详情后底部导航隐藏。
- 进入传承人详情后底部导航隐藏。
- 从详情返回后，底部导航恢复，且仍停留在 `非遗库`。

### 6.3 我的验收

进入 `我的` 后必须确认：

- 不需要经过设置页。
- 顶部标题是 `我的学习空间`。
- 根页面没有返回箭头。
- 右上角有设置入口。
- 右上角有同步入口或同步状态入口。
- `收藏 / 浏览 / 学习 / 旅程 / 资料` 五个 Tab 全部完整显示。
- 点击 `浏览` 能看到最近浏览。
- 点击 `学习` 能看到学习路线进度或空状态。
- 点击 `资料` 能进入研究资料区。
- 点击设置图标进入设置页。
- 设置页返回后回到 `我的`。

### 6.4 设置页验收

必须确认：

- 设置页仍显示主题模式。
- 设置页仍显示语言。
- 设置页不再显示 `收藏与最近浏览`。
- 从文章页设置入口进入设置页时，设置页也不显示 `收藏与最近浏览`。
- 从我的页设置入口进入设置页时，返回后回到我的页。

### 6.5 回归旧问题

本次改造顺手覆盖以下已知体验问题：

- `我的学习空间` 顶部 Tab 中 `资料` 不得再只露出一部分。
- 底部导航内容不得遮挡主列表末尾内容。
- `非遗库` 子页切换不得把筛选按钮挤出屏幕。
- 详情页返回后不能丢失子页状态。

## 7. 验证命令

本地单元与打包验证：

```bash
cd modern-android
./gradlew :app:verifyLocal
```

真机安装：

```bash
cd modern-android
ANDROID_SERIAL=R5CR11FF7EE ./gradlew :app:installDebug
```

连接真机 UI 测试：

```bash
cd modern-android
ANDROID_SERIAL=R5CR11FF7EE ./gradlew :app:connectedDebugAndroidTest
```

如果只想跑冒烟测试：

```bash
cd modern-android
ANDROID_SERIAL=R5CR11FF7EE ./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.duckylife.heritage.modern.feature.smoke.AppSmokeTest
```

如果只想跑我的页测试：

```bash
cd modern-android
ANDROID_SERIAL=R5CR11FF7EE ./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.duckylife.heritage.modern.feature.my.MyPageTest
```

## 8. 分步骤执行建议

### 执行批次 A：导航资源与主导航骨架

目标：

- 新增字符串。
- 修改 `HomeDestination`。
- 新增 `LibraryRoute` 空壳。
- 让底部导航先显示 `文章 / 非遗库 / 发现 / 我的`。

验收：

- 编译通过。
- App 启动后底部导航四项显示正确。
- `非遗库` 可以先临时显示名录页。
- `我的` 可以直接显示 `MyPage`。

### 执行批次 B：非遗库子页切换

目标：

- 给 `DirectoryRoute` 和 `InheritorsRoute` 增加 `listTopContent`。
- `LibraryRoute` 注入 `名录项目 / 代表性传承人` 子 Tab。
- 完成两个子页面切换。

验收：

- `非遗库` 默认名录。
- 点击 `代表性传承人` 后切换到传承人。
- 两个子页面搜索、筛选、详情都可用。
- 从详情返回后仍在正确子页。

### 执行批次 C：我的页根页面化

目标：

- `MyPage` 支持 `showBackButton = false`。
- `MyPage` 顶部增加设置入口。
- 内部五个 Tab 改成完整显示。
- 删除设置页中的“收藏与最近浏览”入口。

验收：

- 底部 `我的` 直接进入学习空间。
- 根页面无返回箭头。
- 设置从我的页打开。
- 五个内部 Tab 全部完整显示。

### 执行批次 D：测试更新

目标：

- 更新 `AppSmokeTest`。
- 更新 `MyPageTest`。
- 更新依赖 `nav_directory` / `nav_inheritors` / 设置进入我的页的旧测试。
- 新增 pending navigation 测试。

验收：

- `./gradlew :app:verifyLocal` 通过。
- `connectedDebugAndroidTest` 至少核心 smoke 与 my 测试通过。

### 执行批次 E：真机视觉验收

目标：

- 安装到真机。
- 逐项截图确认底部导航、非遗库、我的页、设置页。
- 对比本文档第 6 节手工验收清单。

验收：

- 无底部导航文字截断。
- 无 `我的` Tab 隐藏入口。
- 无 `资料` Tab 半露。
- 无 `名录/传承人` 入口丢失。

## 9. 风险与处理

### 风险 1：非遗库切换导致子页状态丢失

症状：

- 名录搜索词丢失。
- 名录统计 Tab 切回列表。
- 传承人筛选条件丢失。

处理：

- 使用 `rememberSaveableStateHolder()` 保存两个子树状态。
- 保证 `DirectoryRoute` 和 `InheritorsRoute` 的 `rememberSaveable` back stack 不被无意义重建。

### 风险 2：我的页内部研究资料详情与底部导航冲突

症状：

- 在 `资料` Tab 进入资料详情时，底部导航仍显示，导致详情页可视区域被压缩。

处理：

- 给 `MyPage` 增加 `onSecondaryDestinationChanged`。
- 当 `selectedResearchPackageId` 或 `selectedResearchReportId` 非空时，回调 `true`，让主 Scaffold 隐藏底部导航。

第一阶段可以先保留底部导航，但视觉验收必须确认没有遮挡。如果遮挡，必须接入 `myInDetail`。

### 风险 3：设置入口重复导致用户困惑

症状：

- 文章页有设置，`我的` 页也有设置。

处理：

- 第一阶段允许重复，但设置页必须只显示设置内容。
- 第二阶段可以移除文章页设置入口。
- 不允许设置页继续显示“收藏与最近浏览”。

### 风险 4：英文环境五个内部 Tab 溢出

症状：

- `Favorites / Browse / Learning / Journeys / Research` 放不下。

处理：

- 英文文案改短为 `Saved / Recent / Learn / Journey / Research`。
- Tab 文本 `maxLines = 1`，必要时 ellipsis。
- 中文环境必须完整显示，不接受 ellipsis。

## 10. 最终验收标准

本次重构完成后，必须同时满足：

1. 底部导航显示 `文章 / 非遗库 / 发现 / 我的`。
2. `我的学习空间` 可以通过底部 `我的` 一步进入。
3. 设置页不再包含 `收藏与最近浏览`。
4. `非遗库` 内部可以在 `名录项目` 和 `代表性传承人` 之间切换。
5. 名录详情和传承人详情仍可正常打开。
6. 从我的页收藏/浏览记录点击文章、名录、传承人时，能跳到正确详情页。
7. `我的` 页五个内部 Tab 全部完整可见。
8. `./gradlew :app:verifyLocal` 通过。
9. 核心 `connectedDebugAndroidTest` 通过。
10. 真机视觉验收无底部导航遮挡、无 Tab 半露、无入口路径混乱。

