package dev.esnault.bunpyro.data.mapper.apitodb

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.network.entities.GrammarPoint


class GrammarPointMapper : IMapper<GrammarPoint, GrammarPointDb> {

    override fun map(o: GrammarPoint): GrammarPointDb {
        return GrammarPointDb(
            id = o.id,
            title = o.attributes.title,
            yomikata = o.attributes.yomikata,
            meaning = o.attributes.meaning,
            caution = o.attributes.caution,
            structure = o.attributes.structure,
            level = o.attributes.level,
            lesson = o.attributes.lesson,
            nuance = o.attributes.nuance,
            incomplete = o.attributes.incomplete,
            order = o.attributes.grammarOrder ?: 0
        )
    }
}
