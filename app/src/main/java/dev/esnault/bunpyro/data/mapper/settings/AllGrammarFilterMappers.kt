package dev.esnault.bunpyro.data.mapper.settings

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter

private val moshi: Moshi by lazy(LazyThreadSafetyMode.NONE) {
    Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

class AllGrammarFilterFromStringMapper : IMapper<String?, AllGrammarFilter> {

    override fun map(o: String?): AllGrammarFilter {
        if (o == null) return AllGrammarFilter.DEFAULT

        val jsoned = moshi.adapter(AllGrammarFilterJson::class.java).fromJson(o)
        if (jsoned == null) return AllGrammarFilter.DEFAULT

        return AllGrammarFilter(jsoned.jlpt.mapTo(mutableSetOf()) { level -> JLPT[level] })
    }
}

class AllGrammarFilterToStringMapper : IMapper<AllGrammarFilter, String> {

    override fun map(o: AllGrammarFilter): String {
        val jsonable = AllGrammarFilterJson(o.jlpt.map { it.level })
        return moshi.adapter(AllGrammarFilterJson::class.java).toJson(jsonable)
    }
}

/**
 * Alternate version of [AllGrammarFilter] made for easier JSON parsing.
 */
private data class AllGrammarFilterJson(
    val jlpt: List<Int>
)