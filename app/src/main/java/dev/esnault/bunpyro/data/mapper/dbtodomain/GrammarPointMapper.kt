package dev.esnault.bunpyro.data.mapper.dbtodomain

import dev.esnault.bunpyro.data.db.grammarpoint.FullGrammarPointDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint


class GrammarPointMapper : IMapper<FullGrammarPointDb, GrammarPoint> {

    private val sentenceMapper = ExampleSentenceMapper()
    private val linkMapper = SupplementalLinkMapper()

    override fun map(o: FullGrammarPointDb): GrammarPoint {
        return GrammarPoint(
            id = o.point.id,
            title = o.point.title,
            yomikata = o.point.yomikata,
            meaning = o.point.meaning,
            caution = o.point.caution,
            structure = o.point.structure,
            level = o.point.level,
            lesson = o.point.lesson,
            nuance = o.point.nuance,
            incomplete = o.point.incomplete,
            // The sentences are sorted here, because there is no clean way to do this
            // using room relationships
            sentences = sentenceMapper.map(o.sentences.sortedBy { it.order }),
            links = linkMapper.map(o.links)
        )
    }
}
