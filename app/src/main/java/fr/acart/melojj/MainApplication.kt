package fr.acart.melojj

import android.app.Application
import fr.acart.melojj.data.SpotifyAppRemoteProvider
import fr.acart.melojj.data.dataModule
import fr.acart.melojj.domain.domainModule
import fr.acart.melojj.ui.uiModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application() {

    private lateinit var koinApp: KoinApplication
    override fun onCreate() {
        super.onCreate()

        koinApp = startKoin {
            modules(
                module {
                    single { SpotifyAppRemoteProvider(this@MainApplication) }
                },
                dataModule,
                domainModule,
                uiModule,
            )
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        koinApp.koin.get<SpotifyAppRemoteProvider>().close()
    }
}