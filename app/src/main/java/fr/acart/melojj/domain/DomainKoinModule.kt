package fr.acart.melojj.domain

import org.koin.dsl.module

val domainModule = module {
    // Add your domain dependencies here
    single { IsSdkConnectedUseCase.init(get()) }
    single { PlayPauseUseCase.init(get()) }
}