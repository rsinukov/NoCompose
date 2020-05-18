package com.rstk.nocompose.lib.components

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.rstk.nocompose.lib.dsl.NoCompose
import com.rstk.nocompose.lib.databinding.Observable
import com.rstk.nocompose.lib.databinding.static

class Overlay(context: Context) : ContainerComponent<FrameLayout>(context) {

  enum class Gravity {
    StartTop, StartCenter, StartBottom, CenterTop, Center, CenterBottom, EndTop, EndCenter, EndBottom
  }

  override val view: FrameLayout by lazy {
    FrameLayout(
      context
    )
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
      this
    )
    return this
  }

  @NoCompose
  data class LayoutParams(
    var width: Observable<Size> = static(
      Size.Fill
    ),
    var height: Observable<Size> = static(
      Size.Wrap
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
        Size.Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Size.Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Size.Const -> Math.round(context.resources.displayMetrics.density * it.dp)
      }
      child.view.requestLayout()
    }
    height.subscribe {
      layoutParams.height = when (it) {
        Size.Fill -> LinearLayout.LayoutParams.MATCH_PARENT
        Size.Wrap -> LinearLayout.LayoutParams.WRAP_CONTENT
        is Size.Const -> Math.round(context.resources.displayMetrics.density * it.dp)
      }
      child.view.requestLayout()
    }
    gravity.subscribe {
      when (it) {
        Gravity.StartTop -> layoutParams.gravity = android.view.Gravity.START or android.view.Gravity.TOP
        Gravity.StartCenter -> layoutParams.gravity = android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
        Gravity.StartBottom -> layoutParams.gravity = android.view.Gravity.START or android.view.Gravity.BOTTOM
        Gravity.CenterTop -> layoutParams.gravity = android.view.Gravity.CENTER_HORIZONTAL or android.view.Gravity.TOP
        Gravity.Center -> layoutParams.gravity = android.view.Gravity.CENTER
        Gravity.CenterBottom -> layoutParams.gravity = android.view.Gravity.CENTER_HORIZONTAL or android.view.Gravity.BOTTOM
        Gravity.EndTop -> layoutParams.gravity = android.view.Gravity.END or android.view.Gravity.TOP
        Gravity.EndCenter -> layoutParams.gravity = android.view.Gravity.END or android.view.Gravity.CENTER_VERTICAL
        Gravity.EndBottom -> layoutParams.gravity = android.view.Gravity.END or android.view.Gravity.BOTTOM
      }
    }
  }
}