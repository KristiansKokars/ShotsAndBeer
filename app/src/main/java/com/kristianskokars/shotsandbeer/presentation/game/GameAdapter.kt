package com.kristianskokars.shotsandbeer.presentation.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.databinding.ItemGamePieceBinding
import com.kristianskokars.shotsandbeer.data.model.Difficulty
import com.kristianskokars.shotsandbeer.data.model.GamePiece
import kotlin.math.ceil

class GameAdapter(
    private val difficulty: Difficulty,
    private val onGamePieceClicked: (GamePiece) -> Unit,
) : ListAdapter<GamePiece, GameAdapter.ViewHolder>(GamePieceDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemGamePieceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        val context = holder.itemView.context
        with(holder.binding) {
            gamePieceTextView.text = item.value.toString()

            when (item.state) {
                GamePiece.State.NONE -> {
                    gamePiece.setCardBackgroundColor(ContextCompat.getColor(context, R.color.piece_idle))
                    gamePieceTextView.setTextColor(ContextCompat.getColor(context, R.color.piece_idle_text))
                }
                GamePiece.State.IS_GUESSED -> {
                    gamePiece.setCardBackgroundColor(ContextCompat.getColor(context, R.color.piece_guessed))
                    gamePieceTextView.setTextColor(ContextCompat.getColor(context, R.color.piece_guessed_text))
                }
                GamePiece.State.IS_FOUND -> {
                    gamePiece.setCardBackgroundColor(ContextCompat.getColor(context, R.color.piece_found))
                    gamePieceTextView.setTextColor(ContextCompat.getColor(context, R.color.piece_found_text))
                }
            }

            // Sets the size and width of the game pieces depending on the chosen difficulty
            val densityFactor = ceil(holder.binding.gamePiece.context.resources.displayMetrics.density).toInt()
            val fontScaleFactor = holder.binding.gamePiece.context.resources.configuration.fontScale

            when (difficulty) {
                Difficulty.NORMAL -> {
                    gamePieceTextView.layoutParams.height = 60 * densityFactor
                    gamePieceTextView.layoutParams.width = 60 * densityFactor
                    gamePieceTextView.textSize = 30 * fontScaleFactor
                }
                Difficulty.HARD -> {
                    gamePieceTextView.layoutParams.height = 50 * densityFactor
                    gamePieceTextView.layoutParams.width = 50 * densityFactor
                    gamePieceTextView.textSize = 22 * fontScaleFactor
                }
                else -> Unit
            }

            gamePiece.setOnClickListener {
                onGamePieceClicked(item)
            }
        }
    }

    inner class ViewHolder(val binding: ItemGamePieceBinding) : RecyclerView.ViewHolder(binding.root)

    class GamePieceDiffUtil : DiffUtil.ItemCallback<GamePiece>() {
        override fun areItemsTheSame(oldItem: GamePiece, newItem: GamePiece) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: GamePiece, newItem: GamePiece) =
            oldItem == newItem
    }
}
