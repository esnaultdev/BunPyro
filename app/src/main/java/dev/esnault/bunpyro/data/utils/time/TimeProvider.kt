package dev.esnault.bunpyro.data.utils.time

import java.util.*


class TimeProvider : ITimeProvider {

    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun currentDate(): Date {
        return Date()
    }
}
