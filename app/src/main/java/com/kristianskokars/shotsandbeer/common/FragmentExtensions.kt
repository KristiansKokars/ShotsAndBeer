package com.kristianskokars.shotsandbeer.common

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.kristianskokars.shotsandbeer.R

fun Fragment.openFragment(id: Int) = activity?.findNavController(R.id.navigation_host_fragment)?.run {
    // Try to go back with destroying the existing fragment to stop flow collection
    if (!popBackStack(id, false)) {
        // If navigating back fails since a fragment not found in back stack - open the fragment instead
        navigate(id)
    }
}