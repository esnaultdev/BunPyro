package dev.esnault.bunpyro.android.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Adds the given observer to the observers list within the view lifespan of the fragment.
 * Shorthand for `observe(fragment.viewLifecycleOwner, observer)`.
 *
 * See [LiveData.observe].
 */
fun <T> LiveData<T>.safeObserve(fragment: Fragment, observer: Observer<in T>) {
    this.observe(fragment.viewLifecycleOwner, observer)
}
