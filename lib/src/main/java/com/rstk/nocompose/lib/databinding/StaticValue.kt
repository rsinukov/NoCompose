package com.rstk.nocompose.lib.databinding

class StaticValue<T>(private val value: T) : Observable<T> {

  override fun subscribe(listener: (T) -> Unit) {
    listener(value)
  }
}

fun <T> static(value: T): StaticValue<out T> =
  StaticValue(value)