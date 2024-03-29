package dev.esnault.bunpyro.data.mapper.apitodb

import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.mapper.INullableMapper
import dev.esnault.bunpyro.data.network.entities.ExampleSentence


class ExampleSentenceMapper : INullableMapper<ExampleSentence, ExampleSentenceDb> {

    override fun map(o: ExampleSentence): ExampleSentenceDb? {
        if (o.id == null ||
            o.attributes == null ||
            o.attributes.grammarId == null ||
            o.attributes.japanese == null ||
            o.attributes.english == null
        ) return null

        return ExampleSentenceDb(
            id = o.id,
            grammarId = o.attributes.grammarId,
            japanese = o.attributes.japanese,
            english = o.attributes.english,
            nuance = o.attributes.nuance,
            audioLink = o.attributes.audioLink,
            order = o.attributes.order ?: 0
        )
    }
}
