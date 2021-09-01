package com.kristianskokars.shotsandbeer.repository.models

data class GamePiece(
    val value: Int,
    var isGuessed: Boolean = false,
    var isFound: Boolean = false,
    var position: Int? = null
) {
    val valueString = value.toString()

    // For auto-incrementing and adding unique IDs, otherwise DiffUtil in Adapter will be funky
    var id: Int = 0

    init {
        id = idCount++
    }

    companion object {
        var idCount = 0
    }

}