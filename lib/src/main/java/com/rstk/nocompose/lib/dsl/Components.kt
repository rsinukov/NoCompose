package com.rstk.nocompose.lib.dsl

import com.rstk.nocompose.lib.databinding.Observable
import com.rstk.nocompose.lib.components.Button
import com.rstk.nocompose.lib.components.Image
import com.rstk.nocompose.lib.components.Label
import com.rstk.nocompose.lib.components.Space
import com.rstk.nocompose.lib.databinding.static

fun <S> ViewTree<S>.label(
  text: Observable<String?>,
  alignment: Observable<Label.TextAlignment> = static(
    Label.TextAlignment.Center
  )
): Label {
  return Label(context, text, alignment)
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