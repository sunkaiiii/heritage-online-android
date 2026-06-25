package com.duckylife.heritage.modern.feature.graph.format

import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEdgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceSource
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.feature.graph.model.GraphEdgeUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GraphRelationFormatterTest {

    @Test
    fun mapsKnownRelationTypesToChinese() {
        assertEquals("相关内容", GraphRelationFormatter.label(GraphRelationType.RelatedTo))
        assertEquals("传承项目", GraphRelationFormatter.label(GraphRelationType.InheritsProject))
        assertEquals("相似内容", GraphRelationFormatter.label(GraphRelationType.Similar))
        assertEquals("同属主题", GraphRelationFormatter.label(GraphRelationType.Topic))
        assertEquals("AI 推断", GraphRelationFormatter.label(GraphRelationType.AiInferred))
        assertEquals("其他关系", GraphRelationFormatter.label(GraphRelationType.Unknown))
    }

    @Test
    fun prefersExplicitLabelOverType() {
        val edge = GraphEdgeUiModel(
            fromNodeKey = "a",
            toNodeKey = "b",
            relationType = GraphRelationType.RelatedTo,
            label = "同项目传承",
        )
        assertEquals("同项目传承", GraphRelationFormatter.label(edge))
    }

    @Test
    fun fallsBackToTypeLabelWhenExplicitLabelBlank() {
        val edge = GraphEdgeUiModel(
            fromNodeKey = "a",
            toNodeKey = "b",
            relationType = GraphRelationType.Topic,
            label = "   ",
        )
        assertEquals("同属主题", GraphRelationFormatter.label(edge))
    }

    @Test
    fun aiInferredIsControlledByToggle() {
        assertTrue(GraphRelationFormatter.isControlledByAiToggle(GraphRelationType.AiInferred))
        assertFalse(GraphRelationFormatter.isControlledByAiToggle(GraphRelationType.RelatedTo))
        assertFalse(GraphRelationFormatter.isControlledByAiToggle(GraphRelationType.Topic))
    }

    @Test
    fun edgeIsAiInferredWhenSourceIsAi() {
        val edge = GraphEdgeUiModel(
            fromNodeKey = "a",
            toNodeKey = "b",
            relationType = GraphRelationType.RelatedTo,
            source = GraphEvidenceSource.Ai,
        )
        assertTrue(edge.isAiInferred)
    }
}
