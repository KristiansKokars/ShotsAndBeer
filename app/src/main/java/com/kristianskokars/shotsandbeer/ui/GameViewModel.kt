package com.kristianskokars.shotsandbeer.ui

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.kristianskokars.shotsandbeer.App
import com.kristianskokars.shotsandbeer.common.*
import com.kristianskokars.shotsandbeer.repository.GameRepository
import com.kristianskokars.shotsandbeer.repository.models.GamePiece
import com.kristianskokars.shotsandbeer.repository.models.HighScoreModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

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

    val highScores: SharedFlow<List<HighScoreModel>> = _highScores
    val gamePieces: SharedFlow<List<GamePiece>> = _gamePieces
    val gameTimer: SharedFlow<String> = _gameTimer
    val attemptCount: SharedFlow<Int> = _attemptCount
    val onGameOver: SharedFlow<String?> = _onGameOver

    private lateinit var answers: List<Int>

    init {
        App.component.inject(this)
        launchIO {
            repository.highScores.collect { scores ->
                _highScores.tryEmit(scores)
            }
        }
    }

    fun startGame() {
        answers = generateAnswerList()

        _onGameOver.tryEmit(null)
        _attemptCount.tryEmit(0)
        _gamePieces.tryEmit(emptyList())
        timer.start()
    }

    private fun generateAnswerList(): List<Int> {
        // first one will always be a unique number so no need to check
        val answerList = mutableListOf(Random.nextInt(1, 10))
        while (answerList.size != GAME_LIST_SIZE) {
            val value = Random.nextInt(1, 10)
            if (value !in answerList) {
                answerList.add(value)
            }
        }

        return answerList
    }

    fun calculateResults(
        input: String,
        onListUpdated: (insertedAtIndex: Int, itemCount: Int) -> Unit
    ) {
        _attemptCount.tryEmit(_attemptCount.replayCache[0] + 1)
        val inputNumbers = convertInputToIntList(input)
        val attemptsList = _gamePieces.replayCache[0].toMutableList()
        val currentAttempt = generatePieces(inputNumbers)
        val insertedAtIndex = attemptsList.size

        // Checks if all pieces have been found
        if (currentAttempt.filter { it.isFound }.size == GAME_LIST_SIZE) {
            onGameOver(true)
        }

        attemptsList.addAll(currentAttempt)
        _gamePieces.tryEmit(attemptsList)
        onListUpdated(insertedAtIndex, currentAttempt.size)
    }

    private fun convertInputToIntList(input: String): List<Int> {
        // Validation is done already in the input, so we don't do it more here
        val results = mutableListOf<Int>()
        input.forEach { number -> results.add(number.digitToInt()) }
        return results.toList()
    }

    private fun generatePieces(inputNumbers: List<Int>): List<GamePiece> {
        val pieces = mutableListOf<GamePiece>()

        inputNumbers.forEachIndexed { index, digit ->
            if (digit in answers) {
                if (index == answers.indexOf(digit)) {
                    Timber.d("Piece found: $digit")
                    pieces.add(GamePiece(digit, isFound = true))
                } else {
                    Timber.d("Piece guessed: $digit")
                    pieces.add(GamePiece(digit, isGuessed = true))
                }
            } else {
                pieces.add(GamePiece(digit))
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