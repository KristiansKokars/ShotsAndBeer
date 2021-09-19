package com.kristianskokars.shotsandbeer.repository

import com.kristianskokars.shotsandbeer.repository.cache.HighScoreDao
import com.kristianskokars.shotsandbeer.repository.models.HighScoreModel

class GameRepository(private val gameDao: HighScoreDao) {

    val highScores = gameDao.getHighScores()

    fun insertHighScore(highScoreModel: HighScoreModel) = gameDao.insertHighScore(highScoreModel)
}
