package dev.esnault.bunpyro.data.mapper.apitodb

import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.network.entities.SupplementalLink


class SupplementalLinkMapper : IMapper<SupplementalLink, SupplementalLinkDb> {

    override fun map(o: SupplementalLink): SupplementalLinkDb {
        return SupplementalLinkDb(
            id = o.id,
            grammarId = o.attributes.grammarId,
            site = o.attributes.site,
            link = o.attributes.link,
            description = o.attributes.description
        )
    }
}
