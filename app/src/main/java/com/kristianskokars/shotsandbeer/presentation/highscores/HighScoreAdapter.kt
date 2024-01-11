package com.kristianskokars.shotsandbeer.presentation.highscores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.common.toTimeString
import com.kristianskokars.shotsandbeer.databinding.ItemHighScoreBinding
import com.kristianskokars.shotsandbeer.data.model.HighScore

class HighScoreAdapter : ListAdapter<HighScore, HighScoreAdapter.ViewHolder>(HighScoreDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHighScoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        val resources = holder.itemView.resources

        holder.binding.date.text = item.date
        holder.binding.attemptCount.text = resources.getString(R.string.score_attempts, item.attempts)
        holder.binding.time.text = resources.getString(R.string.score_time, item.timeInMillis.toTimeString())
    }

    inner class ViewHolder(val binding: ItemHighScoreBinding) : RecyclerView.ViewHolder(binding.root)

    class HighScoreDiffUtil : DiffUtil.ItemCallback<HighScore>() {
        override fun areItemsTheSame(oldItem: HighScore, newItem: HighScore) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: HighScore, newItem: HighScore) = oldItem == newItem

    }
}
