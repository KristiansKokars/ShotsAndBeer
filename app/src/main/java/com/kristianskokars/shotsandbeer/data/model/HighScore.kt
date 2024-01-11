package com.kristianskokars.shotsandbeer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kristianskokars.shotsandbeer.common.HIGH_SCORE_TABLE

@Entity(tableName = HIGH_SCORE_TABLE)
data class HighScore(
    @PrimaryKey val id: Int,
    val date: String,
    val timeInMillis: Long,
    val attempts: Int
)
