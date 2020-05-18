package com.rstk.nocompose.lib.components

import android.view.View
import android.view.ViewGroup
import com.rstk.nocompose.lib.databinding.Observable
import com.rstk.nocompose.lib.toDp

fun <V : View> addMargins(child: Component<V>, margin: Observable<Margin>) {
  val layoutParams = child.view.layoutParams as ViewGroup.MarginLayoutParams
  val context = child.view.context
  margin.subscribe {
    layoutParams.marginStart = it.start.toDp(context)
    layoutParams.topMargin = it.top.toDp(context)
    layoutParams.marginEnd = it.end.toDp(context)
    layoutParams.bottomMargin = it.bottom.toDp(context)
    child.view.requestLayout()
  }
}