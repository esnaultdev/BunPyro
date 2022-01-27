package dev.esnault.bunpyro.data.mapper.apitodb

import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb
import dev.esnault.bunpyro.data.mapper.INullableMapper
import dev.esnault.bunpyro.data.network.entities.SupplementalLink


class SupplementalLinkMapper : INullableMapper<SupplementalLink, SupplementalLinkDb> {

    override fun map(o: SupplementalLink): SupplementalLinkDb? {
        if (o.id == null ||
            o.attributes == null ||
            o.attributes.grammarId == null ||
            o.attributes.site == null ||
            o.attributes.link == null ||
            o.attributes.description == null
        ) return null

        return SupplementalLinkDb(
            id = o.id,
            grammarId = o.attributes.grammarId,
            site = o.attributes.site,
            link = o.attributes.link,
            description = o.attributes.description
        )
    }
}
