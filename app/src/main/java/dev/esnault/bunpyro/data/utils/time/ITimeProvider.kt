package dev.esnault.bunpyro.data.utils.time

import java.util.*


interface ITimeProvider {

    fun currentTimeMillis(): Long

    fun currentDate(): Date
}
