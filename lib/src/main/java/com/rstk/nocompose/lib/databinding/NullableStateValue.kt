package com.rstk.nocompose.lib.databinding

class NullableStateValue<T> private constructor() : Observable<T?>,
  Publisher<T?> {

  private var listeners: MutableSet<(T?) -> Unit> = mutableSetOf()
  private var isSet: Boolean = false
  var value: T? = null
    private set

  override fun subscribe(listener: (T?) -> Unit) {
    this.listeners.add(listener)
    val currentValue = value
    if (isSet) listener(currentValue)
  }

  override fun update(value: T?) {
    this.value = value
    isSet = true
    listeners.forEach { it.invoke(value) }
  }

  companion object {
    fun <T> create(): NullableStateValue<T> =
      NullableStateValue()

    fun <T> createDefault(value: T?): NullableStateValue<T> {
      val stateValue = NullableStateValue<T>()
      stateValue.update(value)
      return stateValue
    }
  }
}