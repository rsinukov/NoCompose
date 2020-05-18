Test idea of building declarative UI in pure kotlin

### Features
* Pure Kotlin, no AP, no compiler hacks
* Supports multiple layouts
* Supports binding to a state and reacting on its changes


### Example
```kotlin
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
            imageResId = static(R.drawable.ic_brightness_high_black_24dp)
          ).layout {
            width = static(Const(48))
            height = static(Const(48))
          }

          +column {
            +label(text = state.title, alignment = static(TextAlignment.StartTop))
            +label(text = state.desc, alignment = static(TextAlignment.StartTop))
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
          }
        }

        +space().layout {
          weight = static(1F)
        }

        +button(
          text = static("Change state"),
          onPress = { changeState(state) }
        )
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
```