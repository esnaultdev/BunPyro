package dev.esnault.bunpyro.data.mapper.dbtodomain

import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.grammar.ExampleSentence


class ExampleSentenceMapper : IMapper<ExampleSentenceDb, ExampleSentence> {

    override fun map(o: ExampleSentenceDb): ExampleSentence {
        return ExampleSentence(
            id = o.id,
            japanese = o.japanese.trim(),
            english = o.english.trim(),
            nuance = o.nuance?.trim(),
            audioLink = o.audioLink?.takeIf { it.isNotBlank() }
        )
    }
}
