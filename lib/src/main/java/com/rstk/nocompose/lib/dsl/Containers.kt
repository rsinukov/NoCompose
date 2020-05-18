package com.rstk.nocompose.lib.dsl

import com.rstk.nocompose.lib.components.Column
import com.rstk.nocompose.lib.components.Overlay
import com.rstk.nocompose.lib.components.Row

fun <S> ViewTree<S>.column(builder: Column.() -> Unit): Column {
  val column = Column(context)
  column.builder()
  return column
}

fun <S> ViewTree<S>.row(builder: Row.() -> Unit): Row {
  return Row(context).apply(builder)
}

fun <S> ViewTree<S>.overlay(builder: Overlay.() -> Unit): Overlay {
  return Overlay(context).apply(builder)
}