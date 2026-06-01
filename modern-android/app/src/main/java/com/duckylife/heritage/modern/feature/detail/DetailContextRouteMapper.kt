package com.duckylife.heritage.modern.feature.detail

/**
 * 通用的 Context 目标到路由 key 的映射器。
 *
 * 每个导航宿主（Articles、Directory、Inheritors、Discovery）各自创建一个实例，
 * 传入该宿主的局部路由 key 构造函数。这样映射逻辑只写一次，
 * 后续新增 DetailContextTarget 类型时编译会提示所有 mapper 需要补分支。
 */
class DetailContextRouteMapper<R>(
    private val article: (String) -> R,
    private val directoryItem: (String) -> R,
    private val inheritor: (String) -> R,
    private val collection: (String) -> R,
    private val topic: (String, String) -> R,
) {
    fun map(target: DetailContextTarget): R =
        when (target) {
            is DetailContextTarget.Article -> article(target.id)
            is DetailContextTarget.DirectoryItem -> directoryItem(target.id)
            is DetailContextTarget.Inheritor -> inheritor(target.id)
            is DetailContextTarget.Collection -> collection(target.id)
            is DetailContextTarget.Topic -> topic(target.type, target.key)
        }
}
