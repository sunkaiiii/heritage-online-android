package com.duckylife.heritage.modern.feature.detail

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.duckylife.heritage.modern.core.data.IntelligenceSection
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentRefDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.feature.detail.intelligence.ContentIntelligenceUiState
import com.duckylife.heritage.modern.feature.detail.intelligence.DetailIntelligenceSection
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailVisualAcceptanceTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun captureDetailEnhancements() {
        val readyAi = AiCardDto(
            hasAi = true,
            summary = "这是完整的智能解读摘要。通过展开按钮可以在同一卡片内查看完整内容，" +
                "测试长文本折叠与收起行为是否正常工作。",
            shortSummary = "这是折叠状态下的简短摘要，用于验证默认 2–3 行展示。",
            highlights = listOf(
                "要点一：展示小圆点列表样式",
                "要点二：最多默认显示 3 条",
                "要点三：超出时可展开全部",
                "要点四：验证展开全部行为",
            ),
            keywords = listOf("剪纸", "民俗", "非遗", "传承", "手艺人", "传统工艺"),
            isStale = true,
        )
        val readyState = ContentIntelligenceUiState(
            aiSection = IntelligenceSection(SectionStatus.Ready, readyAi),
            graphSection = IntelligenceSection(
                SectionStatus.Ready,
                GraphNeighborsDto(
                    nodes = listOf(
                        GraphNodeDto(nodeKey = "n1", type = GraphNodeType.Article),
                        GraphNodeDto(nodeKey = "n2", type = GraphNodeType.Article),
                        GraphNodeDto(nodeKey = "n3", type = GraphNodeType.DirectoryItem),
                    ),
                ),
            ),
            recommendationSection = IntelligenceSection(
                SectionStatus.Ready,
                listOf(
                    ContentRefDto(type = GraphNodeType.Article, id = "r1"),
                    ContentRefDto(type = GraphNodeType.Article, id = "r2"),
                ),
            ),
            learningRoutesAvailable = true,
        )
        val missingState = ContentIntelligenceUiState(
            aiSection = IntelligenceSection(SectionStatus.Missing, AiCardDto()),
        )

        composeRule.setContent {
            HeritageTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                    ) {
                        item {
                            Text(
                                text = "智能解读（Ready + stale）",
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                            DetailIntelligenceSection(
                                uiState = readyState,
                                onKeywordClick = {},
                            )
                        }
                        item {
                            Text(
                                text = "智能解读（Missing）",
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                            DetailIntelligenceSection(
                                uiState = missingState,
                                onKeywordClick = {},
                            )
                        }
                        item {
                            Text(
                                text = "继续探索",
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                            DetailContinueExploreSection(
                                uiState = readyState,
                                onGraphClick = {},
                                onSimilarClick = {},
                                onLearningRoutesClick = {},
                            )
                        }
                    }
                }
            }
        }

        composeRule.waitForIdle()

        // 1. 通过 drawToBitmap 获取组件位图并保存到应用私有目录
        val contentView = composeRule.activity.window.decorView
            .findViewById<ViewGroup>(android.R.id.content)
            .getChildAt(0)
        val bitmap = contentView.drawToBitmap(Bitmap.Config.ARGB_8888)
        val externalDir = composeRule.activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: composeRule.activity.filesDir
        val file = File(externalDir, "detail_acceptance.png")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        // 2. 再拷贝到 /sdcard/Pictures/，防止测试结束后应用私有目录被清理
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val publicPath = "/sdcard/Pictures/detail_acceptance.png"
        uiAutomation.executeShellCommand("cp ${file.absolutePath} $publicPath")
        uiAutomation.executeShellCommand("chmod 644 $publicPath")

        Log.d("VisualAcceptance", "saved screenshot to ${file.absolutePath} and $publicPath")
    }
}
