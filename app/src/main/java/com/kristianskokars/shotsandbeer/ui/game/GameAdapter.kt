package com.kristianskokars.shotsandbeer.ui.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kristianskokars.shotsandbeer.databinding.ItemGamePieceBinding
import com.kristianskokars.shotsandbeer.repository.models.GamePiece
import kotlin.properties.Delegates

class GameAdapter(
    private val onGamePieceClicked: (GamePiece) -> Unit
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
