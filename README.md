# RecyclerView
# 当前最新版本号：[![](https://jitpack.io/v/cn.numeron/recyclerview.decoration.svg)](https://jitpack.io/#cn.numeron/recyclerview.decoration)

### SpaceItemDecoration
* 使每个Item之间的距离保持指定的间距
* 使用方法：
1. 创建实例、指定间距并添加到RecyclerView即可。
```
recyclerView.addItemDecoration(SpaceItemDecoration(8.dp))
```
### StickyItemDecoration
* 在指定的Item顶部添加跟随Item滚动的悬浮视图。
* 使用方法：
1. 在RecyclerView.Adapter的实现类上实现StickySupport接口。
```
class ItemAdapter : PagingBindingAdapter<NumberItem, NumberItemBindingHolder, NumberViewBinding>(::NumberItemBindingHolder), StickySupport {
    /** 如果某个Position上的Item需要显示悬浮视图，则返回true */
    override fun isSticky(position: Int): Boolean {
        val previous = (position - 1).toString()
        val current = position.toString()
        return position == 0 || current.length > 1 && current.first() != previous.first()
    }

    /** 根据position创建要显示的悬浮视图 */
    override fun createStickyView(position: Int, parent: ViewGroup): View {
        val textView = TextView(parent.context)
        val itemString = position.toString()
        textView.text = if (itemString.length == 1) "0" else itemString.first().toString()
        textView.setPadding(24)
        val typedValue = TypedValue()
        parent.context.theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        textView.setBackgroundColor(typedValue.data)
        return textView
    }
}
```
2. 在RecyclerView上添加StickyItemDecoration即可
```
recyclerView.addItemDecoration(StickyItemDecoration())
```

![image](https://raw.githubusercontent.com/xiazunyang/recyclerview.decoration/main/preview.gif)
### 引入
1.  在你的android工程的根目录下的build.gradle文件中的适当的位置添加以下代码：
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
2.  在你的android工程中对应的android模块的build.gradle文件中的适当位置添加以下代码：
```
implementation 'cn.numeron:recyclerview.decoration:latest_version'
```