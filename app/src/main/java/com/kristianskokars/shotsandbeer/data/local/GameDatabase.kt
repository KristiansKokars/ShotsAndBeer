package com.kristianskokars.shotsandbeer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kristianskokars.shotsandbeer.data.model.HighScore

@Database(entities = [HighScore::class], version = 1, exportSchema = false)
abstract class GameDatabase: RoomDatabase() {
    abstract fun gameDao(): HighScoreDao
}
