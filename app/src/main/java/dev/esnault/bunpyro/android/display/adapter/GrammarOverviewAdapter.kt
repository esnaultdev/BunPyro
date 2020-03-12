package dev.esnault.bunpyro.android.display.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.databinding.ItemGrammarPointOverviewBinding
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


class GrammarOverviewAdapter(
    context: Context,
    private val listener: GrammarOverviewViewHolder.Listener
) : RecyclerView.Adapter<GrammarOverviewViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var grammarPoints: List<GrammarPointOverview> = mutableListOf()
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrammarOverviewViewHolder {
        val binding = ItemGrammarPointOverviewBinding.inflate(inflater, parent, false)
        return GrammarOverviewViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: GrammarOverviewViewHolder, position: Int) {
        holder.bind(grammarPoints[position], position == grammarPoints.lastIndex)
    }

    override fun getItemCount(): Int = grammarPoints.size
}
