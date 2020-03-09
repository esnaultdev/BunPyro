package dev.esnault.bunpyro.android.utils

import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet


/**
 * [AutoTransition] but with named references to its transitions.
 */
class NamedAutoTransition : TransitionSet() {

    val fadeIn = Fade(Fade.IN)
    val fadeOut = Fade(Fade.OUT)
    val changeBounds = ChangeBounds()

    init {
        ordering = ORDERING_SEQUENTIAL
        addTransition(fadeOut)
            .addTransition(changeBounds)
            .addTransition(fadeIn)
    }
}
