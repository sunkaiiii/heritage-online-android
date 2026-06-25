package com.duckylife.heritage.modern.feature.graph.model

import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEdgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GraphModelMappingTest {

    @Test
    fun nodeMappingPreservesKeyFields() {
        val dto = GraphNodeDto(
            nodeKey = "article-1",
            type = GraphNodeType.Article,
            id = "a1",
            title = "标题",
            subtitle = "副标题",
            category = "传统技艺",
            region = "浙江",
            coverImageUrl = "https://example.test/cover.jpg",
        )
        val ui = dto.toGraphNodeUiModel()

        assertEquals("article-1", ui.nodeKey)
        assertEquals(GraphNodeType.Article, ui.type)
        assertEquals("a1", ui.id)
        assertEquals("标题", ui.title)
        assertEquals("副标题", ui.subtitle)
        assertEquals("传统技艺", ui.category)
        assertEquals("浙江", ui.region)
        assertEquals("https://example.test/cover.jpg", ui.coverImageUrl)
        assertTrue(ui.isContentNode)
    }

    @Test
    fun topicNodeIsNotContentNode() {
        val ui = GraphNodeDto(
            nodeKey = "category-1",
            type = GraphNodeType.Category,
            title = "类别",
        ).toGraphNodeUiModel()

        assertFalse(ui.isContentNode)
    }

    @Test
    fun nodesAreDedupedByNodeKey() {
        val nodes = listOf(
            GraphNodeDto(nodeKey = "a", type = GraphNodeType.Article, title = "First"),
            GraphNodeDto(nodeKey = "a", type = GraphNodeType.Article, title = "Duplicate"),
            GraphNodeDto(nodeKey = "b", type = GraphNodeType.Article),
        )
        val uiModels = nodes.toGraphNodeUiModels()

        assertEquals(2, uiModels.size)
        assertEquals("First", uiModels.first().title)
    }

    @Test
    fun edgesFilterDanglingEndpoints() {
        val edges = listOf(
            GraphEdgeDto(from = "a", to = "b", type = GraphRelationType.RelatedTo),
            GraphEdgeDto(from = "b", to = "c", type = GraphRelationType.RelatedTo),
            GraphEdgeDto(from = "c", to = "missing", type = GraphRelationType.RelatedTo),
        )
        val uiEdges = edges.toGraphEdgeUiModels(availableNodeKeys = setOf("a", "b", "c"))

        assertEquals(2, uiEdges.size)
        assertTrue(uiEdges.none { it.toNodeKey == "missing" })
    }

    @Test
    fun edgesAreDedupedByFromToRelation() {
        val edges = listOf(
            GraphEdgeDto(from = "a", to = "b", type = GraphRelationType.RelatedTo),
            GraphEdgeDto(from = "a", to = "b", type = GraphRelationType.RelatedTo),
            GraphEdgeDto(from = "a", to = "b", type = GraphRelationType.Topic),
        )
        val uiEdges = edges.toGraphEdgeUiModels(availableNodeKeys = setOf("a", "b"))

        assertEquals(2, uiEdges.size)
    }
}
