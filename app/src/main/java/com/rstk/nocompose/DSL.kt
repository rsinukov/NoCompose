package com.rstk.nocompose

import android.content.Context
import android.view.View

@NoCompose
class ViewTree<T>(val context: Context, val state: T)

@DslMarker
annotation class NoCompose

fun <S> ViewTree<S>.column(builder: Column.() -> Unit): Column {
  val column = Column(context)
  column.builder()
  return column
}

fun <S> ViewTree<S>.row(builder: Row.() -> Unit): Row {
  return Row(context).apply(builder)
}

fun <S> ViewTree<S>.label(text: Observable<String?>): Label {
  return Label(context, text)
}

fun <S> ViewTree<S>.button(
  text: Observable<String?>,
  onPress: () -> Unit
): Button {
  return Button(context, text, onPress)
}

fun <S> ViewTree<S>.image(imageResId: Observable<Int?>): Image {
  return Image(context, imageResId)
}

fun <S> ViewTree<S>.space(): Space {
  return Space(context)
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