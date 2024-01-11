package com.kristianskokars.shotsandbeer.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

fun Fragment.launchUI(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(
    context = CoroutineExceptionHandler { _, e ->
        Timber.d(e, "Coroutine failed: ${e.localizedMessage}")
    },
) {
    repeatOnLifecycle(state) {
        block()
    }
}

fun ViewModel.launch(
    block: suspend CoroutineScope.() -> Unit
) = viewModelScope.launch(
    context = CoroutineExceptionHandler { _, e ->
        Timber.d(e, "Coroutine failed: ${e.localizedMessage}")
    },
    block = block
)

fun <T> Flow<T>.asStateFlow(
    scope: CoroutineScope,
    startingValue: T,
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(500)
) = stateIn(scope, sharingStarted, startingValue)