package dev.esnault.bunpyro.di

import dev.esnault.bunpyro.domain.service.review.IReviewSessionService
import dev.esnault.bunpyro.domain.service.review.ReviewSessionService
import org.koin.dsl.module


val domainModule = module {

    single<IReviewSessionService> { ReviewSessionService(get()) }
}
