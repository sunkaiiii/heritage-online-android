package com.duckylife.heritage.modern.core.profile

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * 在后台重放 pending profile 操作并拉取服务端镜像的 Worker。
 *
 * 不直接使用 Hilt Worker 注入，而是通过 [SyncWorkerEntryPoint] 从 application context 获取仓库，
 * 避免引入额外的 hilt-work 依赖和 Application 配置。
 */
class LocalUserSyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    private val repository: LocalUserSyncRepository by lazy {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            SyncWorkerEntryPoint::class.java,
        )
        entryPoint.localUserSyncRepository()
    }

    override suspend fun doWork(): Result {
        return try {
            repository.syncNow()
            Result.success()
        } catch (e: ProfileSyncException) {
            if (e.isRetryable) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SyncWorkerEntryPoint {
        fun localUserSyncRepository(): LocalUserSyncRepository
    }
}
