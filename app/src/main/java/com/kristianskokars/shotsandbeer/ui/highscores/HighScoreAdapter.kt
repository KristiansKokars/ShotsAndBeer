package com.kristianskokars.shotsandbeer.ui.highscores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.databinding.ItemHighScoreBinding
import com.kristianskokars.shotsandbeer.repository.models.HighScore
import kotlin.properties.Delegates

class HighScoreAdapter : RecyclerView.Adapter<HighScoreAdapter.ViewHolder>() {

    var highScores: List<HighScore> by Delegates.observable(emptyList()) { _, old, new ->
        DiffUtil.calculateDiff(DifferenceUtil(old, new)).dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHighScoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = highScores[position]
        val resources = holder.itemView.resources
        holder.binding.attemptCount.text = resources.getString(R.string.score_attempts, item.attempts.toInt())
        holder.binding.date.text = item.date
        holder.binding.time.text = resources.getString(R.string.score_time, item.time)
    }

    override fun getItemCount() = highScores.size

    inner class ViewHolder(val binding: ItemHighScoreBinding) : RecyclerView.ViewHolder(binding.root)

    inner class DifferenceUtil(private val old: List<HighScore>, private val new: List<HighScore>) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition].id == new[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition] == new[newItemPosition]
    }
}
