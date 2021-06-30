package dev.esnault.bunpyro.data.network.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.adapters.EnumJsonAdapter
import java.io.IOException


/**
 * [JsonAdapter] for a [ValueEnum].
 * This is inspired by [EnumJsonAdapter] but uses a value rather than the enum name.
 */
class ValueEnumJsonAdapter<T : ValueEnum<V>, V>(
    val companion: ValueEnumCompanion<T>
) : JsonAdapter<T>() {

    @Synchronized
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): T? {
        val jsonValue: Any? = reader.readJsonValue()
        return companion.items.firstOrNull { it.value == jsonValue }
    }

    @Synchronized
    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: T?) {
        writer.jsonValue(value?.value)
    }
}

interface ValueEnum<V> {
    val value: V
}

interface ValueEnumCompanion<T : ValueEnum<*>> {
    val items: List<T>
}
