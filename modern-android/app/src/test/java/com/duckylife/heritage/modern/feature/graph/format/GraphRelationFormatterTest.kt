package com.duckylife.heritage.modern.feature.graph.format

import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceSource
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.feature.graph.model.AssociationLevel
import com.duckylife.heritage.modern.feature.graph.model.GraphEdgeUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GraphRelationFormatterTest {

    @Test
    fun mapsKnownRelationTypesToStringResources() {
        assertEquals(R.string.graph_relation_type_related_to, GraphRelationFormatter.labelResId(GraphRelationType.RelatedTo))
        assertEquals(R.string.graph_relation_type_inherits_project, GraphRelationFormatter.labelResId(GraphRelationType.InheritsProject))
        assertEquals(R.string.graph_relation_type_similar, GraphRelationFormatter.labelResId(GraphRelationType.Similar))
        assertEquals(R.string.graph_relation_type_topic, GraphRelationFormatter.labelResId(GraphRelationType.Topic))
        assertEquals(R.string.graph_relation_type_ai_inferred, GraphRelationFormatter.labelResId(GraphRelationType.AiInferred))
        assertEquals(R.string.graph_relation_type_unknown, GraphRelationFormatter.labelResId(GraphRelationType.Unknown))
    }

    @Test
    fun aiInferredIsControlledByToggle() {
        assertTrue(GraphRelationFormatter.isControlledByAiToggle(GraphRelationType.AiInferred))
        assertFalse(GraphRelationFormatter.isControlledByAiToggle(GraphRelationType.RelatedTo))
        assertFalse(GraphRelationFormatter.isControlledByAiToggle(GraphRelationType.Topic))
    }

    @Test
    fun associationAndConfidenceThresholdsAreCentralized() {
        assertEquals(AssociationLevel.High, GraphRelationFormatter.associationLevel(0.75))
        assertEquals(AssociationLevel.Medium, GraphRelationFormatter.associationLevel(0.45))
        assertEquals(AssociationLevel.Low, GraphRelationFormatter.associationLevel(0.1))
        assertEquals(R.string.graph_association_level_high, GraphRelationFormatter.associationLevelLabelResId(AssociationLevel.High))
        assertEquals(R.string.graph_association_level_medium, GraphRelationFormatter.associationLevelLabelResId(AssociationLevel.Medium))
        assertEquals(R.string.graph_association_level_low, GraphRelationFormatter.associationLevelLabelResId(AssociationLevel.Low))
        assertEquals(R.string.graph_confidence_high, GraphRelationFormatter.confidenceLabelResId(0.8))
        assertEquals(R.string.graph_confidence_medium, GraphRelationFormatter.confidenceLabelResId(0.5))
        assertEquals(R.string.graph_confidence_low, GraphRelationFormatter.confidenceLabelResId(0.2))
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
