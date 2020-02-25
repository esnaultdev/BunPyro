package dev.esnault.bunpyro.data.mapper

interface IMapper<T, R> {
    fun map(o: T): R
    fun map(o: List<T>): List<R> = o.map { map(it) }
}
