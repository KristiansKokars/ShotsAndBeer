package com.kristianskokars.shotsandbeer.common

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateString(): String {
    val formatter = SimpleDateFormat("dd:MM.yyy HH:mm.ss", Locale.getDefault())
    return formatter.format(Date(this))
}

fun Long.toTimeString(): String {
    val formatter = SimpleDateFormat("mm:ss:SSS", Locale.getDefault())
    return formatter.format(Date(this))
}
