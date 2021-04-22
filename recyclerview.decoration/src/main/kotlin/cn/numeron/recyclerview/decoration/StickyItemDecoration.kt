package cn.numeron.recyclerview.decoration

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/** 显示悬浮视图的装饰器，需要在adapter上实现[StickySupport]接口 */
class StickyItemDecoration : RecyclerView.ItemDecoration() {

    private val paint = Paint()

    private val stickyBitmaps = SparseArray<Bitmap>()

    /** 当Adapter中的数据发生变化需要重新计算悬浮视图的位置时，调用此方法 */
    fun invalidate() = stickyBitmaps.clear()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        repeat(parent.childCount) { index ->
            //遍历所有子视图
            val view = parent.getChildAt(index)
            //获取该View在RecyclerView中的布局位置
            val layoutPosition = parent.getChildLayoutPosition(view)
            val bitmap = stickyBitmaps[layoutPosition]
            if (bitmap != null) {
                //如果该位置有bitmap，则绘制
                c.drawBitmap(bitmap, view.left.toFloat(), view.top.toFloat() - bitmap.height, paint)
            }
        }
        //绘制顶部的悬浮视图
        drawClosestStickyBitmap(c, parent)
    }

    private fun drawClosestStickyBitmap(canvas: Canvas, parent: RecyclerView) {
        //获取RecyclerView中第1个子View的真实位置
        val view = parent.getChildAt(0)
        val layoutPosition = parent.getChildLayoutPosition(view)
        var bitmapIndex = layoutPosition
        var bitmap: Bitmap?
        do {
            //尝试获取缓存在该位置上的Bitmap，如果没有
            //则获取上一个位置的Bitmap，直到成功获取
            bitmap = stickyBitmaps[bitmapIndex--]
        } while (bitmap == null && bitmapIndex > -1)
        if (bitmap != null) {
            //根据第一个子View的位置，计算出此Bitmap是否需要向上移动
            //当第一个子View的底部小于Bitmap的高度时
            //并且当下一个子View有一个新的Bitmap需要悬浮时
            //让固定的悬浮视图的top上移
            val viewBottom = view.bottom
            val bitmapHeight = bitmap.height
            val hasNextBitmap = stickyBitmaps[layoutPosition + 1] != null
            val top = if (viewBottom <= bitmapHeight && hasNextBitmap) -bitmapHeight + viewBottom else 0
            canvas.drawBitmap(bitmap, 0f, top.toFloat(), paint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val adapter = parent.adapter
        if (adapter is StickySupport) {
            val position = parent.getChildLayoutPosition(view)
            //如果adapter实现了StickySupport接口，判断此位置的itemView是否需要添加粘性视图
            if (adapter.isSticky(position)) {
                //获取或创建粘性视图后，将其转换为Bitmap
                val stickyBitmap = stickyBitmaps.get(position)
                        ?: adapter.createStickyView(position, parent)
                                .toBitmap(parent)
                                .also {
                                    stickyBitmaps.put(position, it)
                                }
                //使当前View的位置往下偏移stickyBitmap.height的高度
                outRect.set(0, stickyBitmap.height, 0, 0)
            }
        }
    }

    private fun View.toBitmap(parent: RecyclerView): Bitmap {
        //按宽：MATCH_PARENT、高：WRAP_CONTENT的尺寸进行测量
        val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(parent.measuredWidthAndState,
                parent.paddingStart + parent.paddingEnd, parent.measuredWidth)
        val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parent.measuredHeightAndState,
                parent.paddingTop + parent.paddingBottom, RecyclerView.LayoutParams.WRAP_CONTENT)
        measure(childWidthMeasureSpec, childHeightMeasureSpec)
        //布局
        layout(0, 0, measuredWidth, measuredHeight)
        //绘制
        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        //返回Bitmap
        return bitmap
    }

}