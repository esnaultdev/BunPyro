package dev.esnault.bunpyro.common

import android.os.Bundle


fun Bundle.getIntOrNull(key: String): Int? {
    return get(key) as? Int
}

fun Bundle.getLongOrNull(key: String): Long? {
    return get(key) as? Long
}
