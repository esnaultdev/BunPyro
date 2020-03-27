package dev.esnault.bunpyro.data.mapper.dbtodomain.search

import dev.esnault.bunpyro.data.db.search.GrammarSearchResultDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


class GrammarSearchResultMapper : IMapper<GrammarSearchResultDb, GrammarPointOverview> {

    override fun map(o: GrammarSearchResultDb): GrammarPointOverview {
        return GrammarPointOverview(
            id = o.id,
            title = o.title,
            meaning = o.meaning,
            studied = o.studied,
            incomplete = o.incomplete
        )
    }
}
