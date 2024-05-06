package fr.acart.melojj

import android.app.Application
import fr.acart.melojj.data.dataModule
import fr.acart.melojj.domain.domainModule
import fr.acart.melojj.ui.uiModule
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(dataModule, uiModule, domainModule)
        }
    }
}