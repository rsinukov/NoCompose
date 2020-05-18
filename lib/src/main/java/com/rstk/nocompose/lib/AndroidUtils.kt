package com.rstk.nocompose.lib

import android.content.Context
import kotlin.math.roundToInt

fun Int.toDp(context: Context): Int = (context.resources.displayMetrics.density * this).roundToInt()