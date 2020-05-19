package com.rstk.nocompose.lib.databinding

fun <T, R> Observable<out T>.map(mapper: (T) -> R): Observable<out R> {
  return object : Observable<R> {
    override fun subscribe(listener: (R) -> Unit) {
      this@map.subscribe { value -> listener.invoke(mapper(value)) }
    }
  }
}

fun <T, R, V> Observable<out T>.combineWith(other: Observable<out R>, mapper: (T, R) -> V): Observable<out V> {
  return object : Observable<V> {
    override fun subscribe(listener: (V) -> Unit) {
      var value1: T? = null
      var value2: R? = null

      fun notify() {
        val value1Local = value1
        val value2local = value2
        if (value1Local != null && value2local != null) {
          listener.invoke(mapper(value1Local, value2local))
        }
      }

      this@combineWith.subscribe {
        value1 = it
        notify()
      }
      other.subscribe {
        value2 = it
        notify()
      }
    }
  }
}