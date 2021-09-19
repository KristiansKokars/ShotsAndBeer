package com.kristianskokars.shotsandbeer.common

fun String.convertInputToIntList(): List<Int> {
    // Validation is done already in the input, so we don't do it more here
    val results = mutableListOf<Int>()
    this.forEach { number -> results.add(number.digitToInt()) }
    return results.toList()
}