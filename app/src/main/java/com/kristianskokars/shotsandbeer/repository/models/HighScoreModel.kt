package com.kristianskokars.shotsandbeer.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kristianskokars.shotsandbeer.common.HIGH_SCORE_TABLE

@Entity(tableName = HIGH_SCORE_TABLE)
data class HighScoreModel(
    @PrimaryKey val id: Int,
    val date: String,
    val time: String,
    val attempts: String
)
