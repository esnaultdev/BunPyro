package dev.esnault.bunpyro.data.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.ParametersBuilder
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


object Analytics {

    private val firebaseAnalytics = Firebase.analytics

    fun screen(
        name: String,
        block: (ParametersBuilder.() -> Unit) = {}
    ) {
        val builder = ParametersBuilder()
        builder.block()
        builder.param(FirebaseAnalytics.Param.SCREEN_NAME, name)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, builder.bundle)
    }

    object Param {
        const val ITEM_ID = FirebaseAnalytics.Param.ITEM_ID
    }
}
