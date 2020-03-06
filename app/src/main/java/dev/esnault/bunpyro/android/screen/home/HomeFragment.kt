package dev.esnault.bunpyro.android.screen.home


import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.utils.RubySpan
import dev.esnault.bunpyro.databinding.FragmentHomeBinding
import org.koin.android.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val vm: HomeViewModel by viewModel()
    override val bindingClass = FragmentHomeBinding::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lessonsButton.setOnClickListener {
            vm.onLessonsTap()
        }

        setupTextViews()
    }

    private fun setupTextViews() {
        binding.furigana.text = SpannableStringBuilder("車日本\n図書館\n富士山").apply {
            setSpan(RubySpan("くるま"), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RubySpan("にっぽん"), 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RubySpan("としょかん"), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RubySpan("ふじさん"), 8, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
