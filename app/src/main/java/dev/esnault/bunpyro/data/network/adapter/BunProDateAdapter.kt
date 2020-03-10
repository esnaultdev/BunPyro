package dev.esnault.bunpyro.data.network.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import dev.esnault.bunpyro.data.network.entities.BunProDate
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Formats [BunProDate], which is formatted like "2019-12-08 18:00:00 +0000".
 */
class BunProDateAdapter : JsonAdapter<BunProDate?>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-DD HH:mm:ss Z", Locale.US)

    @Synchronized
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): BunProDate? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull()
        }

        val string = reader.nextString()
        return parse(string)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: BunProDate?) {
        if (value == null) {
            writer.nullValue()
        } else {
            val string = format(value)
            writer.value(string)
        }
    }

    private fun parse(value: String): BunProDate {
        val date = dateFormat.parse(value)
        return BunProDate(date)
    }

    private fun format(value: BunProDate): String {
        return dateFormat.format(value.date)
    }
}
