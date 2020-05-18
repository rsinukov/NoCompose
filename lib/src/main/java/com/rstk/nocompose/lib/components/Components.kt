package com.rstk.nocompose.lib.components

import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import com.rstk.nocompose.lib.dsl.NoCompose
import com.rstk.nocompose.lib.databinding.Observable

@NoCompose
class Label(
  context: Context,
  text: Observable<String?>,
  alignment: Observable<TextAlignment>
) : Component<TextView>(context) {

  enum class TextAlignment {
    StartTop, StartCenter, StartBottom, CenterTop, Center, CenterBottom, EndTop, EndCenter, EndBottom
  }

  override val view: TextView by lazy { TextView(context) }

  init {
    init()
    text.subscribe { view.text = it }
    alignment.subscribe {
      when (it) {
        TextAlignment.StartTop -> view.gravity = Gravity.START or Gravity.TOP
        TextAlignment.StartCenter -> view.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        TextAlignment.StartBottom -> view.gravity = Gravity.START or Gravity.BOTTOM
        TextAlignment.CenterTop -> view.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        TextAlignment.Center -> view.gravity = Gravity.CENTER
        TextAlignment.CenterBottom -> view.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        TextAlignment.EndTop -> view.gravity = Gravity.END or Gravity.TOP
        TextAlignment.EndCenter -> view.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        TextAlignment.EndBottom -> view.gravity = Gravity.END or Gravity.BOTTOM
      }
    }
  }
}

@NoCompose
class Button(
  context: Context,
  text: Observable<String?>,
  onPress: () -> Unit
) : Component<Button>(context) {

  override val view: Button = Button(context)

  init {
    init()
    text.subscribe { view.text = it }
    view.setOnClickListener { onPress.invoke() }
  }
}

@NoCompose
class Image(context: Context, imageResId: Observable<Int?>) : Component<ImageView>(context) {

  override val view: ImageView by lazy { ImageView(context) }

  init {
    init()
    imageResId.subscribe { resId ->
      view.setImageDrawable(resId?.let { context.resources.getDrawable(it) })
    }
  }
}

@NoCompose
class Space(context: Context) : Component<Space>(context) {

  override val view: Space by lazy { Space(context) }

  init {
    init()
  }
}