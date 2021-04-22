package cn.numeron.recyclerview.decoration

import android.view.View
import android.view.ViewGroup

/** 使用[StickyItemDecoration]时，必需在adapter上实现此接口 */
interface StickySupport {

    fun isSticky(position: Int): Boolean

    fun createStickyView(position: Int, parent: ViewGroup): View

}