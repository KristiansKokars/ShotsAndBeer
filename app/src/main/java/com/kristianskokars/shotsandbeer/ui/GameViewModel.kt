package com.kristianskokars.shotsandbeer.ui

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.kristianskokars.shotsandbeer.App
import com.kristianskokars.shotsandbeer.common.GAME_LIST_SIZE
import com.kristianskokars.shotsandbeer.common.GAME_NUMBER_CAP
import com.kristianskokars.shotsandbeer.common.MAX_GAME_TIME
import com.kristianskokars.shotsandbeer.common.launchIO
import com.kristianskokars.shotsandbeer.repository.GameRepository
import com.kristianskokars.shotsandbeer.repository.models.GamePiece
import com.kristianskokars.shotsandbeer.repository.models.HighScoreModel
import com.testdevlab.numbertapper.common.toDateString
import com.testdevlab.numbertapper.common.toTimeString
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
    private val _onGameOver = MutableSharedFlow<String?>(replay = 1)
    private val _attempts = MutableSharedFlow<Int>(replay = 1)

    val highScores: SharedFlow<List<HighScoreModel>> = _highScores
    val gamePieces: SharedFlow<List<GamePiece>> = _gamePieces
    val gameTimer: SharedFlow<String> = _gameTimer
    val onGameOver: SharedFlow<String?> = _onGameOver
    val attempts: SharedFlow<Int> = _attempts

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

    private fun generatePieces(): List<GamePiece> {
        // TODO: Violation of Single-responsibility, this does too much
        // first one will always be a unique number so no need to check
        val results = mutableListOf(Random.nextInt(1, GAME_NUMBER_CAP))

        while (results.size != GAME_LIST_SIZE) {
            val value = Random.nextInt(1, GAME_NUMBER_CAP)
            if (value !in results) {
                results.add(value)
            }
        }

        val pieces = mutableListOf<GamePiece>()
        for (i in 1 until GAME_NUMBER_CAP) {
            pieces.add(GamePiece(i))
        }

        pieces.forEach { piece ->
            if (piece.value in results)
            {
                piece.position = results.indexOf(piece.value)
            }
        }

        return pieces.toList()
    }

    fun calculateResults(input: String) {
        _attempts.tryEmit(_attempts.replayCache[0] + 1)
        val inputNumbers = convertInputToResults(input)
        val results = gamePieces.replayCache[0].map { it.copy() }

        // filter before hand maybe?
        results.forEach { piece ->
            if (piece.position != null && piece.value in inputNumbers)
            {
                if (piece.position == inputNumbers.indexOf(piece.value))
                {
                    Timber.d("Piece found: ${piece.value}")
                    piece.isFound = true
                } else {
                    Timber.d("Piece guessed: ${piece.value}, position of ${piece.position}")
                    piece.isGuessed = true
                }
            }
        }

        if ( results.filter { it.position != null && it.isFound }.size == GAME_LIST_SIZE)
        {
            onGameOver(true)
        }

        _gamePieces.tryEmit(results)
    }

    private fun convertInputToResults(input: String) : List<Int> {
        // Validation is done already in the input, so this should not be a problem, hopefully this
        // counts as clean code

        // Conversion
        val results = mutableListOf<Int>()
        input.forEach { number -> results.add(number.digitToInt()) }
        Timber.d("Converted input $input to a list of Int: $results")
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