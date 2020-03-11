package dev.esnault.bunpyro.di

import dev.esnault.bunpyro.data.repository.apikey.ApiKeyRepository
import dev.esnault.bunpyro.data.repository.apikey.IApiKeyRepository
import dev.esnault.bunpyro.data.repository.grammarpoint.GrammarPointRepository
import dev.esnault.bunpyro.data.repository.grammarpoint.IGrammarPointRepository
import dev.esnault.bunpyro.data.repository.lesson.ILessonRepository
import dev.esnault.bunpyro.data.repository.lesson.LessonRepository
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository
import dev.esnault.bunpyro.data.repository.sync.SyncRepository
import org.koin.dsl.module


val repoModule = module {

    single<IApiKeyRepository> {
        ApiKeyRepository(get(), get())
    }

    single<ILessonRepository> {
        LessonRepository(get())
    }

    single<IGrammarPointRepository> {
        GrammarPointRepository(get())
    }

    single<ISyncRepository> {
        SyncRepository(get())
    }
}
