package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.reading

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.databinding.ItemSupplementalLinkBinding
import dev.esnault.bunpyro.domain.entities.grammar.SupplementalLink


class SupplementalLinkAdapter(
    context: Context,
    private val listener: ReadingViewHolder.Listener
) : RecyclerView.Adapter<SupplementalLinkAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var supplementalLinks: List<SupplementalLink> = mutableListOf()
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSupplementalLinkBinding.inflate(inflater, parent, false)
        return ViewHolder(
            binding,
            listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(supplementalLinks[position], position == supplementalLinks.lastIndex)
    }

    override fun getItemCount(): Int = supplementalLinks.size

    class ViewHolder(
        private val binding: ItemSupplementalLinkBinding,
        private val listener: ReadingViewHolder.Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private var supplementalLink: SupplementalLink? = null

        init {
            binding.root.setOnClickListener {
                supplementalLink?.let(listener.onClick)
            }
        }

        fun bind(supplementalLink: SupplementalLink, isLast: Boolean) {
            this.supplementalLink = supplementalLink

            binding.description.text = supplementalLink.description
            binding.site.text = supplementalLink.site

            binding.bottomDivider.isVisible = !isLast
        }
    }
}
