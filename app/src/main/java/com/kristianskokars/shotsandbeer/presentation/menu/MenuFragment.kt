package com.kristianskokars.shotsandbeer.presentation.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.common.launchUI
import com.kristianskokars.shotsandbeer.common.navigate
import com.kristianskokars.shotsandbeer.common.viewBinding
import com.kristianskokars.shotsandbeer.databinding.FragmentMenuBinding
import com.kristianskokars.shotsandbeer.data.model.Difficulty
import com.kristianskokars.shotsandbeer.presentation.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.fragment_menu) {
    private val binding by viewBinding(FragmentMenuBinding::bind)
    private val viewModel by activityViewModels<GameViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupCollectors()
    }

    private fun setupListeners() {
        binding.startNewGame.setOnClickListener {
            navigate(R.id.navigation_game)
        }
        binding.showHighScores.setOnClickListener {
            navigate(R.id.navigation_high_scores)
        }
        binding.difficultyEasy.setOnClickListener {
            viewModel.setDifficulty(Difficulty.EASY)
        }
        binding.difficultyNormal.setOnClickListener {
            viewModel.setDifficulty(Difficulty.NORMAL)
        }
        binding.difficultyHard.setOnClickListener {
            viewModel.setDifficulty(Difficulty.HARD)
        }
    }

    private fun setupCollectors() {
        launchUI {
            viewModel.difficulty.collect { difficulty ->
                binding.difficulty.text = getString(R.string.difficulty, difficulty.toString())
            }
        }
    }
}