package dev.esnault.bunpyro.common

import android.content.SharedPreferences
import java.util.*


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

fun SharedPreferences.getLongOrNull(key: String): Long? {
    return getLong(key, Long.MIN_VALUE).takeIf { it != Long.MIN_VALUE }
}

// endregion

// region Date

fun SharedPreferences.Editor.putOrRemoveDate(key: String, value: Date?) {
    if (value == null) {
        remove(key)
    } else {
        putLong(key, value.time)
    }
}

fun SharedPreferences.getDate(key: String): Date? {
    val time = getLongOrNull(key)
    return time?.let(::Date)
}

// endregion
