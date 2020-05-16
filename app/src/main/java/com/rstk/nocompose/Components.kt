package com.rstk.nocompose

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.rstk.nocompose.RowColumn.Size.Fill
import com.rstk.nocompose.RowColumn.Size.Wrap

//@NoCompose
abstract class Component<T : View>(protected val context: Context) {

  abstract val view: T
}

abstract class ContainerComponent<T : ViewGroup>(context: Context) : Component<T>(context) {

  operator fun <V : View> Component<V>.unaryPlus() {
    this@ContainerComponent.view.addView(this.view)
  }
}

class Column(context: Context) : RowColumn(context) {

  enum class Gravity {
    Start, Center, End
  }

  init {
    view.orientation = LinearLayout.VERTICAL
  }

  fun <T : View> child(
    width: Observable<Size> = static(Fill),
    height: Observable<Size> = static(Wrap),
    gravity: Observable<Gravity> = static(Gravity.Center),
    weight: Observable<Float> = static(0F),
    childBuilder: () -> Component<T>
  ) {
    child(width, height, static(Row.Gravity.Center), gravity, weight, childBuilder)
  }
}


class Row(context: Context) : RowColumn(context) {

  enum class Gravity {
    Top, Center, Bottom
  }

  init {
    view.orientation = LinearLayout.HORIZONTAL
  }

  fun <T : View> child(
    width: Observable<Size> = static(Wrap),
    height: Observable<Size> = static(Fill),
    gravity: Observable<Gravity> = static(Gravity.Center),
    weight: Observable<Float> = static(0F),
    childBuilder: () -> Component<T>
  ) {
    child(width, height, gravity, static(Column.Gravity.Center), weight, childBuilder)
  }
}

abstract class RowColumn(context: Context) : ContainerComponent<LinearLayout>(context) {

  sealed class Size {
    data class Const(val dp: Int) : Size()
    object Fill : Size()
    object Wrap : Size()
  }

  override val view: LinearLayout by lazy { LinearLayout(context) }

  protected fun <T : View> child(
    width: Observable<Size>,
    height: Observable<Size>,
    rowGravity: Observable<Row.Gravity>,
    columnGravity: Observable<Column.Gravity>,
    weight: Observable<Float>,
    childBuilder: () -> Component<T>
  ) {
    val layoutParams = LinearLayout.LayoutParams(0, 0)
    val child = childBuilder()
    child.view.layoutParams = layoutParams
    view.addView(child.view)

    var rowGravityValue: Int = Gravity.CENTER_VERTICAL
    var columnGravityValue: Int = Gravity.CENTER_HORIZONTAL

    width.subscribe {
      layoutParams.width = when (it) {
        Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Size.Const -> Math.round(context.resources.displayMetrics.density * it.dp)
      }
      child.view.requestLayout()
    }
    height.subscribe {
      layoutParams.height = when (it) {
        Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Size.Const -> Math.round(context.resources.displayMetrics.density * it.dp)
      }
      child.view.requestLayout()
    }
    rowGravity.subscribe {
      rowGravityValue = when (it) {
        Row.Gravity.Top -> Gravity.TOP
        Row.Gravity.Center -> Gravity.CENTER_VERTICAL
        Row.Gravity.Bottom -> Gravity.BOTTOM
      }
      layoutParams.gravity = rowGravityValue or columnGravityValue
      child.view.requestLayout()
    }
    columnGravity.subscribe {
      columnGravityValue = when (it) {
        Column.Gravity.Start -> Gravity.START
        Column.Gravity.Center -> Gravity.CENTER_HORIZONTAL
        Column.Gravity.End -> Gravity.END
      }
      layoutParams.gravity = rowGravityValue or columnGravityValue
      child.view.requestLayout()
    }
    weight.subscribe {
      layoutParams.weight = it
      child.view.requestLayout()
    }
  }
}

class Label(context: Context) : Component<TextView>(context) {

  override val view: TextView by lazy { TextView(context) }

  var text: Observable<String?>
    get() = throw IllegalStateException("No access")
    set(value) {
      value.subscribe { view.text = it }
    }
}

class Button(context: Context) : Component<android.widget.Button>(context) {

  override val view: android.widget.Button by lazy { android.widget.Button(context) }

  var text: Observable<String?>
    get() = throw IllegalStateException("No access")
    set(value) {
      value.subscribe { view.text = it }
    }

  var onPress: () -> Unit
    get() = throw IllegalStateException("No access")
    set(value) {
      view.setOnClickListener { value.invoke() }
    }
}

class Image(context: Context) : Component<ImageView>(context) {

  override val view: ImageView by lazy { ImageView(context) }

  var imageResId: Observable<Int?>
    get() = throw IllegalStateException("No access")
    set(value) {
      value.subscribe { resId ->
        view.setImageDrawable(resId?.let { context.resources.getDrawable(it) })
      }
    }
}

class Space(context: Context) : Component<android.widget.Space>(context) {

  override val view: android.widget.Space by lazy { android.widget.Space(context) }
}

class Container(context: Context, val child: Component<out View>) : Component<View>(context) {

  override val view: View by lazy { child.view }

  var margin: Observable<Int>
    get() = throw IllegalStateException("No access")
    set(value) {
      value.subscribe {
        val marginDp = Math.round(context.resources.displayMetrics.density * it)
        val layoutParams = child.view.layoutParams as FrameLayout.LayoutParams?
          ?: ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
        layoutParams.setMargins(marginDp, marginDp, marginDp, marginDp)
        child.view.layoutParams = layoutParams
        child.view.requestLayout()
      }
    }

  var padding: Observable<Int>
    get() = throw IllegalStateException("No access")
    set(value) {
      value.subscribe {
        val paddingDp = Math.round(context.resources.displayMetrics.density * it)
        child.view.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
      }
    }
}

class If(
  context: Context,
  private val viewT: Component<out View>,
  private val viewF: Component<out View>
) : Component<FrameLayout>(context) {

  override val view: FrameLayout by lazy { FrameLayout(context) }

  var value: Observable<Boolean>
    get() = throw IllegalStateException("No access")
    set(value) {
      value.subscribe {
        view.removeAllViews()
        if (it) view.addView(viewT.view)
        else view.addView(viewF.view)
        val layoutParams = view.getChildAt(0).layoutParams as FrameLayout.LayoutParams
        layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
        layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
      }
    }
}