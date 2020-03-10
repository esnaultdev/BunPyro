package dev.esnault.bunpyro.data.network.entities

import java.util.*


/**
 * Date representation used by the reviews history.
 * For some reason, this is formatted as such in the API: "2019-12-08 18:00:00 +0000".
 *
 * Since we need a custom Date adapter for specific fields only, but still want a [Date] in the
 * end, we have to use a wrapper around it.
 */
data class BunProDate(val date: Date)
