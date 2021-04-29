package dev.esnault.bunpyro.data.mapper.dbtodomain

import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.grammar.SupplementalLink


class SupplementalLinkMapper : IMapper<SupplementalLinkDb, SupplementalLink> {

    override fun map(o: SupplementalLinkDb): SupplementalLink {
        return SupplementalLink(
            id = o.id,
            site = o.site,
            link = o.link,
            description = o.description.trim()
        )
    }
}
