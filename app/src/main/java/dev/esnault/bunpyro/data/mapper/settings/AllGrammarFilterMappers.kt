package dev.esnault.bunpyro.data.mapper.settings

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import dev.esnault.bunpyro.domain.utils.lazyNone

private val moshi: Moshi by lazyNone {
    Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

class AllGrammarFilterFromStringMapper : IMapper<String?, AllGrammarFilter> {

    override fun map(o: String?): AllGrammarFilter {
        if (o == null) return AllGrammarFilter.DEFAULT

        val jsoned = moshi.adapter(AllGrammarFilterJson::class.java).fromJson(o)
        if (jsoned == null) return AllGrammarFilter.DEFAULT

        return AllGrammarFilter(
            jlpt = jsoned.jlpt.mapTo(mutableSetOf()) { level -> JLPT[level] },
            studied = jsoned.studied ?: true,
            nonStudied = jsoned.nonStudied ?: true
        )
    }
}

class AllGrammarFilterToStringMapper : IMapper<AllGrammarFilter, String> {

    override fun map(o: AllGrammarFilter): String {
        val jsonable = AllGrammarFilterJson(o.jlpt.map { it.level }, o.studied, o.nonStudied)
        return moshi.adapter(AllGrammarFilterJson::class.java).toJson(jsonable)
    }
}

/**
 * Alternate version of [AllGrammarFilter] made for easier JSON parsing.
 */
private data class AllGrammarFilterJson(
    val jlpt: List<Int>,
    val studied: Boolean?,
    val nonStudied: Boolean?
)
