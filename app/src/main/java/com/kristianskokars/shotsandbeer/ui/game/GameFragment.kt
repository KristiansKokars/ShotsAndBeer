package com.kristianskokars.shotsandbeer.ui.game

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.common.launchMain
import com.kristianskokars.shotsandbeer.common.openFragment
import com.kristianskokars.shotsandbeer.databinding.FragmentGameBinding
import com.kristianskokars.shotsandbeer.repository.models.Difficulty
import com.kristianskokars.shotsandbeer.ui.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    private val viewModel by activityViewModels<GameViewModel>()
    private val adapter by lazy {
        GameAdapter(viewModel.difficulty.value) { gamePiece ->
            binding.input.text.append(gamePiece.valueString)
        }
    }

    private var answerLength: Int = Difficulty.EASY.answerLength

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adjusts the number cap
        answerLength = viewModel.difficulty.value.answerLength
        binding.input.filters += InputFilter.LengthFilter(answerLength)

        setupGameGrid()
        setupListeners()
        setupCollectors()

        viewModel.startGame()
    }

    private fun setupGameGrid() {
        binding.gameGrid.adapter = adapter
        binding.gameGrid.layoutManager = GridLayoutManager(requireContext(), answerLength)
    }

    private fun setupListeners() {
        binding.submit.setOnClickListener {
            val text = binding.input.text.toString()
            binding.input.text.clear()
            viewModel.calculateResults(text)
        }

        binding.endGame.setOnClickListener {
            openFragment(R.id.navigation_menu)
        }

        binding.input.doAfterTextChanged {
            binding.submit.isEnabled = binding.input.text.length == answerLength
        }
    }

    private fun setupCollectors() {
        launchMain {
            viewModel.gamePieces.collect { pieces ->
                adapter.gamePieces = pieces
                delay(100)
                binding.gameGrid.smoothScrollToPosition(adapter.itemCount)
            }
        }

        launchMain {
            viewModel.gameTimer.collect { time ->
                binding.gameTimer.text = resources.getString(R.string.score_time, time)
            }
        }

        launchMain {
            viewModel.attemptCount.collect { count ->
                binding.attemptCount.text = resources.getString(R.string.score_attempts, count)
            }
        }

        launchMain {
            viewModel.onGameOver.collect { time ->
                if (time == null) return@collect
                AlertDialog.Builder(requireContext(), R.style.AlertDialog)
                    .setMessage(getString(R.string.game_over_template, time))
                    .setPositiveButton("OK") { popup, _ ->
                        popup.dismiss()
                        openFragment(R.id.navigation_menu)
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}
