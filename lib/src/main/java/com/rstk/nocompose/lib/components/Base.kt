package com.rstk.nocompose.lib.components

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.rstk.nocompose.lib.databinding.Observable
import com.rstk.nocompose.lib.databinding.static
import com.rstk.nocompose.lib.toDp
import java.util.*

abstract class Component<V : View>(val context: Context) {

  abstract val view: V

  var padding: Observable<Padding> = static(Padding.create(0))

  protected fun init() {
    padding.subscribe {
      view.setPaddingRelative(
        it.start.toDp(view.context),
        it.top.toDp(view.context),
        it.end.toDp(view.context),
        it.bottom.toDp(view.context)
      )
    }
  }
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

data class Padding private constructor(
  val start: Int,
  val top: Int,
  val end: Int,
  val bottom: Int
) {
  companion object {
    fun create(padding: Int) = Padding(padding, padding, padding, padding)

    fun create(horizontal: Int = 0, vertical: Int = 0) = Padding(
      start = horizontal,
      top = vertical,
      end = horizontal,
      bottom = vertical
    )

    fun create(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) = Padding(
      start = start,
      top = top,
      end = end,
      bottom = bottom
    )
  }
}

data class Margin private constructor(
  val start: Int,
  val top: Int,
  val end: Int,
  val bottom: Int
) {
  companion object {
    fun create(margin: Int) = Margin(margin, margin, margin, margin)

    fun create(horizontal: Int = 0, vertial: Int = 0) = Margin(
      start = horizontal,
      top = vertial,
      end = horizontal,
      bottom = vertial
    )

    fun create(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) = Margin(
      start = start,
      top = top,
      end = end,
      bottom = bottom
    )
  }
}


