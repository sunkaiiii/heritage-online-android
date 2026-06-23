package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * 带 wire value 的枚举公共接口。
 */
interface WireNamed {
    val wireName: String
}

/**
 * 为枚举创建支持“未知 wire value 降级到指定 Unknown 值”的序列化器。
 */
inline fun <reified T : WireNamed> enumWithUnknownSerializer(
    name: String,
    unknown: T,
): KSerializer<T> = object : KSerializer<T> {
    override val descriptor = PrimitiveSerialDescriptor(name, PrimitiveKind.STRING)
    private val constants by lazy { T::class.java.enumConstants ?: emptyArray() }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.wireName)
    }

    override fun deserialize(decoder: Decoder): T {
        val raw = decoder.decodeString()
        return constants.firstOrNull { it.wireName == raw } ?: unknown
    }
}

@Serializable(with = GraphNodeTypeSerializer::class)
enum class GraphNodeType(override val wireName: String) : WireNamed {
    @SerialName("article") Article("article"),
    @SerialName("directoryItem") DirectoryItem("directoryItem"),
    @SerialName("inheritor") Inheritor("inheritor"),
    @SerialName("category") Category("category"),
    @SerialName("region") Region("region"),
    @SerialName("year") Year("year"),
    @SerialName("kind") Kind("kind"),
    @SerialName("projectCode") ProjectCode("projectCode"),
    @SerialName("unknown") Unknown("unknown");
}

object GraphNodeTypeSerializer :
    KSerializer<GraphNodeType> by enumWithUnknownSerializer("GraphNodeType", GraphNodeType.Unknown)

@Serializable(with = GraphRelationTypeSerializer::class)
enum class GraphRelationType(override val wireName: String) : WireNamed {
    @SerialName("RELATED_TO") RelatedTo("RELATED_TO"),
    @SerialName("INHERITS_PROJECT") InheritsProject("INHERITS_PROJECT"),
    @SerialName("SIMILAR") Similar("SIMILAR"),
    @SerialName("TOPIC") Topic("TOPIC"),
    @SerialName("AI_INFERRED") AiInferred("AI_INFERRED"),
    @SerialName("unknown") Unknown("unknown");
}

object GraphRelationTypeSerializer :
    KSerializer<GraphRelationType> by enumWithUnknownSerializer("GraphRelationType", GraphRelationType.Unknown)

@Serializable(with = GraphEvidenceSourceSerializer::class)
enum class GraphEvidenceSource(override val wireName: String) : WireNamed {
    @SerialName("explicit") Explicit("explicit"),
    @SerialName("inferred") Inferred("inferred"),
    @SerialName("embedding") Embedding("embedding"),
    @SerialName("ai") Ai("ai"),
    @SerialName("unknown") Unknown("unknown");
}

object GraphEvidenceSourceSerializer :
    KSerializer<GraphEvidenceSource> by enumWithUnknownSerializer("GraphEvidenceSource", GraphEvidenceSource.Unknown)

@Serializable(with = LearningRouteDifficultySerializer::class)
enum class LearningRouteDifficulty(override val wireName: String) : WireNamed {
    @SerialName("all") All("all"),
    @SerialName("beginner") Beginner("beginner"),
    @SerialName("intermediate") Intermediate("intermediate"),
    @SerialName("deep") Deep("deep"),
    @SerialName("unknown") Unknown("unknown");
}

object LearningRouteDifficultySerializer :
    KSerializer<LearningRouteDifficulty> by enumWithUnknownSerializer(
        "LearningRouteDifficulty", LearningRouteDifficulty.Unknown
    )

@Serializable(with = LearningRouteSeedTypeSerializer::class)
enum class LearningRouteSeedType(override val wireName: String) : WireNamed {
    @SerialName("region") Region("region"),
    @SerialName("category") Category("category"),
    @SerialName("year") Year("year"),
    @SerialName("kind") Kind("kind"),
    @SerialName("content") Content("content"),
    @SerialName("unknown") Unknown("unknown");
}

object LearningRouteSeedTypeSerializer :
    KSerializer<LearningRouteSeedType> by enumWithUnknownSerializer(
        "LearningRouteSeedType", LearningRouteSeedType.Unknown
    )

@Serializable(with = JourneyStrategySerializer::class)
enum class JourneyStrategy(override val wireName: String) : WireNamed {
    @SerialName("balanced") Balanced("balanced"),
    @SerialName("continue") Continue("continue"),
    @SerialName("novelty") Novelty("novelty"),
    @SerialName("deepDive") DeepDive("deepDive"),
    @SerialName("unknown") Unknown("unknown");
}

object JourneyStrategySerializer :
    KSerializer<JourneyStrategy> by enumWithUnknownSerializer("JourneyStrategy", JourneyStrategy.Unknown)

@Serializable(with = TrailStrategySerializer::class)
enum class TrailStrategy(override val wireName: String) : WireNamed {
    @SerialName("mixed") Mixed("mixed"),
    @SerialName("topicLadder") TopicLadder("topicLadder"),
    @SerialName("hiddenGem") HiddenGem("hiddenGem"),
    @SerialName("bridgeWalk") BridgeWalk("bridgeWalk"),
    @SerialName("similar") Similar("similar"),
    @SerialName("bridge") Bridge("bridge"),
    @SerialName("representative") Representative("representative"),
    @SerialName("diverse") Diverse("diverse"),
    @SerialName("unknown") Unknown("unknown");
}

