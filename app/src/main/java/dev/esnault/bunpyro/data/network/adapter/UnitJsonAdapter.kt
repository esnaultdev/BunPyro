package dev.esnault.bunpyro.data.network.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException


/**
 * [JsonAdapter] for [Unit].
 */
class UnitJsonAdapter : JsonAdapter<Unit>() {

    @Synchronized
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader) {
        reader.skipValue()
        return
    }

    @Synchronized
    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: Unit?) {
        writer.nullValue()
    }
}
