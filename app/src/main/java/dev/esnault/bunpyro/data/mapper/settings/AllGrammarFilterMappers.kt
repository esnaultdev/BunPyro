package dev.esnault.bunpyro.data.mapper.settings

import androidx.annotation.Keep
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.mapper.manualMappingMoshi
import dev.esnault.bunpyro.data.utils.extension.fromJsonResult
import dev.esnault.bunpyro.data.utils.extension.toJsonResult
import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter


private val moshi = manualMappingMoshi

class AllGrammarFilterFromStringMapper : IMapper<String?, AllGrammarFilter> {

    override fun map(o: String?): AllGrammarFilter {
        if (o == null) return AllGrammarFilter.DEFAULT

        val adapter = moshi.adapter(AllGrammarFilterJson::class.java)
        val jsoned = adapter.fromJsonResult(o)
            .getOrNull()
            ?: return AllGrammarFilter.DEFAULT

        return AllGrammarFilter(
            jlpt = jsoned.jlpt.mapTo(mutableSetOf()) { level -> JLPT[level] },
            studied = jsoned.studied ?: true,
            nonStudied = jsoned.nonStudied ?: true
        )
    }
}

class AllGrammarFilterToStringMapper : IMapper<AllGrammarFilter, String?> {

    override fun map(o: AllGrammarFilter): String? {
        val jsonable = AllGrammarFilterJson(o.jlpt.map { it.level }, o.studied, o.nonStudied)
        val adapter = moshi.adapter(AllGrammarFilterJson::class.java)
        return adapter.toJsonResult(jsonable).getOrNull()
    }
}

/**
 * Alternate version of [AllGrammarFilter] made for easier JSON parsing.
 */
@Keep
private data class AllGrammarFilterJson(
    val jlpt: List<Int>,
    val studied: Boolean?,
    val nonStudied: Boolean?
)
