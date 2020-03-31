package dev.esnault.bunpyro.data.db


/** Load the custom SQLite library with its tokenizers */
fun loadCustomSQLite() {
    System.loadLibrary("sqliteX")
    System.loadLibrary("tokenizers")
}
