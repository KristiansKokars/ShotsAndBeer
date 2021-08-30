package com.kristianskokars.shotsandbeer.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.common.launchMain
import com.kristianskokars.shotsandbeer.common.openFragment
import com.kristianskokars.shotsandbeer.databinding.FragmentGameBinding
import com.kristianskokars.shotsandbeer.ui.GameViewModel
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    private val viewModel by activityViewModels<GameViewModel>()
    private val adapter by lazy { GameAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameGrid.adapter = adapter
        binding.gameGrid.layoutManager = GridLayoutManager(requireContext(), 4)

        viewModel.startGame()

        binding.submit.setOnClickListener {
            val text = binding.input.text.toString()
            binding.emptyHighScore.text = getString(R.string.last_input, text)
            viewModel.calculateResults(text)
        }

        binding.endGame.setOnClickListener {
            openFragment(R.id.navigation_menu)
        }

        // Here we are listening for updates on the objects
        launchMain {
            viewModel.gamePieces.collect { pieces ->
                adapter.gamePieces = pieces
            }
        }

        launchMain {
            viewModel.gameTimer.collect { time ->
                binding.gameTimer.text = time
            }
        }

        launchMain {
            viewModel.attempts.collect { attempt ->
                binding.nextGamePiece.text = getString(R.string.attempts_made, attempt.toString())
            }
        }

        launchMain {
            viewModel.onGameOver.collect { time ->
                if (time == null) return@collect
                AlertDialog.Builder(requireContext()) // TODO: NumberTapper can pass in context, Android Studio here requires this though...
                    .setMessage(getString(R.string.game_over_template, time))
                    .setPositiveButton("Ok") { popup, _ ->
                        popup.dismiss()
                        openFragment(R.id.navigation_menu)
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        launchMain {
//            viewModel.nextButton.collect { nextButton ->
//                binding.nextGamePiece.text = getString(R.string.next_button_template, nextButton)
//            }
        }
    }
}
