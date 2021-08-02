package dev.esnault.bunpyro.data.utils.extension

import com.squareup.moshi.JsonAdapter


fun <T> JsonAdapter<T>.fromJsonResult(string: String): Result<T?> =
    kotlin.runCatching { fromJson(string) }

fun <T> JsonAdapter<T>.toJsonResult(value: T?): Result<String> =
    kotlin.runCatching { toJson(value) }
