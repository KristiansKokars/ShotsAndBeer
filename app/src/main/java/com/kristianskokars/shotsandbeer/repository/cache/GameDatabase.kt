package com.kristianskokars.shotsandbeer.repository.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kristianskokars.shotsandbeer.repository.models.HighScore

@Database(entities = [HighScore::class], version = 1, exportSchema = false)
abstract class GameDatabase: RoomDatabase() {
    abstract fun gameDao(): HighScoreDao
}
