package fr.acart.melojj.data

import org.koin.dsl.module

val dataModule = module {
    // Add your data dependencies here
    single { SpotifySdkRepository(get()) }
}