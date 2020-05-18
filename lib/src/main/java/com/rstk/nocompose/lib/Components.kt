package com.rstk.nocompose.lib

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import com.rstk.nocompose.lib.ContainerComponent.Size.*
import java.util.*
import kotlin.math.roundToInt
import android.view.Gravity as AndroidGravity

abstract class Component<V : View>(val context: Context) {

  abstract val view: V
}

abstract class ContainerComponent<V : ViewGroup>(context: Context) : Component<V>(context) {

  sealed class Size {
    data class Const(val dp: Int) : Size()
    object Fill : Size()
    object Wrap : Size()
  }

  operator fun <V : View> Component<V>.unaryPlus() {
    this@ContainerComponent.view.addView(view)
  }

  fun condition(value: Observable<Boolean>, viewT: Component<out View>, viewF: Component<out View>) {
    val id = UUID.randomUUID().toString()
    value.subscribe {
      val oldView = view.findViewWithTag<View>(id)
      val index = if (oldView != null) view.indexOfChild(oldView)
      else view.childCount
      view.removeView(oldView)

      val childView = if (it) viewT.view else viewF.view
      childView.tag = id
      view.addView(childView, index)
    }
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
    var width: Observable<Size> = static(
      Fill
    ),
    var height: Observable<Size> = static(
      Wrap
    ),
    var gravity: Observable<Gravity> = static(
      Gravity.Center
    ),
    var weight: Observable<Float> = static(
      0F
    )
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
    var width: Observable<Size> = static(
      Fill
    ),
    var height: Observable<Size> = static(
      Wrap
    ),
    var gravity: Observable<Gravity> = static(
      Gravity.Center
    ),
    var weight: Observable<Float> = static(
      0F
    )
  )
}

abstract class RowColumn(context: Context) : ContainerComponent<LinearLayout>(context) {

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

    var rowGravityValue: Int = AndroidGravity.CENTER_VERTICAL
    var columnGravityValue: Int = AndroidGravity.CENTER_HORIZONTAL

    width.subscribe {
      layoutParams.width = when (it) {
        Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Const -> (context.resources.displayMetrics.density * it.dp).roundToInt()
      }
      child.view.requestLayout()
    }
    height.subscribe {
      layoutParams.height = when (it) {
        Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Const -> (context.resources.displayMetrics.density * it.dp).roundToInt()
      }
      child.view.requestLayout()
    }
    rowGravity.subscribe {
      rowGravityValue = when (it) {
        Row.Gravity.Top -> AndroidGravity.TOP
        Row.Gravity.Center -> AndroidGravity.CENTER_VERTICAL
        Row.Gravity.Bottom -> AndroidGravity.BOTTOM
      }
      layoutParams.gravity = rowGravityValue or columnGravityValue
      child.view.requestLayout()
    }
    columnGravity.subscribe {
      columnGravityValue = when (it) {
        Column.Gravity.Start -> AndroidGravity.START
        Column.Gravity.Center -> AndroidGravity.CENTER_HORIZONTAL
        Column.Gravity.End -> AndroidGravity.END
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

class Overlay(context: Context) : ContainerComponent<FrameLayout>(context) {

  enum class Gravity {
    StartTop, StartCenter, StartBottom, CenterTop, Center, CenterBottom, EndTop, EndCenter, EndBottom
  }

  override val view: FrameLayout by lazy { FrameLayout(context) }

  fun <V : View> Component<V>.layout(
    paramsBuilder: LayoutParams.() -> Unit
  ): Component<V> {
    val params = LayoutParams()
    params.paramsBuilder()
    layoutChild(
      params.width,
      params.height,
      params.gravity,
      this
    )
    return this
  }

  @NoCompose
  data class LayoutParams(
    var width: Observable<Size> = static(
      Fill
    ),
    var height: Observable<Size> = static(
      Wrap
    ),
    var gravity: Observable<Gravity> = static(
      Gravity.Center
    )
  )

  private fun <V : View> layoutChild(
    width: Observable<Size>,
    height: Observable<Size>,
    gravity: Observable<Gravity>,
    child: Component<V>
  ) {
    val layoutParams = FrameLayout.LayoutParams(0, 0)
    child.view.layoutParams = layoutParams

    width.subscribe {
      layoutParams.width = when (it) {
        Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Const -> Math.round(context.resources.displayMetrics.density * it.dp)
      }
      child.view.requestLayout()
    }
    height.subscribe {
      layoutParams.height = when (it) {
        Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Const -> Math.round(context.resources.displayMetrics.density * it.dp)
      }
      child.view.requestLayout()
    }
    gravity.subscribe {
      when (it) {
        Gravity.StartTop -> layoutParams.gravity = AndroidGravity.START or AndroidGravity.TOP
        Gravity.StartCenter -> layoutParams.gravity = AndroidGravity.START or AndroidGravity.CENTER_VERTICAL
        Gravity.StartBottom -> layoutParams.gravity = AndroidGravity.START or AndroidGravity.BOTTOM
        Gravity.CenterTop -> layoutParams.gravity = AndroidGravity.CENTER_HORIZONTAL or AndroidGravity.TOP
        Gravity.Center -> layoutParams.gravity = AndroidGravity.CENTER
        Gravity.CenterBottom -> layoutParams.gravity = AndroidGravity.CENTER_HORIZONTAL or AndroidGravity.BOTTOM
        Gravity.EndTop -> layoutParams.gravity = AndroidGravity.END or AndroidGravity.TOP
        Gravity.EndCenter -> layoutParams.gravity = AndroidGravity.END or AndroidGravity.CENTER_VERTICAL
        Gravity.EndBottom -> layoutParams.gravity = AndroidGravity.END or AndroidGravity.BOTTOM
      }
    }
  }
}

@NoCompose
class Label(
  context: Context,
  text: Observable<String?>,
  alignment: Observable<TextAlignment>
) : Component<TextView>(context) {

  enum class TextAlignment {
    StartTop, StartCenter, StartBottom, CenterTop, Center, CenterBottom, EndTop, EndCenter, EndBottom
  }

  override val view: TextView by lazy { TextView(context) }

  init {
    text.subscribe { view.text = it }
    alignment.subscribe {
      when (it) {
        TextAlignment.StartTop -> view.gravity = AndroidGravity.START or AndroidGravity.TOP
        TextAlignment.StartCenter -> view.gravity = AndroidGravity.START or AndroidGravity.CENTER_VERTICAL
        TextAlignment.StartBottom -> view.gravity = AndroidGravity.START or AndroidGravity.BOTTOM
        TextAlignment.CenterTop -> view.gravity = AndroidGravity.CENTER_HORIZONTAL or AndroidGravity.TOP
        TextAlignment.Center -> view.gravity = AndroidGravity.CENTER
        TextAlignment.CenterBottom -> view.gravity = AndroidGravity.CENTER_HORIZONTAL or AndroidGravity.BOTTOM
        TextAlignment.EndTop -> view.gravity = AndroidGravity.END or AndroidGravity.TOP
        TextAlignment.EndCenter -> view.gravity = AndroidGravity.END or AndroidGravity.CENTER_VERTICAL
        TextAlignment.EndBottom -> view.gravity = AndroidGravity.END or AndroidGravity.BOTTOM
      }
    }
  }
}

@NoCompose
class Button(
  context: Context,
  text: Observable<String?>,
  onPress: () -> Unit
) : Component<Button>(context) {

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
class Space(context: Context) : Component<Space>(context) {

  override val view: android.widget.Space by lazy { android.widget.Space(context) }
}