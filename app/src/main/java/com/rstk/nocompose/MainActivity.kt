package com.rstk.nocompose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rstk.nocompose.lib.components.*
import com.rstk.nocompose.lib.components.ContainerComponent.Size.Const
import com.rstk.nocompose.lib.components.ContainerComponent.Size.Wrap
import com.rstk.nocompose.lib.components.Label.TextAlignment
import com.rstk.nocompose.lib.databinding.StateValue
import com.rstk.nocompose.lib.databinding.combineWith
import com.rstk.nocompose.lib.databinding.map
import com.rstk.nocompose.lib.databinding.static
import com.rstk.nocompose.lib.dsl.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val view = viewTree(this, State()) {
      column {
        +label(
          text = static("It's my sample"),
          padding = static(Padding.create(8))
        )

        +row {
          +image(
            imageResId = static(R.drawable.ic_brightness_high_black_24dp)
          ).layout {
            width = static(Const(48))
            height = static(Const(48))
            margin = static(Margin.create(8))
          }

          +column {
            +label(
              text = state.title,
              alignment = static(TextAlignment.StartTop),
              padding = static(Padding.create(vertical = 4))
            )
            +label(
              text = state.desc,
              alignment = static(TextAlignment.StartTop),
              padding = static(Padding.create(bottom = 8))
            )
          }.layout {
            weight = static(1F)
            height = static(Wrap)
          }

          +image(
            imageResId = static(R.drawable.ic_brightness_low_black_24dp)
          ).layout {
            width = static(Const(32))
            height = static(Const(32))
            gravity = static(Row.Gravity.Center)
            margin = static(Margin.create(8))
          }
        }

        +space().layout {
          weight = static(1F)
        }

        +button(
          text = static("Change state"),
          onPress = { changeState(state) }
        ).layout {
          margin = static(Margin.create(8))
        }
      }
    }

    setContentView(view.view)
  }

  private fun changeState(state: State) {
    if (state.count.value % 2 == 0) {
      state.title.update("user2")
      state.desc.update("some description")
      state.count.update(state.count.value + 1)
    } else {
      state.title.update("user1")
      state.desc.update("I like Kotlin")
      state.count.update(state.count.value + 1)
    }
  }
}

data class State(
  val title: StateValue<String> = StateValue.createDefault("user1"),
  val desc: StateValue<String> = StateValue.createDefault("I like Kotlin"),
  val count: StateValue<Int> = StateValue.createDefault(0),
  val showTrue: StateValue<Boolean> = StateValue.createDefault(true)
)