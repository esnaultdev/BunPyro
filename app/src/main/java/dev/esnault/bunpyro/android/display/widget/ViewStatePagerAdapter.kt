package dev.esnault.bunpyro.android.display.widget

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.StatefulAdapter
import androidx.viewpager2.widget.ViewPager2


/**
 * A [ViewPager2] adapter that retains the view state of each page.
 * The pages should always be at the same position.
 */
abstract class ViewStatePagerAdapter<VH : ViewStatePagerAdapter.ViewHolder>
    : RecyclerView.Adapter<VH>(), StatefulAdapter {

    // region Adapter state

    private val bundleKeyAdapterState = "AdapterState"
    private var restoredState = SparseArray<Parcelable>()

    override fun saveState(): Parcelable {
        holders.forEach { key, value ->
            restoredState[key] = value.itemView.saveInstanceState()
        }

        return Bundle().apply {
            putSparseParcelableArray(bundleKeyAdapterState, restoredState)
        }
    }

    override fun restoreState(savedState: Parcelable) {
        val bundle = (savedState as? Bundle) ?: return
        val container: SparseArray<Parcelable> =
            bundle.getSparseParcelableArray(bundleKeyAdapterState) ?: return

        restoredState = container
    }

    // endregion

    private val holders = SparseArray<VH>()

    /**
     * Called by RecyclerView to display the data at the specified position.
     * Adapters should implement [onBindPageViewHolder] instead.
     *
     * This indirection is needed because we have to execute the adapter's binding before
     * restoring our state, so that the view holder is populated with the right data.
     */
    final override fun onBindViewHolder(holder: VH, position: Int) {
        onBindPageViewHolder(holder, position)

        if (!holder.firstBindDone) {
            val state = restoredState[position]
            if (state != null) {
                holder.itemView.restoreInstanceState(state)
            }
            holder.firstBindDone = true
        }
        holders.put(position, holder)
    }

    /**
     * Indirection for [RecyclerView.Adapter.onBindViewHolder].
     */
    abstract fun onBindPageViewHolder(holder: VH, position: Int)

    @CallSuper
    override fun onViewRecycled(holder: VH) {
        val position = holder.adapterPosition
        restoredState.remove(position)
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var firstBindDone = false
    }
}


// region View Holder state

private const val bundleKeyViewHolderState = "ViewHolderState"

private fun View.saveInstanceState(): Parcelable {
    // We need to wrap each call to saveHierarchyState with a different SparseArray since
    // the view holder are likely to share the same view ids.
    val container = SparseArray<Parcelable>()
    saveHierarchyState(container)

    return Bundle().apply {
        putSparseParcelableArray(bundleKeyViewHolderState, container)
    }
}

private fun View.restoreInstanceState(parcelable: Parcelable) {
    // We need to unwrap the sparseArray used in saveInstanceState
    val bundle = parcelable as? Bundle ?: return
    val container: SparseArray<Parcelable> =
        bundle.getSparseParcelableArray(bundleKeyViewHolderState) ?: return

    restoreHierarchyState(container)
}

// endregion
