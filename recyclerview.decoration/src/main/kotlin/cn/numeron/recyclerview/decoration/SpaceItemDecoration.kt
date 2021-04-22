package cn.numeron.recyclerview.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/** 平均分配两个ItemView间距的装饰器，适应各种LayoutManager */
class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        when (parent.layoutManager) {
            is StaggeredGridLayoutManager -> setOutRectByGrid(outRect, view, parent)
            is GridLayoutManager -> setOutRectByGrid(outRect, view, parent)
            is LinearLayoutManager -> setOutRectByLinear(outRect, view, parent)
            else -> outRect.set(space, space, space, space)
        }
    }

    private fun setOutRectByGrid(rect: Rect, view: View, parent: RecyclerView) {
        //获取当前View的位置
        val position = parent.getChildAdapterPosition(view)
        //计算有多少列
        val columnCount = getSpanCount(parent)
        //当前是第几列
        val column = position % columnCount
        //当前是第几行
        val row = position / columnCount
        //计算平均间隔
        val average = space / columnCount.toFloat()

        rect.left = ((columnCount - column) * average).toInt()
        rect.top = if (row == 0) space else 0
        rect.right = ((column + 1) * average).toInt()
        rect.bottom = space
    }

    private fun setOutRectByLinear(rect: Rect, view: View, parent: RecyclerView) {
        val position = parent.getChildLayoutPosition(view)
        val orientation = getLinearLayoutOrientation(parent)
        val isFirstView = position == 0
        val isHorizontal = orientation == LinearLayoutManager.HORIZONTAL
        val isVertical = orientation == LinearLayoutManager.VERTICAL
        rect.right = if (isVertical) space else space / 2
        rect.bottom = if (isHorizontal) space else space / 2
        rect.left = if (isVertical || isFirstView) space else space / 2
        rect.top = if (isHorizontal || isFirstView) space else space / 2
    }

    private fun getSpanCount(recyclerView: RecyclerView): Int {
        return when (val manager = recyclerView.layoutManager) {
            is StaggeredGridLayoutManager -> manager.spanCount
            is GridLayoutManager -> manager.spanCount
            else -> 1
        }
    }

    private fun getLinearLayoutOrientation(parent: RecyclerView): Int {
        return (parent.layoutManager as LinearLayoutManager).orientation
    }

}