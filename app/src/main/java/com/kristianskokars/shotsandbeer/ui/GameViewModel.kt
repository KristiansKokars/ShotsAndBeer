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
    private val _attempts = MutableSharedFlow<Int>(replay = 1)
    private val _onGameOver = MutableSharedFlow<String?>(replay = 1)

    val highScores: SharedFlow<List<HighScoreModel>> = _highScores
    val gamePieces: SharedFlow<List<GamePiece>> = _gamePieces
    val gameTimer: SharedFlow<String> = _gameTimer
    val attempts: SharedFlow<Int> = _attempts
    val onGameOver: SharedFlow<String?> = _onGameOver

    init {
        App.component.inject(this)
        launchIO {
            repository.highScores.collect { scores ->
                _highScores.tryEmit(scores)
            }
        }
    }

    fun startGame() {
        val pieces = generatePieces() // we could move this to tryEmit, but this is useful to keep for debugging

        _onGameOver.tryEmit(null)
        _attempts.tryEmit(0)
        _gamePieces.tryEmit(pieces)
        timer.start()
    }

    // I am unsure if I should divide this more due to Single Responsibility or not?
    private fun generatePieces(): List<GamePiece> {
        val valuesToMark = randomPositionsToMark()

        val pieces = mutableListOf<GamePiece>()
        for (i in 1 until GAME_NUMBER_CAP) {
            pieces.add(GamePiece(i))
        }

        // Marks the pieces in their appropriate locations
        pieces.forEach { piece ->
            if (piece.value in valuesToMark) {
                piece.position = valuesToMark.indexOf(piece.value)
            }
        }

        return pieces.toList()
    }

    private fun randomPositionsToMark(): List<Int> {
        // first one will always be a unique number so no need to check
        val markedValues = mutableListOf(Random.nextInt(1, GAME_NUMBER_CAP))
        while (markedValues.size != GAME_LIST_SIZE) {
            val value = Random.nextInt(1, GAME_NUMBER_CAP)
            if (value !in markedValues) {
                markedValues.add(value)
            }
        }

        return markedValues
    }

    fun calculateResults(input: String) {
        _attempts.tryEmit(_attempts.replayCache[0] + 1)
        val inputNumbers = convertInputToResults(input)
        val results = gamePieces.replayCache[0].map { it.copy() }

        // checks only positions that are in the answer
        // (works thanks to references, hope it counts as clean)
        results.filter { it.position != null }.forEach { piece ->
            if (piece.value in inputNumbers) {
                if (piece.position == inputNumbers.indexOf(piece.value)) {
                    Timber.d("Piece found: ${piece.value}")
                    piece.isFound = true
                } else {
                    Timber.d("Piece guessed: ${piece.value}, position of ${piece.position}")
                    piece.isGuessed = true
                }
            } else { // when pieces are previously found and not in the current input, wipe their status
                piece.isFound = false
                piece.isGuessed = false
            }
        }

        // Checks if all pieces have been found
        if (results.filter { it.isFound }.size == GAME_LIST_SIZE) {
            onGameOver(true)
        }

        _gamePieces.tryEmit(results)
    }

    private fun convertInputToResults(input: String): List<Int> {
        // Validation is done already in the input, so we don't do it more here
        val results = mutableListOf<Int>()
        input.forEach { number -> results.add(number.digitToInt()) }
        return results.toList()
    }

    private fun onGameOver(isGameWon: Boolean) {
        timer.cancel()
        val score = _gameTimer.replayCache[0]
        val attempts = _attempts.replayCache[0]
        _onGameOver.tryEmit(_gameTimer.replayCache[0])

        if (isGameWon) {
            launchIO {
                val id = _highScores.replayCache.lastOrNull()?.maxOfOrNull { it.id }?.plus(1) ?: 0
                repository.insertHighScore(HighScoreModel(id, Date().time.toDateString(), score, attempts.toString()))
            }
        }
    }

}