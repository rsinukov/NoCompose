package com.rstk.nocompose

import android.content.Context
import android.view.View

@NoCompose
class ViewTree<T>(val context: Context, val state: T)

@DslMarker
annotation class NoCompose

fun <T> ViewTree<T>.column(builder: Column.() -> Unit): Column {
  val column = Column(context)
  column.builder()
  return column
}

fun <T> ViewTree<T>.row(builder: Row.() -> Unit): Row {
  return Row(context).apply(builder)
}

fun <T> ViewTree<T>.label(builder: Label.() -> Unit): Label {
  return Label(context).apply(builder)
}

fun <T> ViewTree<T>.button(builder: Button.() -> Unit): Button {
  return Button(context).apply(builder)
}

fun <T> ViewTree<T>.image(builder: Image.() -> Unit): Image {
  return Image(context).apply(builder)
}

fun <T> ViewTree<T>.space(builder: Space.() -> Unit): Space {
  return Space(context).apply(builder)
}

fun <S> ViewTree<S>.container(
  margin: Observable<Int> = static(0),
  padding: Observable<Int> = static(0),
  childBuilder: () -> Component<out View>
): Container {
  val container = Container(context, childBuilder.invoke())
  container.margin = margin
  container.padding = padding
  return container
}

fun <S> ViewTree<S>.ifComponent(
  value: Observable<Boolean>,
  viewT: Component<out View>,
  viewF: Component<out View>
): If {
  val ifComponent = If(context, viewT, viewF)
  ifComponent.value = value
  return ifComponent
}

fun <V : View, S> viewTree(context: Context, state: S, builder: ViewTree<S>.() -> Component<V>): Component<V> {
  return ViewTree(context, state).let(builder)
}