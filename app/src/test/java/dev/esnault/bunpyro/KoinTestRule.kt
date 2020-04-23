package dev.esnault.bunpyro

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration


class KoinTestRule private constructor(val appDeclaration: KoinAppDeclaration): TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {

            override fun evaluate() {
                startKoin(appDeclaration)
                base.evaluate()
                stopKoin()
            }
        }
    }

    companion object {
        fun create(appDeclaration: KoinAppDeclaration) = KoinTestRule(appDeclaration)
    }
}
