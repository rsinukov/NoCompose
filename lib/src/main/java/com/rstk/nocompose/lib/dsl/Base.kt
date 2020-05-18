package com.rstk.nocompose.lib.dsl

import android.content.Context
import android.view.View
import com.rstk.nocompose.lib.components.*

@NoCompose
class ViewTree<T>(val context: Context, val state: T)

@DslMarker
annotation class NoCompose

fun <V : View, S> viewTree(context: Context, state: S, builder: ViewTree<S>.() -> Component<V>): Component<V> {
  return ViewTree(context, state).let(builder)
}