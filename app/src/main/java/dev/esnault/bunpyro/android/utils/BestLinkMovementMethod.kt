package dev.esnault.bunpyro.android.utils

import android.text.Spannable
import android.widget.TextView
import me.saket.bettermovementmethod.BetterLinkMovementMethod


/**
 * Handles URL clicks on TextViews. See [BetterLinkMovementMethod].
 *
 * Fixes the text view being initially scrolled by one line when the link
 * movement method is attached. I have no idea why this bug occurs.
 */
open class BestLinkMovementMethod : BetterLinkMovementMethod() {

    override fun initialize(widget: TextView?, text: Spannable?) {
        super.initialize(widget, text)
        if (widget == null) return

        val layout = widget.layout

        if (layout != null) {
            widget.scrollTo(widget.scrollX, layout.getLineTop(0))
        }
    }
}
