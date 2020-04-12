package dev.esnault.bunpyro.android.display.viewholder

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.common.getColorCompat
import dev.esnault.bunpyro.databinding.ItemGrammarPointOverviewBinding
import dev.esnault.bunpyro.domain.DomainConfig
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.settings.HankoDisplaySetting


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

    fun bind(
        grammarPoint: GrammarPointOverview,
        isLast: Boolean,
        hankoDisplay: HankoDisplaySetting
    ) {
        this.grammarPoint = grammarPoint

        binding.japanese.text = grammarPoint.title
        binding.english.text = grammarPoint.processedMeaning

        binding.bottomDivider.isVisible = !isLast

        bindStudy(context, binding, grammarPoint.srsLevel, hankoDisplay)

        // Incomplete
        binding.background.isEnabled = !grammarPoint.incomplete
        binding.japanese.isEnabled = !grammarPoint.incomplete
    }
}

fun bindStudy(
    context: Context,
    binding: ItemGrammarPointOverviewBinding,
    srsLevel: Int?,
    hankoDisplay: HankoDisplaySetting
) {
    binding.studyHanko.isVisible = srsLevel != null

    if (srsLevel != null) {
        when (hankoDisplay) {
            HankoDisplaySetting.NORMAL -> bindHankoNormal(context, binding, srsLevel)
            HankoDisplaySetting.LEVEL -> bindHankoLevel(context, binding, srsLevel)
        }
    }
}

fun bindHankoNormal(context: Context, binding: ItemGrammarPointOverviewBinding, srsLevel: Int) {
    binding.studyHankoIcon.setImageResource(R.drawable.ic_bunpyro_hanko)
    val isBurned = srsLevel == DomainConfig.STUDY_BURNED
    binding.studyHankoIcon.colorFilter = if (isBurned) {
        val goldColor = context.getColorCompat(R.color.hanko_gold)
        PorterDuffColorFilter(goldColor, PorterDuff.Mode.SRC_IN)
    } else {
        null
    }
    binding.studyHankoIcon.rotation = -30f
}

fun bindHankoLevel(context: Context, binding: ItemGrammarPointOverviewBinding, srsLevel: Int) {
    val isBurned = srsLevel == DomainConfig.STUDY_BURNED
    binding.studyHankoLevel.isVisible = !isBurned
    binding.studyHankoLevel.text = srsLevel.toString()

    val (iconResId, colorFilter) = if (isBurned) {
        val goldColor = context.getColorCompat(R.color.hanko_gold)
        R.drawable.ic_bunpyro_hanko to PorterDuffColorFilter(goldColor, PorterDuff.Mode.SRC_IN)
    } else {
        R.drawable.ic_bunpyro_hanko_empty to null
    }
    binding.studyHankoIcon.setImageResource(iconResId)
    binding.studyHankoIcon.colorFilter = colorFilter
    binding.studyHankoIcon.rotation = 0f
}
