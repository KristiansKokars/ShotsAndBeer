package com.kristianskokars.shotsandbeer.ui.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kristianskokars.shotsandbeer.databinding.ItemGamePieceBinding
import com.kristianskokars.shotsandbeer.repository.models.Difficulty
import com.kristianskokars.shotsandbeer.repository.models.GamePiece
import kotlin.math.ceil
import kotlin.properties.Delegates

class GameAdapter(
    private val difficulty: Difficulty,
    private val onGamePieceClicked: (GamePiece) -> Unit,
) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    var gamePieces: List<GamePiece> by Delegates.observable(emptyList(), { _, old, new ->
        DiffUtil.calculateDiff(DifferenceUtil(old, new)).dispatchUpdatesTo(this)
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemGamePieceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = gamePieces[position]
        holder.binding.gamePiece.tag = item
        holder.binding.item = item

        // TODO: I do not like this, I do not approve of this, but I accept this - for now, as I have
        //  no better idea since data binding was messy as well... not the right place to do this, but
        //  this is going to be a refactoring for later
        // nothing for easy as that is default values already in XML view

        val densityFactor = ceil(holder.binding.gamePiece.context.resources.displayMetrics.density).toInt()
        val fontScaleFactor = holder.binding.gamePiece.context.resources.configuration.fontScale
        when (difficulty) {
            Difficulty.NORMAL -> {
                holder.binding.gamePieceTextView.layoutParams.height = 60 * densityFactor
                holder.binding.gamePieceTextView.layoutParams.width = 60 * densityFactor
                holder.binding.gamePieceTextView.textSize = 30 * fontScaleFactor
            }
            Difficulty.HARD -> {
                holder.binding.gamePieceTextView.layoutParams.height = 50 * densityFactor
                holder.binding.gamePieceTextView.layoutParams.width = 50 * densityFactor
                holder.binding.gamePieceTextView.textSize = 22 * fontScaleFactor
            }
            else -> Unit
        }

        holder.binding.gamePiece.setOnClickListener {
            onGamePieceClicked(it.tag as GamePiece)
        }
    }

    override fun getItemCount() = gamePieces.size

    inner class ViewHolder(val binding: ItemGamePieceBinding) : RecyclerView.ViewHolder(binding.root)

    inner class DifferenceUtil(private val old: List<GamePiece>, private val new: List<GamePiece>) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition].id == new[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition] == new[newItemPosition]
    }
}
