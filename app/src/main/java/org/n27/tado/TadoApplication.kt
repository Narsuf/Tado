package org.n27.tado

import android.app.Application
import org.n27.tado.injection.AppComponent
import org.n27.tado.injection.AppModule
import org.n27.tado.injection.DaggerAppComponent
import timber.log.Timber

class TadoApplication : Application() {

    val appComponent: AppComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()

    override fun onCreate() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        super.onCreate()
    }
}
