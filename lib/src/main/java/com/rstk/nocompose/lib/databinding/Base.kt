package com.rstk.nocompose.lib.databinding

interface Observable<out T> {
  fun subscribe(listener: (T) -> Unit)
}

interface Publisher<in T> {
  fun update(value: T)
}

