package com.kristianskokars.shotsandbeer.data.model

data class GamePiece(
    var id: Int,
    val value: Int,
    val state: State = State.NONE,
) {
    enum class State {
        IS_FOUND, IS_GUESSED, NONE
    }
}
