package com.duckylife.heritage.modern.core.profile

class FakeProfileSyncScheduler : ProfileSyncScheduler {
    var scheduleImmediateCallCount = 0
        private set

    override fun scheduleImmediate() {
        scheduleImmediateCallCount++
    }
}
