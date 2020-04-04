package dev.esnault.bunpyro.common

import android.content.SharedPreferences


// region putOrRemove

fun SharedPreferences.Editor.putOrRemoveInt(key: String, value: Int?) {
    if (value == null) {
        remove(key)
    } else {
        putInt(key, value)
    }
}

fun SharedPreferences.Editor.putOrRemoveString(key: String, value: String?) {
    if (value == null) {
        remove(key)
    } else {
        putString(key, value)
    }
}

// endregion

// region getOrNull

fun SharedPreferences.getIntOrNull(key: String): Int? {
    return getInt(key, Int.MIN_VALUE).takeIf { it != Int.MIN_VALUE }
}

// endregion
