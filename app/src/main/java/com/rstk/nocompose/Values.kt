package com.rstk.nocompose

interface Observable<out T> {
  fun subscribe(listener: (T) -> Unit)
}

interface Publisher<in T> {
  fun update(value: T)
}

class StaticValue<T>(private val value: T) : Observable<T> {

  override fun subscribe(listener: (T) -> Unit) {
    listener(value)
  }
}

class StateValue<T> private constructor() : Observable<T>, Publisher<T> {

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
    fun <T> create(): StateValue<T> = StateValue()
    fun <T> createDefault(value: T): StateValue<T> {
      val stateValue = StateValue<T>()
      stateValue.update(value)
      return stateValue
    }
  }
}

class NullableStateValue<T> private constructor() : Observable<T?>, Publisher<T?> {

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
    fun <T> create(): NullableStateValue<T> = NullableStateValue()
    fun <T> createDefault(value: T?): NullableStateValue<T> {
      val stateValue = NullableStateValue<T>()
      stateValue.update(value)
      return stateValue
    }
  }
}

fun <T> static(value: T): StaticValue<out T> = StaticValue(value)
