package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.InheritorDetailEntity
import com.duckylife.heritage.modern.core.database.entity.InheritorEntity
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InheritorMapperTest {

    // region queryKey

    @Test
    fun queryKeyIncludesKeywords() {
        val emptyKey = InheritorQuery(keywords = null).queryKey()
        val searchKey = InheritorQuery(keywords = "张三").queryKey()
        assertNotEquals(emptyKey, searchKey)
    }

    @Test
    fun queryKeyIncludesRegion() {
        val noRegion = InheritorQuery(region = null).queryKey()
        val regionKey = InheritorQuery(region = "四川").queryKey()
        assertNotEquals(noRegion, regionKey)
    }

    @Test
    fun queryKeyIncludesCategory() {
        val noCat = InheritorQuery(category = null).queryKey()
        val catKey = InheritorQuery(category = "传统美术").queryKey()
        assertNotEquals(noCat, catKey)
    }

    @Test
    fun queryKeyIncludesYear() {
        val noYear = InheritorQuery(year = null).queryKey()
        val yearKey = InheritorQuery(year = 2015).queryKey()
        assertNotEquals(noYear, yearKey)
    }

    @Test
    fun queryKeyIncludesGender() {
        val noGender = InheritorQuery(gender = null).queryKey()
        val maleKey = InheritorQuery(gender = "男").queryKey()
        assertNotEquals(noGender, maleKey)
    }

    @Test
    fun sameQueryProducesSameKey() {
        val q1 = InheritorQuery(keywords = "李四", region = "湖南", category = "传统技艺", year = 2018, gender = "女")
        val q2 = q1.copy()
        assertEquals(q1.queryKey(), q2.queryKey())
    }

    @Test
    fun emptyQueryProducesPredictableKey() {
        val key = InheritorQuery().queryKey()
        assertEquals("||||", key)
    }

    // endregion

    // region InheritorSummaryDto ↔ InheritorEntity

    @Test
    fun toEntityUsesDtoIdWhenPresent() {
        val dto = InheritorSummaryDto(id = "inh-1", name = "张三")
        val entity = dto.toEntity(InheritorQuery(), page = 1, positionInPage = 0)
        assertEquals("inh-1", entity.id)
    }

    @Test
    fun toEntityFallsBackToSourceUrlWhenIdMissing() {
        val dto = InheritorSummaryDto(id = null, sourceUrl = "https://src.test/inh/1", name = "张三")
        val entity = dto.toEntity(InheritorQuery(), page = 1, positionInPage = 0)
        assertEquals("https://src.test/inh/1", entity.id)
    }

    @Test
    fun toEntityFallsBackToGeneratedId() {
        val dto = InheritorSummaryDto(id = null, sourceUrl = null, name = "张三")
        val query = InheritorQuery(keywords = "剪纸", region = "河北")
        val entity = dto.toEntity(query, page = 2, positionInPage = 3)
        assertEquals("剪纸|河北|||-2-3", entity.id)
    }

    @Test
    fun toEntityStoresQueryKey() {
        val query = InheritorQuery(keywords = "陶瓷", region = "江西")
        val entity = InheritorSummaryDto(id = "inh-1", name = "李四").toEntity(query, page = 1, positionInPage = 0)
        assertEquals(query.queryKey(), entity.queryKey)
    }

    @Test
    fun toEntityStoresAllFields() {
        val dto = InheritorSummaryDto(
            id = "inh-1",
            name = "王五",
            gender = "男",
            birthDateText = "1965年",
            ethnicity = "汉族",
            category = "传统戏剧",
            projectCode = "IV-101",
            projectName = "京剧",
            region = "北京",
            batch = "第二批",
            description = "国家级传承人",
            coverImage = MediaAssetDto(displayUrl = "https://img.test/inh.jpg"),
            sourceUrl = "https://src.test/inh",
        )
        val entity = dto.toEntity(InheritorQuery(), page = 1, positionInPage = 0)
        assertEquals("王五", entity.name)
        assertEquals("男", entity.gender)
        assertEquals("1965年", entity.birthDateText)
        assertEquals("汉族", entity.ethnicity)
        assertEquals("传统戏剧", entity.category)
        assertEquals("IV-101", entity.projectCode)
        assertEquals("京剧", entity.projectName)
        assertEquals("北京", entity.region)
        assertEquals("第二批", entity.batch)
        assertEquals("国家级传承人", entity.description)
        assertEquals("https://src.test/inh", entity.sourceUrl)
        assertNotNull(entity.coverImageJson)
    }

    @Test
    fun toDtoRoundtripPreservesKeyFields() {
        val original = InheritorSummaryDto(
            id = "inh-1",
            name = "赵六",
            gender = "女",
            birthDateText = "1970年",
            ethnicity = "苗族",
            category = "传统美术",
            projectCode = "VII-200",
            projectName = "苗绣",
            region = "贵州",
            batch = "第三批",
            description = "省级传承人",
            coverImage = MediaAssetDto(thumbnailUrl = "https://img.test/thumb.jpg"),
            sourceUrl = "https://src.test/inh2",
        )
        val entity = original.toEntity(InheritorQuery(), page = 1, positionInPage = 0)
        val roundtripped = entity.toDto()
        assertEquals(original.id, roundtripped.id)
        assertEquals(original.name, roundtripped.name)
        assertEquals(original.gender, roundtripped.gender)
        assertEquals(original.birthDateText, roundtripped.birthDateText)
        assertEquals(original.ethnicity, roundtripped.ethnicity)
        assertEquals(original.category, roundtripped.category)
        assertEquals(original.projectCode, roundtripped.projectCode)
        assertEquals(original.projectName, roundtripped.projectName)
        assertEquals(original.region, roundtripped.region)
        assertEquals(original.batch, roundtripped.batch)
        assertEquals(original.description, roundtripped.description)
        assertEquals(original.sourceUrl, roundtripped.sourceUrl)
        assertEquals(original.coverImage?.thumbnailUrl, roundtripped.coverImage?.thumbnailUrl)
    }

    @Test
    fun toDtoHandlesNullCoverImage() {
        val entity = InheritorEntity(
            id = "inh-1",
            queryKey = "||||",
            name = "张三",
            gender = null,
            birthDateText = null,
            ethnicity = null,
            category = null,
            projectCode = null,
            projectName = null,
            region = null,
            batch = null,
            description = null,
            coverImageJson = null,
            sourceUrl = null,
            page = 1,
            positionInPage = 0,
        )
        val dto = entity.toDto()
        assertNull(dto.coverImage)
    }

    // endregion

    // region InheritorDetailDto ↔ InheritorDetailEntity

    @Test
    fun detailToEntityUsesDtoIdFirst() {
        val dto = InheritorDetailDto(id = "detail-inh-1", name = "张三")
        val entity = dto.toEntity(sourceId = null, updatedAtEpochMillis = 1000L)
        assertEquals("detail-inh-1", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToSourceId() {
        val dto = InheritorDetailDto(id = null, name = "张三")
        val entity = dto.toEntity(sourceId = "src-inh-1", updatedAtEpochMillis = 1000L)
        assertEquals("src-inh-1", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToSourceUrl() {
        val dto = InheritorDetailDto(id = null, name = "张三", sourceUrl = "https://s.test/inh")
        val entity = dto.toEntity(sourceId = null, updatedAtEpochMillis = 1000L)
        assertEquals("https://s.test/inh", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToName() {
        val dto = InheritorDetailDto(id = null, name = "李四")
        val entity = dto.toEntity(sourceId = null, updatedAtEpochMillis = 1000L)
        assertEquals("李四", entity.id)
    }

    @Test
    fun detailToEntityStoresAllMetadata() {
        val dto = InheritorDetailDto(
            id = "detail-inh-1",
            name = "王五",
            gender = "男",
            birthDateText = "1965年",
            ethnicity = "汉族",
            category = "传统戏剧",
            projectCode = "IV-101",
            projectName = "京剧",
            region = "北京",
            batch = "第二批",
            description = "国家级传承人",
            sourceUrl = "https://src.test/detail-inh",
        )
        val entity = dto.toEntity(sourceId = "src-1", updatedAtEpochMillis = 2000L)
        assertEquals("src-1", entity.sourceId)
        assertEquals("王五", entity.name)
        assertEquals("男", entity.gender)
        assertEquals("1965年", entity.birthDateText)
        assertEquals("汉族", entity.ethnicity)
        assertEquals("传统戏剧", entity.category)
        assertEquals("IV-101", entity.projectCode)
        assertEquals("京剧", entity.projectName)
        assertEquals("北京", entity.region)
        assertEquals("第二批", entity.batch)
        assertEquals("国家级传承人", entity.description)
        assertEquals("https://src.test/detail-inh", entity.sourceUrl)
        assertEquals(2000L, entity.updatedAtEpochMillis)
    }

    @Test
    fun detailToEntitySerializesContentBlocksAndReferences() {
        val dto = InheritorDetailDto(
            id = "d1",
            name = "张三",
            contentBlocks = listOf(
                com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto(
                    type = com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType.Text,
                    text = "传承谱系",
                ),
            ),
            relatedProjects = listOf(
                com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto(title = "关联项目", sourceId = "rp1"),
            ),
            relatedInheritors = listOf(
                com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto(title = "关联传承人", sourceId = "ri1"),
            ),
        )
        val entity = dto.toEntity(sourceId = null, updatedAtEpochMillis = 0L)
        assertTrue(entity.contentBlocksJson.contains("传承谱系"))
        assertTrue(entity.relatedProjectsJson.contains("关联项目"))
        assertTrue(entity.relatedInheritorsJson.contains("关联传承人"))
    }

    @Test
    fun detailToDtoRoundtripPreservesContent() {
        val original = InheritorDetailDto(
            id = "inh-detail-1",
            name = "张大千",
            gender = "男",
            birthDateText = "1940年",
            ethnicity = "汉族",
            category = "传统美术",
            projectCode = "VII-300",
            projectName = "国画",
            region = "四川",
            batch = "第一批",
            description = "详细介绍",
            coverImage = MediaAssetDto(displayUrl = "https://img.test/cover.jpg"),
            sourceUrl = "https://src.test/inh-detail",
            contentBlocks = listOf(
                com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto(
                    type = com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType.Heading,
                    text = "艺术生涯",
                ),
                com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto(
                    type = com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType.Text,
                    text = "师从名家...",
                ),
            ),
            relatedProjects = listOf(
                com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto(title = "国画项目"),
            ),
            relatedInheritors = listOf(
                com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto(title = "弟子某某", sourceId = "disciple-1"),
            ),
        )
        val entity = original.toEntity(sourceId = null, updatedAtEpochMillis = 0L)
        val roundtripped = entity.toDto()
        assertEquals(original.id, roundtripped.id)
        assertEquals(original.name, roundtripped.name)
        assertEquals(original.projectName, roundtripped.projectName)
        assertEquals(original.description, roundtripped.description)
        assertEquals(original.coverImage?.displayUrl, roundtripped.coverImage?.displayUrl)
        assertEquals(2, roundtripped.contentBlocks.size)
        assertEquals("艺术生涯", roundtripped.contentBlocks[0].text)
        assertEquals("师从名家...", roundtripped.contentBlocks[1].text)
        assertEquals(1, roundtripped.relatedProjects.size)
        assertEquals("国画项目", roundtripped.relatedProjects[0].title)
        assertEquals(1, roundtripped.relatedInheritors.size)
        assertEquals("弟子某某", roundtripped.relatedInheritors[0].title)
    }

    @Test
    fun detailToDtoHandlesEmptyCollections() {
        val entity = InheritorDetailEntity(
            id = "d1",
            sourceId = null,
            name = "张三",
            gender = null,
            birthDateText = null,
            ethnicity = null,
            category = null,
            projectCode = null,
            projectName = null,
            region = null,
            batch = null,
            description = null,
            coverImageJson = null,
            sourceUrl = null,
            contentBlocksJson = "[]",
            relatedProjectsJson = "[]",
            relatedInheritorsJson = "[]",
            updatedAtEpochMillis = 0L,
        )
        val dto = entity.toDto()
        assertTrue(dto.contentBlocks.isEmpty())
        assertTrue(dto.relatedProjects.isEmpty())
        assertTrue(dto.relatedInheritors.isEmpty())
    }

    // endregion
}
