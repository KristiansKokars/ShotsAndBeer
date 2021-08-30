package com.kristianskokars.shotsandbeer.common

const val TAG_NAME = "ShotsAndBeer"
const val HIGH_SCORE_TABLE = "high_score_table"
const val HIGH_SCORE_DATABASE = "high_score_database"
const val MAX_GAME_TIME = 1000 * 60 * 60L

// Game Values
// Controls the available values in guessing, excluding the last value (so 9 means it will generate up to 8) will have problems if set to more than 10
const val GAME_NUMBER_CAP = 9
// Controls how long a value the user will have to guess (4152 is 4), will break if there is more than possible numbers
const val GAME_LIST_SIZE = 4
