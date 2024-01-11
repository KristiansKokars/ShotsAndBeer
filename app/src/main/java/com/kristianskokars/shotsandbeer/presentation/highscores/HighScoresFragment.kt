package com.kristianskokars.shotsandbeer.presentation.highscores

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.common.launchUI
import com.kristianskokars.shotsandbeer.common.navigate
import com.kristianskokars.shotsandbeer.common.viewBinding
import com.kristianskokars.shotsandbeer.databinding.FragmentHighscoresBinding
import com.kristianskokars.shotsandbeer.presentation.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HighScoresFragment : Fragment(R.layout.fragment_highscores) {
    private val binding by viewBinding(FragmentHighscoresBinding::bind)
    private val viewModel by activityViewModels<GameViewModel>()
    private val adapter by lazy { HighScoreAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.highScoreList.adapter = adapter
        setupListeners()
        setupCollectors()
    }

    private fun setupListeners() {
        binding.closeHighScores.setOnClickListener {
            navigate(R.id.navigation_menu)
        }
    }

    private fun setupCollectors() {
        launchUI {
            viewModel.highScores.collectLatest { highScores ->
                if (highScores == null) {
                    binding.loadingIndicator.visibility = View.VISIBLE
                    return@collectLatest
                }

                binding.loadingIndicator.visibility = View.GONE
                binding.emptyHighScore.visibility = if (highScores.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                adapter.submitList(highScores)
            }
        }
    }
}
