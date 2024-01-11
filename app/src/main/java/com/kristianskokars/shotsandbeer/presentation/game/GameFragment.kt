package com.kristianskokars.shotsandbeer.presentation.game

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.common.launchUI
import com.kristianskokars.shotsandbeer.common.navigate
import com.kristianskokars.shotsandbeer.common.toTimeString
import com.kristianskokars.shotsandbeer.common.viewBinding
import com.kristianskokars.shotsandbeer.databinding.FragmentGameBinding
import com.kristianskokars.shotsandbeer.presentation.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {
    private val binding by viewBinding(FragmentGameBinding::bind)

    private val viewModel by activityViewModels<GameViewModel>()
    private val adapter by lazy {
        GameAdapter(viewModel.difficulty.value) { gamePiece ->
            binding.input.text.append(gamePiece.value.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val answerLength = viewModel.difficulty.value.answerLength

        limitTextFieldLengthToAnswerLength(answerLength)
        setupGameGrid(answerLength)
        setupListeners(answerLength)
        setupCollectors()

        viewModel.startGame()
    }

    private fun limitTextFieldLengthToAnswerLength(answerLength: Int) {
        binding.input.filters += InputFilter.LengthFilter(answerLength)
    }

    private fun setupGameGrid(answerLength: Int) {
        binding.gameGrid.adapter = adapter
        binding.gameGrid.layoutManager = GridLayoutManager(requireContext(), answerLength)
    }

    private fun setupListeners(answerLength: Int) {
        binding.submit.setOnClickListener {
            val text = binding.input.text.toString()
            binding.input.text.clear()
            viewModel.determineGuessedPieces(text)
        }

        binding.endGame.setOnClickListener {
            navigate(R.id.navigation_menu)
        }

        binding.input.doAfterTextChanged {
            binding.submit.isEnabled = binding.input.text.length == answerLength
        }
    }

    private fun setupCollectors() {
        launchUI {
            viewModel.gamePieces.collectLatest { pieces ->
                adapter.submitList(pieces)
                delay(100)
                binding.gameGrid.smoothScrollToPosition(adapter.itemCount)
            }
        }

        launchUI {
            viewModel.gameTimer.collectLatest { time ->
                binding.gameTimer.text = resources.getString(R.string.score_time, time.toTimeString())
            }
        }

        launchUI {
            viewModel.attemptCount.collectLatest { count ->
                binding.attemptCount.text = resources.getString(R.string.score_attempts, count)
            }
        }

        launchUI {
            viewModel.onGameOver.collect { time ->
                AlertDialog.Builder(requireContext(), R.style.AlertDialog)
                    .setMessage(getString(R.string.game_over_template, time.toTimeString()))
                    .setPositiveButton("OK") { popup, _ ->
                        popup.dismiss()
                        navigate(R.id.navigation_menu)
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}
