package dev.esnault.bunpyro.android.screen.home

import dev.esnault.bunpyro.android.screen.base.BaseViewModel


class HomeViewModel() : BaseViewModel() {
    // TODO: Implement the ViewModel

    fun onLessonsTap() {
        navigate(HomeFragmentDirections.actionHomeToLessons())
    }

    fun onAllGrammarTap() {
        navigate(HomeFragmentDirections.actionHomeToAllGrammar())
    }
}
