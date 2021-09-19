package com.kristianskokars.shotsandbeer.injection

import android.content.Context
import androidx.room.Room
import com.kristianskokars.shotsandbeer.common.HIGH_SCORE_DATABASE
import com.kristianskokars.shotsandbeer.repository.GameRepository
import com.kristianskokars.shotsandbeer.repository.cache.GameDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InjectionModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideGameDatabase() = Room
        .databaseBuilder(context, GameDatabase::class.java, HIGH_SCORE_DATABASE)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideGameRepository(database: GameDatabase) = GameRepository(database.gameDao())
}
