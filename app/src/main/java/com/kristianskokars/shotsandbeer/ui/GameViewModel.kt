package com.kristianskokars.shotsandbeer.ui

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.kristianskokars.shotsandbeer.App
import com.kristianskokars.shotsandbeer.common.*
import com.kristianskokars.shotsandbeer.repository.GameRepository
import com.kristianskokars.shotsandbeer.repository.models.Difficulty
import com.kristianskokars.shotsandbeer.repository.models.GamePiece
import com.kristianskokars.shotsandbeer.repository.models.HighScoreModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

class GameViewModel : ViewModel() {

    @Inject
    lateinit var repository: GameRepository

    private var timer = object : CountDownTimer(MAX_GAME_TIME, 10) {
        override fun onTick(ellapsedTime: Long) {
            val date = Date(MAX_GAME_TIME - ellapsedTime)
            _gameTimer.tryEmit(date.time.toTimeString())
        }

        override fun onFinish() {
            onGameOver(false)
        }
    }

    private val _highScores = MutableSharedFlow<List<HighScoreModel>>(replay = 1)
    private val _gamePieces = MutableSharedFlow<List<GamePiece>>(replay = 1)
    private val _gameTimer = MutableSharedFlow<String>(replay = 1)
    private val _attemptCount = MutableSharedFlow<Int>(replay = 1)
    private val _onGameOver = MutableSharedFlow<String?>(replay = 1)

    private var answer: List<Int> = emptyList()
    var difficulty = MutableSharedFlow<Difficulty>(replay = 1)

    val highScores: SharedFlow<List<HighScoreModel>> = _highScores
    val gamePieces: SharedFlow<List<GamePiece>> = _gamePieces
    val gameTimer: SharedFlow<String> = _gameTimer
    val attemptCount: SharedFlow<Int> = _attemptCount
    val onGameOver: SharedFlow<String?> = _onGameOver



    init {
        App.component.inject(this)
        launchIO {
            repository.highScores.collect { scores ->
                _highScores.tryEmit(scores)
            }
        }
        launchMain {
            difficulty.emit(Difficulty.EASY)
        }
    }

    fun startGame() {
        answer = generateAnswerList()

        _onGameOver.tryEmit(null)
        _attemptCount.tryEmit(0)
        _gamePieces.tryEmit(emptyList())
        timer.start()
    }

    private fun generateAnswerList(): List<Int> {
        // Credit to Edijs Gorbunovs for vastly reducing the logic needed here (and all the other improvements!)
        val values = (0..9).shuffled().toMutableList()
        answer = (0 until difficulty.replayCache[0].answerLength).map { values.removeFirst() }

        return answer
    }

    fun calculateResults(input: String) {
        _attemptCount.tryEmit(_attemptCount.replayCache[0] + 1)
        val inputNumbers = input.convertInputToIntList()
        val attemptsList = _gamePieces.replayCache[0].toMutableList()
        val currentAttempt = generatePieces(inputNumbers)

        // Checks if all pieces have been found
        if (currentAttempt.filter { it.isFound }.size == difficulty.replayCache[0].answerLength) {
            onGameOver(true)
        }

        attemptsList.addAll(currentAttempt)
        _gamePieces.tryEmit(attemptsList)
    }

    private fun generatePieces(inputNumbers: List<Int>): List<GamePiece> {
        val pieces = mutableListOf<GamePiece>()
        var id = _gamePieces.replayCache[0].size

        inputNumbers.forEachIndexed { index, digit ->
            if (digit in answer) {
                if (index == answer.indexOf(digit)) {
                    pieces.add(GamePiece(id++ , digit, isFound = true))
                } else {
                    pieces.add(GamePiece(id++ , digit, isGuessed = true))
                }
            } else {
                pieces.add(GamePiece(id++, digit))
            }
        }

        return pieces.toList()
    }

    private fun onGameOver(isGameWon: Boolean) {
        timer.cancel()
        val score = _gameTimer.replayCache[0]
        val attempts = _attemptCount.replayCache[0]
        _onGameOver.tryEmit(_gameTimer.replayCache[0])

        if (isGameWon) {
            launchIO {
                val id = _highScores.replayCache.lastOrNull()?.maxOfOrNull { it.id }?.plus(1) ?: 0
                repository.insertHighScore(HighScoreModel(id, Date().time.toDateString(), score, attempts.toString()))
            }
        }
    }
}
