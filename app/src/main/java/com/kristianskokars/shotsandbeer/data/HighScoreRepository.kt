package com.kristianskokars.shotsandbeer.data

import com.kristianskokars.shotsandbeer.common.toDateString
import com.kristianskokars.shotsandbeer.data.local.HighScoreDao
import com.kristianskokars.shotsandbeer.data.model.HighScore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Date

class HighScoreRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val gameDao: HighScoreDao
) {
    val highScores = gameDao.getHighScores()

    suspend fun saveHighScore(attempts: Int, time: Long) = withContext(ioDispatcher) {
        val id = gameDao.getHighScores().first().maxOfOrNull { it.id }?.plus(1) ?: 0
        val highScore = HighScore(id, Date().time.toDateString(), time, attempts)
        gameDao.insertHighScore(highScore)
    }
}
