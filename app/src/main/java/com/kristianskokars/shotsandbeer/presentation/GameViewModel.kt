package com.kristianskokars.shotsandbeer.presentation

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.shotsandbeer.common.*
import com.kristianskokars.shotsandbeer.data.HighScoreRepository
import com.kristianskokars.shotsandbeer.data.model.Difficulty
import com.kristianskokars.shotsandbeer.data.model.GamePiece
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val repository: HighScoreRepository) : ViewModel() {
    private var timer = object : CountDownTimer(MAX_GAME_TIME, 10) {
        override fun onTick(ellapsedTime: Long) {
            val date = Date(MAX_GAME_TIME - ellapsedTime)
            _gameTimer.update { date.time }
        }

        override fun onFinish() {
            onGameOver(false)
        }
    }

    private var answer: List<Int> = emptyList()

    private val _gamePieces = MutableStateFlow<List<GamePiece>>(emptyList())
    private val _gameTimer = MutableStateFlow(0L)
    private val _attemptCount = MutableStateFlow(0)
    private val _difficulty = MutableStateFlow(Difficulty.EASY)
    private val _onGameOver = Channel<Long>()

    val highScores = repository.highScores.asStateFlow(viewModelScope, null)
    val gamePieces = _gamePieces.asStateFlow()
    val gameTimer = _gameTimer.asStateFlow()
    val attemptCount = _attemptCount.asStateFlow()
    val difficulty = _difficulty.asStateFlow()
    val onGameOver = _onGameOver.receiveAsFlow()

    fun setDifficulty(difficulty: Difficulty) {
        _difficulty.value = difficulty
    }

    fun startGame() {
        answer = generateAnswerList()
        _attemptCount.value = 0
        _gamePieces.value = emptyList()
        timer.start()
    }

    fun determineGuessedPieces(input: String) {
        _attemptCount.update { it + 1 }
        val inputNumbers = input.convertInputToIntList()
        val attemptsList = _gamePieces.value.toMutableList()
        val currentAttempt = generatePieces(inputNumbers)

        if (haveAllPiecesBeenFound(currentAttempt)) {
            onGameOver(true)
        }

        attemptsList.addAll(currentAttempt)
        _gamePieces.update { attemptsList }
    }

    private fun haveAllPiecesBeenFound(pieces: List<GamePiece>) =
        pieces.filter { it.state == GamePiece.State.IS_FOUND }.size == difficulty.value.answerLength

    private fun generateAnswerList(): List<Int> {
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
                    pieces.add(GamePiece(id++, digit, state = GamePiece.State.IS_FOUND))
                } else {
                    pieces.add(GamePiece(id++, digit, state = GamePiece.State.IS_GUESSED))
                }
            } else {
                pieces.add(GamePiece(id++, digit))
            }
        }

        return pieces.toList()
    }

    private fun onGameOver(isGameWon: Boolean) {
        launch {
            timer.cancel()
            val time = _gameTimer.value
            val attempts = _attemptCount.value
            _onGameOver.send(_gameTimer.value)
            if (isGameWon) {
                repository.saveHighScore(attempts, time)
            }
        }
    }
}
