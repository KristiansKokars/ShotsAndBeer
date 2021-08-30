package com.kristianskokars.shotsandbeer.repository.models

data class GamePiece(
    val value: Int,
    var isGuessed: Boolean = false,
    var isFound: Boolean = false,
    var position: Int? = null
) {
    val valueString = value.toString()
}