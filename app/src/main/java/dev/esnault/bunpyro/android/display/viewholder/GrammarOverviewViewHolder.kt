package dev.esnault.bunpyro.android.display.viewholder

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.srsStringOrNull
import dev.esnault.bunpyro.common.getColorCompat
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.show
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
    when (hankoDisplay) {
        HankoDisplaySetting.NORMAL -> bindStudyNormal(context, binding, srsLevel)
        HankoDisplaySetting.LEVEL -> bindStudyLevel(context, binding, srsLevel)
    }
}

fun bindStudyNormal(context: Context, binding: ItemGrammarPointOverviewBinding, srsLevel: Int?) {
    binding.srsTag.hide()

    if (srsLevel == null) {
        binding.studyHanko.hide()
        return
    } else {
        binding.studyHanko.show()
    }

    val isBurned = srsLevel == DomainConfig.STUDY_BURNED
    binding.studyHanko.colorFilter = if (isBurned) {
        val goldColor = context.getColorCompat(R.color.hanko_gold)
        PorterDuffColorFilter(goldColor, PorterDuff.Mode.SRC_IN)
    } else {
        null
    }
}

fun bindStudyLevel(context: Context, binding: ItemGrammarPointOverviewBinding, srsLevel: Int?) {
    binding.studyHanko.hide()

    val srsString = srsStringOrNull(context, srsLevel)
    if (srsString == null) {
        binding.srsTag.hide()
        return
    }

    binding.srsTag.show()
    binding.srsTag.text = srsString
    val isBurned = srsLevel == DomainConfig.STUDY_BURNED
    binding.srsTag.backgroundTintList = if (isBurned) {
        val goldColor = context.getColorCompat(R.color.hanko_gold)
        ColorStateList.valueOf(goldColor)
    } else {
        val normalColor = context.getColorCompat(R.color.hanko)
        ColorStateList.valueOf(normalColor)
    }
}
