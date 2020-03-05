package dev.esnault.bunpyro.data.mapper.apitodb

import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.network.entities.ExampleSentence


class ExampleSentenceMapper : IMapper<ExampleSentence, ExampleSentenceDb> {

    override fun map(o: ExampleSentence): ExampleSentenceDb {
        return ExampleSentenceDb(
            id = o.id,
            grammarId = o.attributes.grammarId,
            japanese = o.attributes.japanese,
            english = o.attributes.english,
            nuance = o.attributes.nuance,
            audioLink = o.attributes.audioLink,
            order = o.attributes.order
        )
    }
}
