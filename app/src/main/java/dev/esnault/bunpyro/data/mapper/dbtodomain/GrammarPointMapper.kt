package dev.esnault.bunpyro.data.mapper.dbtodomain

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.GrammarPoint


class GrammarPointMapper : IMapper<GrammarPointDb, GrammarPoint> {

    override fun map(o: GrammarPointDb): GrammarPoint {
        return GrammarPoint(
            id = o.id,
            title = o.title,
            yomikata = o.yomikata,
            meaning = o.meaning,
            caution = o.caution,
            structure = o.structure,
            level = o.level,
            lesson = o.lesson,
            nuance = o.nuance,
            incomplete = o.incomplete,
            order = o.order
        )
    }
}
