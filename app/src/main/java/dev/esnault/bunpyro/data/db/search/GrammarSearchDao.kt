package dev.esnault.bunpyro.data.db.search

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction


@Dao
abstract class GrammarSearchDao {

    suspend fun searchByTerm(term: String): List<GrammarSearchResultDb> {
        return searchByTermImpl(term.matchString)
    }

    @Transaction
    @Query("""
SELECT gp.id, gp.lesson, gp.title, gp.yomikata, gp.meaning, gp.incomplete,
MAX(rv_h.streak) AS srsLevel, COUNT(rv.id) AS studied, 2 AS rank
FROM grammar_point AS gp
JOIN grammar_point_fts ON gp.id = grammar_point_fts.docid
AND grammar_point_fts MATCH :term
LEFT JOIN review AS rv ON rv.grammar_id = gp.id AND rv.type = 0
LEFT JOIN review_history AS rv_h ON rv_h.review_id == rv.id AND rv_h.review_type = 0
GROUP BY gp.id
ORDER BY gp.grammar_order
""")
    protected abstract suspend fun searchByTermImpl(term: String): List<GrammarSearchResultDb>

    suspend fun searchByTermWithKana(term: String, kana: String): List<GrammarSearchResultDb> {
        return searchByTermWithKanaImpl(term.matchString, kana.matchString)
    }

    @Transaction
    @Query("""
SELECT gp.id, gp.lesson, gp.title, gp.yomikata, gp.meaning, gp.incomplete,
MAX(rv_h.streak) AS srsLevel, COUNT(rv.id) AS studied, 1 AS rank, gp.grammar_order
FROM grammar_point AS gp
LEFT JOIN review AS rv ON rv.grammar_id = gp.id AND rv.type = 0
LEFT JOIN review_history AS rv_h ON rv_h.review_id == rv.id AND rv_h.review_type = 0
WHERE gp.id in
(SELECT docid FROM grammar_point_fts WHERE yomikata MATCH :kana
UNION
SELECT docid FROM grammar_point_fts WHERE title MATCH :kana)
GROUP BY gp.id
UNION
SELECT gp.id, gp.lesson, gp.title, gp.yomikata, gp.meaning, gp.incomplete,
MAX(rv_h.streak) AS srsLevel, COUNT(rv.id) AS studied, 2 AS rank, gp.grammar_order
FROM grammar_point AS gp
JOIN grammar_point_fts ON gp.id = grammar_point_fts.docid
AND grammar_point_fts.meaning MATCH :term
LEFT JOIN review AS rv ON rv.grammar_id = gp.id AND rv.type = 0
LEFT JOIN review_history AS rv_h ON rv_h.review_id == rv.id AND rv_h.review_type = 0
GROUP BY gp.id
ORDER BY gp.grammar_order
""")
    protected abstract suspend fun searchByTermWithKanaImpl(term: String, kana: String): List<GrammarSearchResultDb>
}

private inline val String.matchString: String
    get() = "\"${this.escaped()}\""

/**
 * According to https://www.sqlite.org/fts3.html
 * > The option value may optionally be enclosed in single or double quotes, with embedded quote
 * > characters escaped in the same way as for SQL literals.
 */
private fun String.escaped(): String {
    return this.replace("\"", "\"\"")
}
