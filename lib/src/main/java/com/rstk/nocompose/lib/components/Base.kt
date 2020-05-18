package com.rstk.nocompose.lib.components

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.rstk.nocompose.lib.databinding.Observable
import java.util.*

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


