package dev.esnault.bunpyro.domain.utils


val canBecomeKanaRegex = Regex("""[a-zA-Z]+""")
val isHiraganaRegex = Regex("""\p{Hiragana}+""")
val isKanaRegex = Regex("""(\p{Hiragana}+|\p{Katakana}+)""")
