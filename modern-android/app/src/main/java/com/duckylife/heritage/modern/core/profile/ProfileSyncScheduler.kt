package com.duckylife.heritage.modern.core.profile

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 调度唯一的 profile 同步后台任务。
 */
interface ProfileSyncScheduler {
    fun scheduleImmediate()
}

/**
 * 使用 WorkManager 实现的 [ProfileSyncScheduler]。
 *
 * 使用 `ExistingWorkPolicy.KEEP` 避免并发重复同步；任务需要网络连接。
 */
@Singleton
class WorkManagerProfileSyncScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ProfileSyncScheduler {

    override fun scheduleImmediate() {
        val request = OneTimeWorkRequestBuilder<LocalUserSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request,
        )
    }

    private companion object {
        const val SYNC_WORK_NAME = "heritage-profile-sync"
    }
}
