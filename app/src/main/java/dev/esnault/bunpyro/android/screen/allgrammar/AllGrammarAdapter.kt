package dev.esnault.bunpyro.android.screen.allgrammar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.android.res.textResId
import dev.esnault.bunpyro.databinding.ItemAllGrammarJlptHeaderBinding
import dev.esnault.bunpyro.databinding.ItemGrammarPointOverviewBinding
import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.JlptGrammar
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.settings.HankoDisplaySetting


class AllGrammarAdapter(
    context: Context,
    private val listener: GrammarOverviewViewHolder.Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private var viewModel = ViewModel(emptyList(), HankoDisplaySetting.DEFAULT)
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                notifyDataSetChanged()
            }
        }

    fun set(jlptGrammars: List<JlptGrammar>, hankoDisplay: HankoDisplaySetting) {
        val items = jlptGrammars.flatMap { jlptGrammar ->
            val result = mutableListOf<ViewModel.Item>()
            result.add(ViewModel.Item.JlptHeader(jlptGrammar.level))
            jlptGrammar.grammar.mapTo(result) { ViewModel.Item.Grammar(it) }
            result
        }

        viewModel = ViewModel(items, hankoDisplay)
    }

    override fun getItemCount(): Int = viewModel.items.size

    override fun getItemViewType(position: Int): Int {
        return when (viewModel.items[position]) {
            is ViewModel.Item.JlptHeader -> ViewType.JLPT_HEADER
            is ViewModel.Item.Grammar -> ViewType.GRAMMAR_POINT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.JLPT_HEADER -> {
                val binding = ItemAllGrammarJlptHeaderBinding.inflate(inflater, parent, false)
                JlptHeaderViewHolder(binding)
            }
            ViewType.GRAMMAR_POINT -> {
                val binding = ItemGrammarPointOverviewBinding.inflate(inflater, parent, false)
                GrammarOverviewViewHolder(binding, listener)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = viewModel.items[position]) {
            is ViewModel.Item.JlptHeader -> {
                (holder as? JlptHeaderViewHolder)?.bind(item.level)
            }
            is ViewModel.Item.Grammar -> {
                val nextItem = viewModel.items.getOrNull(position + 1)
                val isLast = nextItem !is ViewModel.Item.Grammar
                val hankoDisplay = viewModel.hankoDisplay
                (holder as? GrammarOverviewViewHolder)?.bind(item.point, isLast, hankoDisplay)
            }
        }
    }

    data class ViewModel(
        val items: List<Item>,
        val hankoDisplay: HankoDisplaySetting
    ) {
        sealed class Item {
            data class JlptHeader(val level: JLPT) : Item()
            data class Grammar(val point: GrammarPointOverview) : Item()
        }
    }

    class JlptHeaderViewHolder(
        private val binding: ItemAllGrammarJlptHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(level: JLPT) {
            binding.level.setText(level.textResId)
        }
    }
}

private object ViewType {
    const val JLPT_HEADER = 0
    const val GRAMMAR_POINT = 1
}
