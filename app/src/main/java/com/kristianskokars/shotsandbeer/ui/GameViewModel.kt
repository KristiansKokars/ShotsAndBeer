package com.kristianskokars.shotsandbeer.ui

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.kristianskokars.shotsandbeer.App
import com.kristianskokars.shotsandbeer.common.*
import com.kristianskokars.shotsandbeer.repository.GameRepository
import com.kristianskokars.shotsandbeer.repository.models.Difficulty
import com.kristianskokars.shotsandbeer.repository.models.GamePiece
import com.kristianskokars.shotsandbeer.repository.models.HighScore
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class GameViewModel : ViewModel() {

    @Inject lateinit var repository: GameRepository

    private var timer = object : CountDownTimer(MAX_GAME_TIME, 10) {
        override fun onTick(ellapsedTime: Long) {
            val date = Date(MAX_GAME_TIME - ellapsedTime)
            _gameTimer.tryEmit(date.time.toTimeString())
        }

        override fun onFinish() {
            onGameOver(false)
        }
    }

    private var answer: List<Int> = emptyList()

    private val _highScores = MutableStateFlow<List<HighScore>>(emptyList())
    private val _gamePieces = MutableStateFlow<List<GamePiece>>(emptyList())
    private val _gameTimer = MutableStateFlow("00:00:00")
    private val _attemptCount = MutableStateFlow(0)
    private val _difficulty = MutableStateFlow(Difficulty.EASY)
    private val _onGameOver = MutableSharedFlow<String?>(replay = 1)

    val highScores = _highScores.asStateFlow()
    val gamePieces = _gamePieces.asStateFlow()
    val gameTimer = _gameTimer.asStateFlow()
    val attemptCount = _attemptCount.asStateFlow()
    val difficulty = _difficulty.asStateFlow()
    val onGameOver = _onGameOver.asSharedFlow()

    init {
        App.component.inject(this)
        launchIO {
            repository.highScores.collect { scores ->
                _highScores.value = scores
            }
        }
    }

    fun setDifficulty(difficulty: Difficulty) {
        _difficulty.value = difficulty
    }

    fun startGame() {
        answer = generateAnswerList()

        _onGameOver.tryEmit(null)
        _attemptCount.value = 0
        _gamePieces.value = emptyList()
        timer.start()
    }

    fun calculateResults(input: String) {
        _attemptCount.update { it + 1 }
        val inputNumbers = input.convertInputToIntList()
        val attemptsList = _gamePieces.value.toMutableList()
        val currentAttempt = generatePieces(inputNumbers)

        // Checks if all pieces have been found
        if (currentAttempt.filter { it.isFound }.size == difficulty.value.answerLength) {
            onGameOver(true)
        }

        attemptsList.addAll(currentAttempt)
        _gamePieces.value = attemptsList
    }

    private fun generateAnswerList(): List<Int> {
        // Credit to Edijs Gorbunovs for vastly reducing the logic needed here (and all the other improvements!)
        val values = (0..9).shuffled().toMutableList()
        answer = (0 until difficulty.value.answerLength).map { values.removeFirst() }

        return answer
    }

    private fun generatePieces(inputNumbers: List<Int>): List<GamePiece> {
        val pieces = mutableListOf<GamePiece>()
        var id = _gamePieces.value.size

        inputNumbers.forEachIndexed { index, digit ->
            if (digit in answer) {
                if (index == answer.indexOf(digit)) {
                    pieces.add(GamePiece(id++, digit, isFound = true))
                } else {
                    pieces.add(GamePiece(id++, digit, isGuessed = true))
                }
            } else {
                pieces.add(GamePiece(id++, digit))
            }
        }

        return pieces.toList()
    }

    private fun onGameOver(isGameWon: Boolean) {
        timer.cancel()
        val score = _gameTimer.value
        val attempts = _attemptCount.value
        _onGameOver.tryEmit(_gameTimer.value)

        if (isGameWon) {
            launchIO {
                val id = _highScores.replayCache.lastOrNull()?.maxOfOrNull { it.id }?.plus(1) ?: 0
                repository.insertHighScore(HighScore(id, Date().time.toDateString(), score, attempts.toString()))
            }
        }
    }
}
