package com.duckylife.heritage.modern.core.database.converter

import android.util.Log
import androidx.room.TypeConverter
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.profile.PendingOperationKind
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class ProfileTypeConverters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let {
            HeritageJson.encodeToString(ListSerializer(String.serializer()), it)
        }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return try {
            value?.let {
                HeritageJson.decodeFromString(ListSerializer(String.serializer()), it)
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode string list JSON, returning empty list", e)
            emptyList()
        }
    }

    @TypeConverter
    fun fromSyncStatus(status: ProfileSyncStatus): String = status.name

    @TypeConverter
    fun toSyncStatus(value: String): ProfileSyncStatus =
        ProfileSyncStatus.entries.find { it.name == value } ?: ProfileSyncStatus.Synced

    @TypeConverter
    fun fromPendingOperationKind(kind: PendingOperationKind): String = kind.name

    @TypeConverter
    fun toPendingOperationKind(value: String): PendingOperationKind =
        PendingOperationKind.entries.find { it.name == value } ?: PendingOperationKind.Unknown

    private companion object {
        const val TAG = "ProfileTypeConverters"
    }
}
