package com.kristianskokars.shotsandbeer.di

import android.content.Context
import androidx.room.Room
import com.kristianskokars.shotsandbeer.common.HIGH_SCORE_DATABASE
import com.kristianskokars.shotsandbeer.data.HighScoreRepository
import com.kristianskokars.shotsandbeer.data.local.GameDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideGameDatabase(@ApplicationContext context: Context) = Room
        .databaseBuilder(context, GameDatabase::class.java, HIGH_SCORE_DATABASE)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideHighScoreRepository(database: GameDatabase) = HighScoreRepository(Dispatchers.IO, database.gameDao())
}
