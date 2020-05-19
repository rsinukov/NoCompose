package com.rstk.nocompose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rstk.nocompose.lib.components.ContainerComponent.Size.Const
import com.rstk.nocompose.lib.components.ContainerComponent.Size.Wrap
import com.rstk.nocompose.lib.components.Label.TextAlignment
import com.rstk.nocompose.lib.components.Margin
import com.rstk.nocompose.lib.components.Padding
import com.rstk.nocompose.lib.components.Row
import com.rstk.nocompose.lib.databinding.StateValue
import com.rstk.nocompose.lib.databinding.static
import com.rstk.nocompose.lib.dsl.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val view = viewTree(this, State.create()) {
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

data class State private constructor(
  val title: StateValue<String>,
  val desc: StateValue<String>,
  val count: StateValue<Int>,
  val showTrue: StateValue<Boolean>
) {
  companion object {
    fun create(): State {
      return State(
        title = StateValue.createDefault("user1"),
        desc = StateValue.createDefault("I like Kotlin"),
        count = StateValue.createDefault(0),
        showTrue = StateValue.createDefault(true)
      )
    }
  }
}