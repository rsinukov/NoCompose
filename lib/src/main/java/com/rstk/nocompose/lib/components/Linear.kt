package com.rstk.nocompose.lib.components

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.rstk.nocompose.lib.databinding.Observable
import com.rstk.nocompose.lib.databinding.static
import com.rstk.nocompose.lib.dsl.NoCompose
import com.rstk.nocompose.lib.toDp

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
      params.margin,
      this
    )
    return this
  }

  @NoCompose
  data class LayoutParams(
    var width: Observable<Size> = static(Size.Fill),
    var height: Observable<Size> = static(Size.Wrap),
    var gravity: Observable<Gravity> = static(Gravity.Center),
    var margin: Observable<Margin> = static(Margin.create(0)),
    var weight: Observable<Float> = static(0F)
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
      params.margin,
      this
    )
    return this
  }

  @NoCompose
  data class LayoutParams(
    var width: Observable<Size> = static(Size.Fill),
    var height: Observable<Size> = static(Size.Wrap),
    var gravity: Observable<Gravity> = static(Gravity.Center),
    var weight: Observable<Float> = static(0F),
    var margin: Observable<Margin> = static(Margin.create(0))
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
    margin: Observable<Margin>,
    child: Component<V>
  ) {
    val layoutParams = LinearLayout.LayoutParams(0, 0)
    child.view.layoutParams = layoutParams

    var rowGravityValue: Int = Gravity.CENTER_VERTICAL
    var columnGravityValue: Int = Gravity.CENTER_HORIZONTAL

    width.subscribe {
      layoutParams.width = when (it) {
        Size.Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Size.Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Size.Const -> it.dp.toDp(context)
      }
      child.view.requestLayout()
    }
    height.subscribe {
      layoutParams.height = when (it) {
        Size.Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Size.Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Size.Const -> it.dp.toDp(context)
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
    addMargins(child = child, margin = margin)
  }
}