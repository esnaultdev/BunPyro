package dev.esnault.bunpyro.data.utils


data class DataUpdate<T, K>(
    val toInsert: List<T>,
    val toUpdate: List<T>,
    val toDelete: List<K>
) {
    companion object // enable extension functions on the companion
}

fun <T, K> DataUpdate.Companion.fromLocalIds(
    localIds: List<K>,
    data: List<T>,
    dataId: (data: T) -> K
): DataUpdate<T, K> {
    // Return quickly if we don't have any local data
    // This means that we have to insert everything
    if (localIds.isEmpty()) {
        return DataUpdate(
            toInsert = data,
            toUpdate = emptyList(),
            toDelete = emptyList()
        )
    }

    // Return quickly if we don't have any new data
    // This means that we have to delete everything
    if (data.isEmpty()) {
        return DataUpdate(
            toInsert = emptyList(),
            toUpdate = emptyList(),
            toDelete = localIds
        )
    }

    val localIdsSet = localIds.toSet()

    val (toUpdate, toInsert) = data.partition { localIdsSet.contains(dataId(it)) }
    val toUpdateIdsSet = toUpdate.map(dataId).toSet()
    val toDelete = (localIdsSet - toUpdateIdsSet).toList()

    return DataUpdate(
        toInsert = toInsert,
        toUpdate = toUpdate,
        toDelete = toDelete
    )
}
