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

abstract class Component<V : View>(protected val context: Context) {

  abstract val view: V
}

abstract class ContainerComponent<V : ViewGroup>(context: Context) : Component<V>(context) {

  operator fun <V : View> Component<V>.unaryPlus() {
    this@ContainerComponent.view.addView(view)
  }
}

class Column(context: Context) : RowColumn(context) {

  enum class Gravity {
    Start, Center, End
  }

  init {
    view.orientation = LinearLayout.VERTICAL
  }

  fun <V : View> Component<V>.layout(
    paramsBuilder: LayoutParams.() -> Unit
  ): Component<V> {
    val params = LayoutParams()
    params.paramsBuilder()
    layoutChild(
      params.width,
      params.height,
      static(Row.Gravity.Center),
      params.gravity,
      params.weight,
      this
    )
    return this
  }

  @NoCompose
  data class LayoutParams(
    var width: Observable<Size> = static(Fill),
    var height: Observable<Size> = static(Wrap),
    var gravity: Observable<Gravity> = static(Gravity.Center),
    var weight: Observable<Float> = static(0F),
    var test: Observable<String> = static("")
  )
}


class Row(context: Context) : RowColumn(context) {

  enum class Gravity {
    Top, Center, Bottom
  }

  init {
    view.orientation = LinearLayout.HORIZONTAL
  }

  fun <V : View> Component<V>.layout(
    paramsBuilder: LayoutParams.() -> Unit
  ): Component<V> {
    val params = LayoutParams()
    params.paramsBuilder()
    layoutChild(
      params.width,
      params.height,
      params.gravity,
      static(Column.Gravity.Center),
      params.weight,
      this
    )
    return this
  }

  @NoCompose
  data class LayoutParams(
    var width: Observable<Size> = static(Fill),
    var height: Observable<Size> = static(Wrap),
    var gravity: Observable<Gravity> = static(Gravity.Center),
    var weight: Observable<Float> = static(0F)
  )
}

abstract class RowColumn(context: Context) : ContainerComponent<LinearLayout>(context) {

  sealed class Size {
    data class Const(val dp: Int) : Size()
    object Fill : Size()
    object Wrap : Size()
  }

  override val view: LinearLayout by lazy { LinearLayout(context) }

  protected fun <V : View> layoutChild(
    width: Observable<Size>,
    height: Observable<Size>,
    rowGravity: Observable<Row.Gravity>,
    columnGravity: Observable<Column.Gravity>,
    weight: Observable<Float>,
    child: Component<V>
  ) {
    val layoutParams = LinearLayout.LayoutParams(0, 0)
    child.view.layoutParams = layoutParams

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

@NoCompose
class Label(context: Context, text: Observable<String?>) : Component<TextView>(context) {

  override val view: TextView by lazy { TextView(context) }

  init {
    text.subscribe { view.text = it }
  }
}

@NoCompose
class Button(
  context: Context,
  text: Observable<String?>,
  onPress: () -> Unit
) : Component<android.widget.Button>(context) {

  override val view: android.widget.Button by lazy { android.widget.Button(context) }

  init {
    text.subscribe { view.text = it }
    view.setOnClickListener { onPress.invoke() }
  }
}

@NoCompose
class Image(context: Context, imageResId: Observable<Int?>) : Component<ImageView>(context) {

  override val view: ImageView by lazy { ImageView(context) }

  init {
    imageResId.subscribe { resId ->
      view.setImageDrawable(resId?.let { context.resources.getDrawable(it) })
    }
  }
}

@NoCompose
class Space(context: Context) : Component<android.widget.Space>(context) {

  override val view: android.widget.Space by lazy { android.widget.Space(context) }
}

@NoCompose
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
        val viewToAdd = if (it) viewT else viewF
        view.addView(viewToAdd.view)
        val layoutParams = viewToAdd.view.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
        layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
      }
    }
}