package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.DirectoryDetailEntity
import com.duckylife.heritage.modern.core.database.entity.DirectoryItemEntity
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DirectoryMapperTest {

    // region queryKey

    @Test
    fun queryKeyIncludesKind() {
        val nationalKey = DirectoryItemQuery(kind = DirectoryItemKind.NationalProject).queryKey()
        val unescoKey = DirectoryItemQuery(kind = DirectoryItemKind.UnescoEntry).queryKey()
        assertNotEquals(nationalKey, unescoKey)
    }

    @Test
    fun queryKeyIncludesKeywords() {
        val emptyKey = DirectoryItemQuery(keywords = null).queryKey()
        val keyWithSearch = DirectoryItemQuery(keywords = "故宫").queryKey()
        assertNotEquals(emptyKey, keyWithSearch)
    }

    @Test
    fun queryKeyIncludesRegion() {
        val noRegion = DirectoryItemQuery(region = null).queryKey()
        val beijing = DirectoryItemQuery(region = "北京").queryKey()
        assertNotEquals(noRegion, beijing)
    }

    @Test
    fun queryKeyIncludesCategory() {
        val noCat = DirectoryItemQuery(category = null).queryKey()
        val folk = DirectoryItemQuery(category = "民间文学").queryKey()
        assertNotEquals(noCat, folk)
    }

    @Test
    fun queryKeyIncludesYear() {
        val noYear = DirectoryItemQuery(year = null).queryKey()
        val year2020 = DirectoryItemQuery(year = 2020).queryKey()
        assertNotEquals(noYear, year2020)
    }

    @Test
    fun queryKeyIncludesListType() {
        val noList = DirectoryItemQuery(listType = null).queryKey()
        val representative = DirectoryItemQuery(listType = "representative").queryKey()
        assertNotEquals(noList, representative)
    }

    @Test
    fun sameQueryProducesSameKey() {
        val q1 = DirectoryItemQuery(
            kind = DirectoryItemKind.NationalProject,
            keywords = "剪纸",
            region = "河北",
            category = "传统美术",
            year = 2022,
            listType = "representative",
        )
        val q2 = q1.copy()
        assertEquals(q1.queryKey(), q2.queryKey())
    }

    @Test
    fun differentSearchesProduceDifferentKeys() {
        val base = DirectoryItemQuery(kind = DirectoryItemKind.NationalProject)
        val withSearch = DirectoryItemQuery(kind = DirectoryItemKind.NationalProject, keywords = "剪纸")
        // Keys should differ; Room will cache them separately.
        assertNotEquals(base.queryKey(), withSearch.queryKey())
    }

    // endregion

    // region DirectoryItemSummaryDto ↔ DirectoryItemEntity

    @Test
    fun toEntityUsesDtoIdWhenPresent() {
        val dto = DirectoryItemSummaryDto(id = "dir-1", title = "Test", kind = DirectoryItemKind.NationalProject)
        val entity = dto.toEntity(DirectoryItemQuery(), page = 1, positionInPage = 0)
        assertEquals("dir-1", entity.id)
    }

    @Test
    fun toEntityFallsBackToSourceUrlWhenIdMissing() {
        val dto = DirectoryItemSummaryDto(id = null, sourceUrl = "https://src.test/dir/1", title = "Test")
        val entity = dto.toEntity(DirectoryItemQuery(), page = 1, positionInPage = 0)
        assertEquals("https://src.test/dir/1", entity.id)
    }

    @Test
    fun toEntityFallsBackToGeneratedId() {
        val dto = DirectoryItemSummaryDto(id = null, sourceUrl = null)
        val query = DirectoryItemQuery(kind = DirectoryItemKind.CulturalEcoZone, page = 1)
        val entity = dto.toEntity(query, page = 3, positionInPage = 0)
        assertEquals("culturalEcoZone|||||-3-0", entity.id)
    }

    @Test
    fun toEntityStoresAllFields() {
        val dto = DirectoryItemSummaryDto(
            id = "dir-1",
            kind = DirectoryItemKind.ProductiveProtectionBase,
            title = "非遗基地",
            summary = "摘要",
            category = "传统技艺",
            region = "江苏",
            projectCode = "VIII-101",
            batch = "第一批",
            publishedYear = 2006,
            listType = "representative",
            coverImage = MediaAssetDto(displayUrl = "https://img.test/d.jpg"),
            sourceUrl = "https://src.test/d",
        )
        val entity = dto.toEntity(DirectoryItemQuery(kind = DirectoryItemKind.ProductiveProtectionBase), page = 1, positionInPage = 0)
        assertEquals("productiveProtectionBase", entity.kind)
        assertEquals("非遗基地", entity.title)
        assertEquals("摘要", entity.summary)
        assertEquals("传统技艺", entity.category)
        assertEquals("江苏", entity.region)
        assertEquals("VIII-101", entity.projectCode)
        assertEquals("第一批", entity.batch)
        assertEquals(2006, entity.publishedYear)
        assertEquals("representative", entity.listType)
        assertEquals("https://src.test/d", entity.sourceUrl)
        assertNotNull(entity.coverImageJson)
    }

    @Test
    fun toDtoRoundtripPreservesKeyFields() {
        val original = DirectoryItemSummaryDto(
            id = "dir-1",
            kind = DirectoryItemKind.ChinaUnescoEntry,
            title = "中国非遗",
            summary = "摘要",
            category = "传统戏剧",
            region = "浙江",
            projectCode = "IV-200",
            batch = "第二批",
            publishedYear = 2008,
            listType = "representative",
            coverImage = MediaAssetDto(thumbnailUrl = "https://img.test/thumb.jpg"),
            sourceUrl = "https://src.test/2",
        )
        val entity = original.toEntity(DirectoryItemQuery(kind = DirectoryItemKind.ChinaUnescoEntry), page = 1, positionInPage = 0)
        val roundtripped = entity.toDto()
        assertEquals(original.id, roundtripped.id)
        assertEquals(original.kind, roundtripped.kind)
        assertEquals(original.title, roundtripped.title)
        assertEquals(original.summary, roundtripped.summary)
        assertEquals(original.category, roundtripped.category)
        assertEquals(original.region, roundtripped.region)
        assertEquals(original.projectCode, roundtripped.projectCode)
        assertEquals(original.batch, roundtripped.batch)
        assertEquals(original.publishedYear, roundtripped.publishedYear)
        assertEquals(original.listType, roundtripped.listType)
        assertEquals(original.coverImage?.thumbnailUrl, roundtripped.coverImage?.thumbnailUrl)
        assertEquals(original.sourceUrl, roundtripped.sourceUrl)
    }

    @Test
    fun toDtoHandlesNullCoverImage() {
        val entity = DirectoryItemEntity(
            id = "dir-1",
            queryKey = "nationalProject|||||",
            kind = "nationalProject",
            title = "T",
            summary = null,
            category = null,
            region = null,
            projectCode = null,
            batch = null,
            publishedYear = null,
            listType = null,
            coverImageJson = null,
            sourceUrl = null,
            page = 1,
            positionInPage = 0,
        )
        val dto = entity.toDto()
        assertNull(dto.coverImage)
    }

    // endregion

    // region DirectoryItemDetailDto ↔ DirectoryDetailEntity

    @Test
    fun detailToEntityUsesDtoIdFirst() {
        val dto = DirectoryItemDetailDto(id = "detail-dir-1", title = "D")
        val entity = dto.toEntity(DirectoryItemKind.NationalProject, sourceId = null, updatedAtEpochMillis = 1000L)
        assertEquals("detail-dir-1", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToSourceId() {
        val dto = DirectoryItemDetailDto(id = null, title = "D")
        val entity = dto.toEntity(DirectoryItemKind.CulturalEcoZone, sourceId = "src-dir-1", updatedAtEpochMillis = 1000L)
        assertEquals("src-dir-1", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToSourceUrl() {
        val dto = DirectoryItemDetailDto(id = null, title = "D", sourceUrl = "https://s.test/d")
        val entity = dto.toEntity(DirectoryItemKind.NationalProject, sourceId = null, updatedAtEpochMillis = 1000L)
        assertEquals("https://s.test/d", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToTitle() {
        val dto = DirectoryItemDetailDto(id = null, title = "My Directory Item")
        val entity = dto.toEntity(DirectoryItemKind.UnescoEntry, sourceId = null, updatedAtEpochMillis = 1000L)
        assertEquals("My Directory Item", entity.id)
    }

    @Test
    fun detailToEntityStoresKindFromParameter() {
        val dto = DirectoryItemDetailDto(id = "d1", title = "T", kind = DirectoryItemKind.NationalProject)
        // The kind parameter should be used, not dto.kind
        val entity = dto.toEntity(DirectoryItemKind.ContractingState, sourceId = null, updatedAtEpochMillis = 0L)
        assertEquals("contractingState", entity.kind)
    }

    @Test
    fun detailToEntitySerializesGalleryAndReferences() {
        val dto = DirectoryItemDetailDto(
            id = "d1",
            title = "T",
            gallery = listOf(MediaAssetDto(displayUrl = "https://img.test/g1.jpg")),
            contentBlocks = listOf(
                com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto(
                    type = com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType.Text,
                    text = "body",
                ),
            ),
            relatedProjects = listOf(
                com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto(title = "Related Project", sourceId = "rp1"),
            ),
            relatedInheritors = listOf(
                com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto(title = "Related Inheritor", sourceId = "ri1"),
            ),
            relatedDocuments = listOf(
                com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto(title = "Related Doc"),
            ),
        )
        val entity = dto.toEntity(DirectoryItemKind.NationalProject, sourceId = "src-1", updatedAtEpochMillis = 1000L)
        assertEquals("src-1", entity.sourceId)
        // Verify gallery JSON contains the image URL
        assertTrue(entity.galleryJson.contains("g1.jpg"))
        assertTrue(entity.contentBlocksJson.contains("body"))
        assertTrue(entity.relatedProjectsJson.contains("Related Project"))
        assertTrue(entity.relatedInheritorsJson.contains("Related Inheritor"))
        assertTrue(entity.relatedDocumentsJson.contains("Related Doc"))
    }

    @Test
    fun detailToDtoRoundtripPreservesGallery() {
        val original = DirectoryItemDetailDto(
            id = "dir-detail-1",
            kind = DirectoryItemKind.NationalProject,
            title = "非遗项目",
            summary = "简介",
            category = "传统医药",
            region = "云南",
            projectCode = "IX-300",
            batch = "第三批",
            publishedYear = 2011,
            listType = "representative",
            coverImage = MediaAssetDto(displayUrl = "https://img.test/cover.jpg"),
            sourceUrl = "https://src.test/3",
            nominationType = "国家级",
            protectionUnit = "保护单位名称",
            gallery = listOf(
                MediaAssetDto(displayUrl = "https://img.test/g1.jpg", altText = "图片1"),
                MediaAssetDto(displayUrl = "https://img.test/g2.jpg", altText = "图片2"),
            ),
            relatedProjects = listOf(
                com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto(
                    title = "关联项目",
                    sourceId = "rel-1",
                    kind = "nationalProject",
                    category = "传统美术",
                ),
            ),
        )
        val entity = original.toEntity(DirectoryItemKind.NationalProject, sourceId = null, updatedAtEpochMillis = 1000L)
        val roundtripped = entity.toDto()
        assertEquals(original.id, roundtripped.id)
        assertEquals(original.kind, roundtripped.kind)
        assertEquals(original.projectCode, roundtripped.projectCode)
        assertEquals(original.protectionUnit, roundtripped.protectionUnit)
        assertEquals(original.nominationType, roundtripped.nominationType)
        assertEquals(2, roundtripped.gallery.size)
        assertEquals("https://img.test/g1.jpg", roundtripped.gallery[0].displayUrl)
        assertEquals("图片1", roundtripped.gallery[0].altText)
        assertEquals(1, roundtripped.relatedProjects.size)
        assertEquals("关联项目", roundtripped.relatedProjects[0].title)
        assertEquals("rel-1", roundtripped.relatedProjects[0].sourceId)
    }

    @Test
    fun detailToDtoHandlesEmptyGalleryAndReferences() {
        val entity = DirectoryDetailEntity(
            id = "d1",
            sourceId = null,
            kind = "nationalProject",
            title = "T",
            summary = null,
            category = null,
            region = null,
            projectCode = null,
            batch = null,
            publishedYear = null,
            listType = null,
            coverImageJson = null,
            sourceUrl = null,
            nominationType = null,
            protectionUnit = null,
            galleryJson = "[]",
            contentBlocksJson = "[]",
            relatedProjectsJson = "[]",
            relatedInheritorsJson = "[]",
            relatedDocumentsJson = "[]",
            updatedAtEpochMillis = 0L,
        )
        val dto = entity.toDto()
        assertTrue(dto.gallery.isEmpty())
        assertTrue(dto.contentBlocks.isEmpty())
        assertTrue(dto.relatedProjects.isEmpty())
        assertTrue(dto.relatedInheritors.isEmpty())
        assertTrue(dto.relatedDocuments.isEmpty())
    }

    // endregion
}
