package com.rstk.nocompose

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rstk.nocompose.lib.components.ContainerComponent.Size.*
import com.rstk.nocompose.lib.components.Overlay
import com.rstk.nocompose.lib.components.Row
import com.rstk.nocompose.lib.databinding.NullableStateValue
import com.rstk.nocompose.lib.databinding.StateValue
import com.rstk.nocompose.lib.databinding.static
import com.rstk.nocompose.lib.dsl.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val view = viewTree(this, State.create()) {
      column {
        +label(
          text = static("It's my sample")
        )

        +row {
          +image(
            imageResId = state.imageResStart
          ).layout {
            width = static(Const(48))
            height = static(Const(48))
          }

          +column {
            +label(text = state.userName)
            +label(text = state.quote)
          }.layout {
            weight = static(1F)
            height = static(Wrap)
          }

          +image(
            imageResId = state.imageResEnd
          ).layout {
            width = static(Const(32))
            height = static(Const(32))
            gravity = static(Row.Gravity.Center)
          }
        }

        +overlay {
          +image(imageResId = state.imageResEnd)
            .layout {
              gravity = static(Overlay.Gravity.Center)
            }
          +label(text = static("Text on top of image"))
            .layout {
              gravity = static(Overlay.Gravity.Center)
            }
        }.layout {
          weight = static(1F)
        }

        condition(
          value = state.showTrue,
          viewT = label(
            text = static("This is true")
          ).layout {
            width = static(Fill)
            weight = static(1F)
          },
          viewF = label(
            text = static("This is false")
          ).layout {
            width = static(Fill)
            weight = static(1F)
          }
        )

        +button(
          text = static("Change state"),
          onPress = { changeState(state) }
        )
      }
    }

    setContentView(view.view)
  }

  private fun changeState(state: State) {
    if (state.count.value!! % 2 == 0) {
      state.imageResStart.update(R.drawable.ic_brightness_low_black_24dp)
      state.imageResEnd.update(R.drawable.ic_brightness_high_black_24dp)
      state.userName.update("user2")
      state.quote.update("Workers of the world unite!\nAll power to the soviets!")
      state.count.update(state.count.value!! + 1)
      state.showTrue.update(false)
    } else {
      state.imageResStart.update(R.drawable.ic_brightness_high_black_24dp)
      state.imageResEnd.update(R.drawable.ic_brightness_low_black_24dp)
      state.userName.update("user1")
      state.quote.update("I like Kotlin")
      state.count.update(state.count.value!! + 1)
      state.showTrue.update(true)
    }
  }
}

data class State private constructor(
  val imageResStart: NullableStateValue<Int>,
  val imageResEnd: NullableStateValue<Int>,
  val userName: NullableStateValue<String>,
  val quote: NullableStateValue<String>,
  val count: StateValue<Int>,
  val showTrue: StateValue<Boolean>
) {
  companion object {
    fun create(): State {
      return State(
        imageResStart = NullableStateValue.createDefault(R.drawable.ic_brightness_high_black_24dp),
        imageResEnd = NullableStateValue.createDefault(R.drawable.ic_brightness_low_black_24dp),
        userName = NullableStateValue.createDefault("user1"),
        quote = NullableStateValue.createDefault("I like Kotlin"),
        count = StateValue.createDefault(0),
        showTrue = StateValue.createDefault(true)
      )
    }
  }
}
