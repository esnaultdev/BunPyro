package dev.esnault.bunpyro.data.utils.time


class TimeProvider : ITimeProvider {

    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}
