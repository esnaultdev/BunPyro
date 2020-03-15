package dev.esnault.bunpyro.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.esnault.bunpyro.R

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            // Load the custom SQLite library with its tokenizers
            System.loadLibrary("sqliteX")
            System.loadLibrary("tokenizers")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }
}
