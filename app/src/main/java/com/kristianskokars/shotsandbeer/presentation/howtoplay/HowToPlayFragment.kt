package com.kristianskokars.shotsandbeer.presentation.howtoplay

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.kristianskokars.shotsandbeer.R
import com.kristianskokars.shotsandbeer.common.navigateUp
import com.kristianskokars.shotsandbeer.common.viewBinding
import com.kristianskokars.shotsandbeer.databinding.FragmentHowToPlayBinding

class HowToPlayFragment : Fragment(R.layout.fragment_how_to_play) {
    private val binding by viewBinding(FragmentHowToPlayBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.goBack.setOnClickListener { navigateUp() }
    }
}