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
import com.kristianskokars.shotsandbeer.common.GAME_LIST_SIZE
import com.kristianskokars.shotsandbeer.common.launchMain
import com.kristianskokars.shotsandbeer.common.openFragment
import com.kristianskokars.shotsandbeer.databinding.FragmentGameBinding
import com.kristianskokars.shotsandbeer.ui.GameViewModel
import kotlinx.coroutines.flow.collect

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    private val viewModel by activityViewModels<GameViewModel>()
    private val adapter by lazy {
        GameAdapter { gamePiece ->
            binding.input.text.append(gamePiece.valueString)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameGrid.adapter = adapter
        binding.gameGrid.layoutManager = GridLayoutManager(requireContext(), GAME_LIST_SIZE)

        // Adjusts the number cap in the input field
        binding.input.filters += InputFilter.LengthFilter(GAME_LIST_SIZE)

        viewModel.startGame()

        binding.submit.setOnClickListener {
            val text = binding.input.text.toString()
            binding.input.text.clear()

            viewModel.calculateResults(text) { index, itemCount ->
                adapter.notifyItemRangeInserted(index, itemCount)
                // Originally tried smoothScrollToPosition, this felt better though
                binding.gameGrid.scrollToPosition(adapter.gamePieces.size)
            }

        }

        binding.endGame.setOnClickListener {
            openFragment(R.id.navigation_menu)
        }

        binding.input.doAfterTextChanged {
            binding.submit.isEnabled = binding.input.text.length == GAME_LIST_SIZE
        }

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
            viewModel.attemptCount.collect { count ->
                binding.nextGamePiece.text = getString(R.string.attempts_made, count.toString())
            }
        }

        launchMain {
            viewModel.onGameOver.collect { time ->
                if (time == null) return@collect
                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.game_over_template, time))
                    .setPositiveButton("Ok") { popup, _ ->
                        popup.dismiss()
                        openFragment(R.id.navigation_menu)
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}
