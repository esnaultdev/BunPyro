package dev.esnault.bunpyro.common


fun Long?.after(source: Long, delay: Long): Boolean {
    if (this == null) return true
    return source + delay <= this
}