object TrailStrategySerializer :
    KSerializer<TrailStrategy> by enumWithUnknownSerializer("TrailStrategy", TrailStrategy.Unknown)

@Serializable(with = SpacetimeDimensionSerializer::class)
enum class SpacetimeDimension(override val wireName: String) : WireNamed {
    @SerialName("region") Region("region"),
    @SerialName("category") Category("category"),
    @SerialName("year") Year("year"),
    @SerialName("kind") Kind("kind"),
    @SerialName("unknown") Unknown("unknown");
}

object SpacetimeDimensionSerializer :
    KSerializer<SpacetimeDimension> by enumWithUnknownSerializer("SpacetimeDimension", SpacetimeDimension.Unknown)

@Serializable(with = AnalyticsDimensionSerializer::class)
enum class AnalyticsDimension(override val wireName: String) : WireNamed {
    @SerialName("region") Region("region"),
    @SerialName("category") Category("category"),
    @SerialName("year") Year("year"),
    @SerialName("kind") Kind("kind"),
    @SerialName("targetType") TargetType("targetType"),
    @SerialName("unknown") Unknown("unknown");
}

object AnalyticsDimensionSerializer :
    KSerializer<AnalyticsDimension> by enumWithUnknownSerializer("AnalyticsDimension", AnalyticsDimension.Unknown)

@Serializable(with = RankingMetricSerializer::class)
enum class RankingMetric(override val wireName: String) : WireNamed {
    @SerialName("total") Total("total"),
    @SerialName("articleCount") ArticleCount("articleCount"),
    @SerialName("directoryItemCount") DirectoryItemCount("directoryItemCount"),
    @SerialName("inheritorCount") InheritorCount("inheritorCount"),
    @SerialName("connectivity") Connectivity("connectivity"),
    @SerialName("hiddenGem") HiddenGem("hiddenGem"),
    @SerialName("completeness") Completeness("completeness"),
    @SerialName("imageRichness") ImageRichness("imageRichness"),
    @SerialName("aiCoverage") AiCoverage("aiCoverage"),
    @SerialName("freshness") Freshness("freshness"),
    @SerialName("unknown") Unknown("unknown");
}

object RankingMetricSerializer :
    KSerializer<RankingMetric> by enumWithUnknownSerializer("RankingMetric", RankingMetric.Unknown)

@Serializable(with = ExportFormatSerializer::class)
enum class ExportFormat(override val wireName: String) : WireNamed {
    @SerialName("json") Json("json"),
    @SerialName("markdown") Markdown("markdown"),
    @SerialName("csv") Csv("csv"),
    @SerialName("unknown") Unknown("unknown");
}

object ExportFormatSerializer : KSerializer<ExportFormat> {
    private val delegate = enumWithUnknownSerializer<ExportFormat>("ExportFormat", ExportFormat.Unknown)
    override val descriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: ExportFormat) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): ExportFormat {
        val raw = decoder.decodeString()
        return when (raw) {
            "md", "markdown" -> ExportFormat.Markdown
            else -> ExportFormat.entries.firstOrNull { it.wireName == raw } ?: ExportFormat.Unknown
        }
    }
}

@Serializable(with = ExportScopeTypeSerializer::class)
enum class ExportScopeType(override val wireName: String) : WireNamed {
    @SerialName("ids") Ids("ids"),
    @SerialName("search") Search("search"),
    @SerialName("topic") Topic("topic"),
    @SerialName("ranking") Ranking("ranking"),
    @SerialName("learningRoute") LearningRoute("learningRoute"),
    @SerialName("favorites") Favorites("favorites"),
    @SerialName("unknown") Unknown("unknown");
}

object ExportScopeTypeSerializer :
    KSerializer<ExportScopeType> by enumWithUnknownSerializer("ExportScopeType", ExportScopeType.Unknown)

@Serializable(with = ResearchTaskStatusSerializer::class)
enum class ResearchTaskStatus(override val wireName: String) : WireNamed {
    @SerialName("queued") Queued("queued"),
    @SerialName("running") Running("running"),
    @SerialName("succeeded") Succeeded("succeeded"),
    @SerialName("failed") Failed("failed"),
    @SerialName("cancelled") Cancelled("cancelled"),
    @SerialName("unknown") Unknown("unknown");
}

object ResearchTaskStatusSerializer :
    KSerializer<ResearchTaskStatus> by enumWithUnknownSerializer("ResearchTaskStatus", ResearchTaskStatus.Unknown)

@Serializable(with = SectionStatusSerializer::class)
enum class SectionStatus(override val wireName: String) : WireNamed {
    @SerialName("ready") Ready("ready"),
    @SerialName("empty") Empty("empty"),
    @SerialName("missing") Missing("missing"),
    @SerialName("unavailable") Unavailable("unavailable"),
    @SerialName("disabled") Disabled("disabled"),
    @SerialName("unknown") Unknown("unknown");
}

object SectionStatusSerializer :
    KSerializer<SectionStatus> by enumWithUnknownSerializer("SectionStatus", SectionStatus.Unknown)
