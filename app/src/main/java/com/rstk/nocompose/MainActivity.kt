package com.rstk.nocompose

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rstk.nocompose.lib.components.Component
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

    val view = viewTree(this, State()) {
      column {
        +label(
          text = static("It's my sample"),
          padding = static(Padding.create(8))
        )

        +userRow(state.user1)
        +userRow(state.user2)

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

  private fun userRow(state: State.UserState): Component<out View> {
    return viewTree(this, state) {
      row {
        +image(
          imageResId = static(R.drawable.ic_brightness_high_black_24dp)
        ).layout {
          width = static(Const(48))
          height = static(Const(48))
          margin = static(Margin.create(8))
        }

        +column {
          +label(
            text = state.name,
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
    }
  }

  private fun changeState(state: State) {
    if (state.count.value % 2 == 0) {
      state.user1.name.update("user2")
      state.user1.desc.update("some desc")
      state.user2.name.update("user1")
      state.user2.desc.update("I like Kotlin")
      state.count.update(state.count.value + 1)
    } else {
      state.user2.name.update("user2")
      state.user2.desc.update("some desc")
      state.user1.name.update("user1")
      state.user1.desc.update("I like Kotlin")
      state.count.update(state.count.value + 1)
    }
  }
}

data class State(
  val count: StateValue<Int> = StateValue.createDefault(0),
  val showTrue: StateValue<Boolean> = StateValue.createDefault(true),
  val user1: UserState = UserState(
    name = StateValue.createDefault("user1"),
    desc = StateValue.createDefault("some desc")
  ),
  val user2: UserState = UserState(
    name = StateValue.createDefault("user2"),
    desc = StateValue.createDefault("I like kotlin")

  )
) {
  data class UserState(
    val name: StateValue<String>,
    val desc: StateValue<String>
  )
}