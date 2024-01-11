package com.kristianskokars.shotsandbeer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kristianskokars.shotsandbeer.data.model.HighScore
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM high_score_table")
    fun getHighScores(): Flow<List<HighScore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHighScore(highScore: HighScore)
}
