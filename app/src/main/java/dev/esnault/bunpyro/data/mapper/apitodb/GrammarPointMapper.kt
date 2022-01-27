package dev.esnault.bunpyro.data.mapper.apitodb

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.mapper.INullableMapper
import dev.esnault.bunpyro.data.network.entities.GrammarPoint


class GrammarPointMapper : INullableMapper<GrammarPoint, GrammarPointDb> {

    override fun map(o: GrammarPoint): GrammarPointDb? {
        if (o.id == null ||
            o.attributes == null ||
            o.attributes.title == null ||
            o.attributes.yomikata == null ||
            o.attributes.meaning == null ||
            o.attributes.lesson == null ||
            o.attributes.incomplete == null
        ) return null

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
