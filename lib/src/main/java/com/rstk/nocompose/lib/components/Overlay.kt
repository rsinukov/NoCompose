package com.rstk.nocompose.lib.components

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.rstk.nocompose.lib.databinding.Observable
import com.rstk.nocompose.lib.databinding.static
import com.rstk.nocompose.lib.dsl.NoCompose
import com.rstk.nocompose.lib.toDp
import android.view.Gravity as AndroidGravity

class Overlay(context: Context) : ContainerComponent<FrameLayout>(context) {

  enum class Gravity {
    StartTop, StartCenter, StartBottom, CenterTop, Center, CenterBottom, EndTop, EndCenter, EndBottom
  }

  override val view: FrameLayout by lazy { FrameLayout(context) }

  init {
    init()
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
    var margin: Observable<Margin> = static(Margin.create(0))
  )

  private fun <V : View> layoutChild(
    width: Observable<Size>,
    height: Observable<Size>,
    gravity: Observable<Gravity>,
    margin: Observable<Margin>,
    child: Component<V>
  ) {
    val layoutParams = FrameLayout.LayoutParams(0, 0)
    child.view.layoutParams = layoutParams

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
    addMargins(child = child, margin = margin)
  }
}