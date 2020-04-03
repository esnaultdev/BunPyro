package dev.esnault.bunpyro.android.screen.allgrammar

import android.content.Context
import android.view.LayoutInflater
import android.widget.CheckBox
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.databinding.DialogAllGrammarFilterBinding
import dev.esnault.bunpyro.databinding.DialogItemMultichoiceBinding
import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter


fun buildFilterDialog(
    context: Context,
    filter: AllGrammarFilter,
    onPositive: (AllGrammarFilter) -> Unit
): MaterialDialog {
    val inflater = LayoutInflater.from(context)
    var result = filter

    val binding = DialogAllGrammarFilterBinding.inflate(inflater)

    // Init JLPT items
    fun DialogItemMultichoiceBinding.initJlptItem(resId: Int, jlpt: JLPT) {
        initItem(resId, result.jlpt.contains(jlpt)) { checkBox ->
            result = result.toggle(JLPT.N5)
            checkBox.isChecked = result.jlpt.contains(JLPT.N5)
        }
    }
    binding.jlptN5.initJlptItem(R.string.jlpt_n5, JLPT.N5)
    binding.jlptN4.initJlptItem(R.string.jlpt_n4, JLPT.N4)
    binding.jlptN3.initJlptItem(R.string.jlpt_n3, JLPT.N3)
    binding.jlptN2.initJlptItem(R.string.jlpt_n2, JLPT.N2)
    binding.jlptN1.initJlptItem(R.string.jlpt_n1, JLPT.N1)

    // Init Study items
    binding.studyStudied.initItem(
        resId = R.string.allGrammar_filterDialog_studied,
        checked = result.studied
    ) { checkBox ->
        result = result.copy(studied = !result.studied)
        checkBox.isChecked = result.studied
    }
    binding.studyNotStudied.initItem(
        resId = R.string.allGrammar_filterDialog_notStudied,
        checked = result.nonStudied
    ) { checkBox ->
        result = result.copy(nonStudied = !result.nonStudied)
        checkBox.isChecked = result.nonStudied
    }

    return MaterialDialog(context)
        .customView(
            view = binding.root,
            scrollable = true,
            noVerticalPadding = true,
            horizontalPadding = false
        )
        .positiveButton(R.string.common_ok) {
            onPositive(result)
        }
}

private fun DialogItemMultichoiceBinding.initItem(
    resId: Int,
    checked: Boolean,
    onClick: (checkBox: CheckBox) -> Unit
) {
    title.setText(resId)
    control.isChecked = checked
    root.setOnClickListener { onClick(control) }
}
