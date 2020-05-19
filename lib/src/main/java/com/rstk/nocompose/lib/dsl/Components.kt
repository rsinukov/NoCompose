package com.rstk.nocompose.lib.dsl

import com.rstk.nocompose.lib.components.*
import com.rstk.nocompose.lib.databinding.Observable
import com.rstk.nocompose.lib.databinding.static

fun <S> ViewTree<S>.label(
  text: Observable<String?>,
  alignment: Observable<Label.TextAlignment> = static(Label.TextAlignment.Center),
  padding: Observable<Padding> = static(Padding.create(0))
): Label {
  return Label(context, text, alignment).apply {
    setPadding(padding)
  }
}

fun <S> ViewTree<S>.button(
  text: Observable<String?>,
  padding: Observable<Padding> = static(Padding.create(0)),
  onPress: () -> Unit
): Button {
  return Button(context, text, onPress).apply {
    setPadding(padding)
  }
}

fun <S> ViewTree<S>.image(
  imageResId: Observable<Int?>,
  padding: Observable<Padding> = static(Padding.create(0))
): Image {
  return Image(context, imageResId).apply {
    setPadding(padding)
  }
}

fun <S> ViewTree<S>.space(): Space {
  return Space(context)
}