package com.duckylife.heritage.modern.core.saved

enum class SavedContentType(val wireName: String) {
    Article("article"),
    DirectoryItem("directoryItem"),
    Inheritor("inheritor"),
}

data class SavedContentTarget(
    val id: String? = null,
    val sourceId: String? = null,
    val sourceUrl: String? = null,
    val category: String? = null,
    val kind: String? = null,
)

data class SavedContentSnapshot(
    val contentType: SavedContentType,
    val id: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val coverImageJson: String? = null,
    val category: String? = null,
    val region: String? = null,
    val year: Int? = null,
    val sourceUrl: String? = null,
    val target: SavedContentTarget = SavedContentTarget(),
)
