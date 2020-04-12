package dev.esnault.bunpyro.android.display.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.databinding.ItemGrammarPointOverviewBinding
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.settings.HankoDisplaySetting


class GrammarOverviewAdapter(
    context: Context,
    private val listener: GrammarOverviewViewHolder.Listener
) : RecyclerView.Adapter<GrammarOverviewViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var viewModel: ViewModel = ViewModel(emptyList(), HankoDisplaySetting.DEFAULT)
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
        val grammarPoint = viewModel.grammarPoints[position]
        val isLast = position == viewModel.grammarPoints.lastIndex
        holder.bind(grammarPoint, isLast, viewModel.hankoDisplay)
    }

    override fun getItemCount(): Int = viewModel.grammarPoints.size

    data class ViewModel(
        val grammarPoints: List<GrammarPointOverview>,
        val hankoDisplay: HankoDisplaySetting
    )
}
