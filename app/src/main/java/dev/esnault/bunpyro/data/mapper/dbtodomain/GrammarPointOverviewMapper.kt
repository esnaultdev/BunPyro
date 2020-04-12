package dev.esnault.bunpyro.data.mapper.dbtodomain

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointOverviewDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


class GrammarPointOverviewMapper : IMapper<GrammarPointOverviewDb, GrammarPointOverview> {

    override fun map(o: GrammarPointOverviewDb): GrammarPointOverview {
        return GrammarPointOverview(
            id = o.id,
            title = o.title,
            meaning = o.meaning,
            srsLevel = o.srsLevel ?: (if (o.studied) 0 else null),
            incomplete = o.incomplete
        )
    }
}
