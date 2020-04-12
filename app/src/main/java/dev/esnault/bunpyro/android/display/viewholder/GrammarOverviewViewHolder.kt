package dev.esnault.bunpyro.android.display.viewholder

import android.content.Context
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.databinding.ItemGrammarPointOverviewBinding
import dev.esnault.bunpyro.domain.DomainConfig
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


class GrammarOverviewViewHolder(
    private val binding: ItemGrammarPointOverviewBinding,
    private val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

    class Listener(
        val onGrammarClicked: (point: GrammarPointOverview) -> Unit
    )

    private val context: Context
        get() = itemView.context

    private var grammarPoint: GrammarPointOverview? = null

    init {
        binding.root.setOnClickListener {
            grammarPoint?.let(listener.onGrammarClicked)
        }

        binding.jlptTag.isVisible = false
    }

    fun bind(grammarPoint: GrammarPointOverview, isLast: Boolean) {
        this.grammarPoint = grammarPoint

        binding.japanese.text = grammarPoint.title
        binding.english.text = grammarPoint.processedMeaning

        binding.bottomDivider.isVisible = !isLast

        // Study
        val srsLevel = grammarPoint.srsLevel
        val studied = srsLevel != null
        binding.studyHanko.isVisible = studied
        if (studied) {
            val isBurned = srsLevel == DomainConfig.STUDY_BURNED
            binding.studyHankoLevel.isVisible = !isBurned
            binding.studyHankoLevel.text = srsLevel?.toString()

            val iconResId = if (isBurned) {
                R.drawable.ic_bunpyro_hanko
            } else {
                R.drawable.ic_bunpyro_hanko_empty
            }
            binding.studyHankoIcon.setImageResource(iconResId)
        }

        // Incomplete
        binding.background.isEnabled = !grammarPoint.incomplete
        binding.japanese.isEnabled = !grammarPoint.incomplete
    }
}
