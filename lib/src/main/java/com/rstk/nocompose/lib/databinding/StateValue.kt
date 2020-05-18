package com.rstk.nocompose.lib.databinding

class StateValue<T> private constructor() : Observable<T>,
  Publisher<T> {

  private var listeners: MutableSet<(T) -> Unit> = mutableSetOf()
  var value: T? = null
    private set

  override fun subscribe(listener: (T) -> Unit) {
    this.listeners.add(listener)
    val currentValue = value
    if (currentValue != null) listener(currentValue)
  }

  override fun update(value: T) {
    this.value = value
    listeners.forEach { it.invoke(value) }
  }

  companion object {
    fun <T> create(): StateValue<T> =
      StateValue()

    fun <T> createDefault(value: T): StateValue<T> {
      val stateValue = StateValue<T>()
      stateValue.update(value)
      return stateValue
    }
  }
}