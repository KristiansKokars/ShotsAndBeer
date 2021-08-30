package com.kristianskokars.shotsandbeer.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.common.openFragment
import com.kristianskokars.shotsandbeer.databinding.FragmentMenuBinding
import timber.log.Timber

class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding

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
    }

}