package com.rstk.nocompose.lib.databinding

class StateValue<T> private constructor(_value: T) : Observable<T>, Publisher<T> {

  private val listeners: MutableSet<(T) -> Unit> = mutableSetOf()
  private var hasValue: Boolean = false
  var value: T = _value
    private set

  override fun subscribe(listener: (T) -> Unit) {
    this.listeners.add(listener)
    val currentValue = value
    if (hasValue) listener(currentValue)
  }

  override fun update(value: T) {
    this.value = value
    hasValue = true
    listeners.forEach { it.invoke(value) }
  }

  companion object {
    fun <T> create(): StateValue<T?> = StateValue(null)

    fun <T> createDefault(value: T): StateValue<T> {
      val stateValue = StateValue(value)
      stateValue.update(value)
      return stateValue
    }
  }
}