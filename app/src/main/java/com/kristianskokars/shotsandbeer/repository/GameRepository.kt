package com.kristianskokars.shotsandbeer.repository

import com.kristianskokars.shotsandbeer.repository.cache.GameDao
import com.kristianskokars.shotsandbeer.repository.models.HighScoreModel

class GameRepository(private val gameDao: GameDao) {
    val highScores = gameDao.getHighScores()

    fun insertHighScore(highScoreModel: HighScoreModel) = gameDao.insertHighScore(highScoreModel)
}