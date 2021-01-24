package dev.esnault.bunpyro.android.screen.settings.licenses


data class License(
    val title: String,
    val summary: String,
    val license: String,
    val url: String
)

val licenses = listOf(
    License(
        title = "wanakana-kt",
        summary = "Kotlin port of WanaKana, a library for detecting and transliterating Hiragana <--> Katakana <--> Romaji.",
        license = "MIT License",
        url = "https://github.com/esnaultdev/wanakana-kt"
    ),
    License(
        title = "SQLite_Custom",
        summary = "Custom SQLite build with specific tokenizers.",
        license = "Apache License 2.0",
        url = "https://github.com/JeremiahStephenson/SQLite_Custom"
    ),
    License(
        title = "SqliteSubstringSearch",
        summary = "An open source tokenizer which supports fast substring search with sqlite FTS (full text search).",
        license = "MIT License",
        url = "https://github.com/haifengkao/SqliteSubstringSearch"
    ),
    License(
        title = "Material icons",
        summary = "Material design iconography.",
        license = "Apache License 2.0",
        url = "https://material.io/resources/icons/"
    ),
    License(
        title = "JUnit",
        summary = "JVM testing framework.",
        license = "Eclipse Public License 1.0",
        url = "https://junit.org/junit5/"
    ),
    License(
        title = "JSoup",
        summary = "Java library for working with real-world HTML.",
        license = "MIT License",
        url = "https://jsoup.org/"
    ),
    License(
        title = "OkHttp",
        summary = "Square’s meticulous HTTP client for Java and Kotlin.",
        license = "Apache License 2.0",
        url = "https://square.github.io/okhttp/"
    ),
    License(
        title = "Moshi",
        summary = "A modern JSON library for Android and Java.",
        license = "Apache License 2.0",
        url = "https://github.com/square/moshi"
    ),
    License(
        title = "Retrofit",
        summary = "Type-safe HTTP client for Android and Java by Square, Inc.",
        license = "Apache License 2.0",
        url = "https://github.com/square/retrofit/"
    ),
    License(
        title = "Koin",
        summary = "A pragmatic lightweight dependency injection framework for Kotlin developers.",
        license = "Apache License 2.0",
        url = "https://insert-koin.io/"
    ),
    License(
        title = "Material Components Android",
        summary = "Modular and customizable Material Design UI components for Android.",
        license = "Apache License 2.0",
        url = "https://github.com/material-components/material-components-android"
    ),
    License(
        title = "BetterLinkMovementMethod",
        summary = "A custom LinkMovementMethod for TextView that attempts to improve how clickable links are detected, highlighted and handled.",
        license = "Apache License 2.0",
        url = "https://github.com/saket/Better-Link-Movement-Method"
    ),
    License(
        title = "Transitions Everywhere",
        summary = "Set of extra Transitions on top of AndroidX Transitions Library.",
        license = "Apache License 2.0",
        url = "https://github.com/andkulikov/Transitions-Everywhere"
    )
).sortedBy { it.title }
