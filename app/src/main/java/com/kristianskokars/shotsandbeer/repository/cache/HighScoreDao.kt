package com.kristianskokars.shotsandbeer.repository.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kristianskokars.shotsandbeer.repository.models.HighScoreModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM high_score_table")
    fun getHighScores(): Flow<List<HighScoreModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHighScore(highScoreModel: HighScoreModel)
}
