package com.kristianskokars.shotsandbeer.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.common.launchMain
import com.kristianskokars.shotsandbeer.common.openFragment
import com.kristianskokars.shotsandbeer.databinding.FragmentMenuBinding
import com.kristianskokars.shotsandbeer.repository.models.Difficulty
import com.kristianskokars.shotsandbeer.ui.GameViewModel
import kotlinx.coroutines.flow.collect

class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding

    private val viewModel by activityViewModels<GameViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMenuBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startNewGame.setOnClickListener {
            openFragment(R.id.navigation_game)
        }
        binding.showHighScores.setOnClickListener {
            openFragment(R.id.navigation_high_scores)
        }

        binding.difficultyEasy.setOnClickListener {
            viewModel.difficulty.tryEmit(Difficulty.EASY)
        }

        binding.difficultyNormal.setOnClickListener {
            viewModel.difficulty.tryEmit(Difficulty.NORMAL)
        }

        binding.difficultyHard.setOnClickListener {
            viewModel.difficulty.tryEmit(Difficulty.HARD)
        }

        launchMain {
            viewModel.difficulty.collect { difficulty ->
                binding.difficulty.text = getString(R.string.difficulty, difficulty.toString())
            }
        }

    }

}