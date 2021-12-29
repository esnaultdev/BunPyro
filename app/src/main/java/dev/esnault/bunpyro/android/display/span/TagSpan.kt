package dev.esnault.bunpyro.android.display.span


/**
 * A span used to tag a portion of text.
 */
class TagSpan(var tag: Any) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagSpan
        if (tag != other.tag) return false
        return true
    }

    override fun hashCode(): Int {
        return tag.hashCode()
    }
}
